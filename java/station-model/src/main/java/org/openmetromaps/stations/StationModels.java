// Copyright 2018 Sebastian Kuerten
//
// This file is part of OpenMetroMaps.
//
// OpenMetroMaps is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// OpenMetroMaps is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with OpenMetroMaps. If not, see <http://www.gnu.org/licenses/>.

package org.openmetromaps.stations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.rawstations.RawStationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.collections.util.ListUtil;

public class StationModels
{

	final static Logger logger = LoggerFactory.getLogger(StationModels.class);

	public static StationModel derive(ModelData modelData,
			RawStationModel rawModel)
	{
		List<Change> changes = new ArrayList<>();
		List<Exit> exits = new ArrayList<>();

		List<Line> lines = modelData.lines;

		for (org.openmetromaps.rawstations.Change change : rawModel
				.getChanges()) {
			convert(changes, change, lines);
		}

		for (org.openmetromaps.rawstations.Exit exit : rawModel.getExits()) {
			convert(exits, exit);
		}

		return new StationModel(changes, exits);
	}

	private static void convert(List<Change> changes,
			org.openmetromaps.rawstations.Change raw, List<Line> lines)
	{
		Matcher matcher = null;
		if (raw.getChangeLine() != null) {
			if (raw.getChangeTowards() == null) {
				matcher = new SimpleMatcher(raw.getChangeLine());
			} else {
				matcher = new LineTowardsMatcher(raw.getChangeLine(),
						raw.getChangeTowards());
			}
		} else if (raw.getChangeLineRegex() != null) {
			matcher = new RegexMatcher(raw.getChangeLineRegex());
		}
		Location location = convert(raw.getLocation());
		Change change = new Change(raw.getLine(), raw.getTowards(), raw.getAt(),
				location, matcher);
		changes.add(change);
		if (raw.isDeriveReverseFrom()) {
			String reverseLine = raw.getReverseLine() != null
					? raw.getReverseLine() : raw.getLine();
			String reverseTowards = raw.getReverseTowards() != null
					? raw.getReverseTowards()
					: reverse(lines, raw.getLine(), raw.getTowards());
			logger.debug(String.format(
					"Determine reverse for line '%s' towards '%s': '%s' towards '%s'",
					raw.getLine(), raw.getTowards(), reverseLine,
					reverseTowards));

			Change reverse = new Change(reverseLine, reverseTowards,
					raw.getAt(), reverse(location), matcher);
			changes.add(reverse);
		}
	}

	private static String reverse(List<Line> lines, String lineName,
			String towards)
	{
		Line line = StationUtil.findLine(lines, lineName);
		if (line == null) {
			return null;
		}
		Stop first = line.getStops().get(0);
		Stop last = ListUtil.last(line.getStops());
		if (first.getStation().getName().equals(towards)) {
			return last.getStation().getName();
		} else if (last.getStation().getName().equals(towards)) {
			return first.getStation().getName();
		}
		return null;
	}

	private static void convert(List<Exit> exits,
			org.openmetromaps.rawstations.Exit raw)
	{
		Exit exit = new Exit();
		exits.add(exit);
	}

	private static Location convert(
			org.openmetromaps.rawstations.Location location)
	{
		if (location == null) {
			return null;
		}
		switch (location) {
		case FRONT:
			return Location.FRONT;
		case ALMOST_FRONT:
			return Location.ALMOST_FRONT;
		case MIDDLE_MIDDLE_FRONT:
			return Location.MIDDLE_MIDDLE_FRONT;
		case MIDDLE:
			return Location.MIDDLE;
		case MIDDLE_MIDDLE_BACK:
			return Location.MIDDLE_MIDDLE_BACK;
		case ALMOST_BACK:
			return Location.ALMOST_BACK;
		case BACK:
			return Location.BACK;
		}
		return null;
	}

	private static Location reverse(Location location)
	{
		if (location == null) {
			return null;
		}
		switch (location) {
		case FRONT:
			return Location.BACK;
		case ALMOST_FRONT:
			return Location.ALMOST_BACK;
		case MIDDLE_MIDDLE_FRONT:
			return Location.MIDDLE_MIDDLE_BACK;
		case MIDDLE:
			return Location.MIDDLE;
		case MIDDLE_MIDDLE_BACK:
			return Location.MIDDLE_MIDDLE_FRONT;
		case ALMOST_BACK:
			return Location.ALMOST_FRONT;
		case BACK:
			return Location.FRONT;
		}
		return null;
	}

	public static List<LineWithOrientation> match(Matcher matcher,
			Collection<Line> lines)
	{
		List<LineWithOrientation> results = new ArrayList<>();
		for (Line line : lines) {
			addMatches(results, matcher, line);
		}
		return results;
	}

	public static void addMatches(List<LineWithOrientation> results,
			Matcher matcher, Line line)
	{
		if (matcher instanceof SimpleMatcher) {
			SimpleMatcher sm = (SimpleMatcher) matcher;
			if (sm.getName().equals(line.getName())) {
				results.add(new LineWithOrientation(line, false));
				if (!line.isCircular()) {
					results.add(new LineWithOrientation(line, true));
				}
			}
		} else if (matcher instanceof LineTowardsMatcher) {
			LineTowardsMatcher ltm = (LineTowardsMatcher) matcher;
			if (ltm.getName().equals(line.getName())) {
				List<Stop> stops = line.getStops();
				Stop first = stops.get(0);
				Stop last = ListUtil.last(stops);

				boolean valid = false;
				boolean reverse = false;
				if (last.getStation().getName().equals(ltm.getTowards())) {
					reverse = false;
					valid = true;
				} else if (first.getStation().getName()
						.equals(ltm.getTowards())) {
					reverse = true;
					valid = true;
				}

				if (valid) {
					results.add(new LineWithOrientation(line, reverse));
				} else {
					logger.warn(String.format(
							"Unable to find towards value '%s' on line '%s', having '%s' and '%s'",
							ltm.getTowards(), line.getName(),
							first.getStation().getName(),
							last.getStation().getName()));
				}
			}
		} else if (matcher instanceof RegexMatcher) {
			RegexMatcher rm = (RegexMatcher) matcher;
			Pattern pattern = Pattern.compile(rm.getPattern());
			java.util.regex.Matcher m = pattern.matcher(line.getName());
			if (m.matches()) {
				results.add(new LineWithOrientation(line, false));
				if (!line.isCircular()) {
					results.add(new LineWithOrientation(line, true));
				}
			}

		}
	}

}
