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

package org.openmetromaps.change;

import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.rawchange.RawChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.collections.util.ListUtil;

public class ChangeModels
{

	final static Logger logger = LoggerFactory.getLogger(ChangeModels.class);

	public static ChangeModel derive(ModelData modelData,
			RawChangeModel rawModel)
	{
		List<Change> changes = new ArrayList<>();
		List<Exit> exits = new ArrayList<>();

		List<Line> lines = modelData.lines;

		for (org.openmetromaps.rawchange.Change change : rawModel
				.getChanges()) {
			convert(changes, change, lines);
		}

		for (org.openmetromaps.rawchange.Exit exit : rawModel.getExits()) {
			convert(exits, exit);
		}

		return new ChangeModel(changes, exits);
	}

	private static void convert(List<Change> changes,
			org.openmetromaps.rawchange.Change raw, List<Line> lines)
	{
		Matcher matcher = null;
		if (raw.getChangeLine() != null) {
			matcher = new SimpleMatcher(raw.getChangeLine());
		} else if (raw.getChangeLineRegex() != null) {
			matcher = new RegexMatcher(raw.getChangeLineRegex());
		}
		Location location = convert(raw.getLocation());
		Change change = new Change(raw.getLine(), raw.getTowards(), raw.getAt(),
				location, matcher);
		changes.add(change);
		if (raw.isDeriveReverse()) {
			String reverseTowards = reverse(lines, raw.getLine(),
					raw.getTowards());
			logger.debug(String.format(
					"Determine reverse for line '%s' towards '%s': '%s'",
					raw.getLine(), raw.getTowards(), reverseTowards));
			Change reverse = new Change(raw.getLine(), reverseTowards,
					raw.getAt(), reverse(location), matcher);
			changes.add(reverse);
		}
	}

	private static String reverse(List<Line> lines, String lineName,
			String towards)
	{
		Line line = ChangeUtil.findLine(lines, lineName);
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
			org.openmetromaps.rawchange.Exit raw)
	{
		Exit exit = new Exit();
		exits.add(exit);
	}

	private static Location convert(
			org.openmetromaps.rawchange.Location location)
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

}
