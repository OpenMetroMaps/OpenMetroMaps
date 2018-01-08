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

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.TestData;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.rawchange.RawChangeModel;
import org.openmetromaps.rawchange.xml.DesktopXmlChangeReader;

import com.google.common.base.Joiner;

import de.topobyte.collections.util.ListUtil;
import de.topobyte.xml.domabstraction.iface.ParsingException;

/**
 * This test converts our change model to the CSV format specifed and used in
 * this repository: <a href=
 * "https://github.com/juliuste/vbb-change-positions">juliuste/vbb-change-positions</a>.
 */
public class TestConvertChangeModelBerlin
{

	public static void main(String[] args)
			throws ParserConfigurationException, IOException, ParsingException
	{
		XmlModel xmlModel = TestData.berlinXml();
		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel mapModel = modelConverter.convert(xmlModel);

		LineNetworkBuilder builder = new LineNetworkBuilder(mapModel.getData(),
				MapModelUtil.allEdges(mapModel));
		LineNetwork lineNetwork = builder.getGraph();

		InputStream input = TestConvertChangeModelBerlin.class.getClassLoader()
				.getResourceAsStream("berlin-changes.xml");
		RawChangeModel rawModel = DesktopXmlChangeReader.read(input);
		ChangeModel model = ChangeModels.derive(mapModel.getData(), rawModel);

		for (Change change : model.getChanges()) {
			print(change, mapModel, lineNetwork);
		}
	}

	private static void print(Change change, MapModel mapModel,
			LineNetwork lineNetwork)
	{
		List<Line> lines = mapModel.getData().lines;
		List<Station> stations = mapModel.getData().stations;

		Station station = findStation(stations, change.getAt());

		Node node = lineNetwork.getStationToNode().get(station);
		Set<Line> linesAtStation = getLines(node);

		Line lineFrom = ChangeUtil.findLine(lines, change.getLine());
		List<Line> matchingLines = match(change.getMatcher(), linesAtStation);
		for (Line lineTo : matchingLines) {
			Stop first = lineTo.getStops().get(0);
			Stop last = ListUtil.last(lineTo.getStops());
			// TODO: currently, we simply assume that the change entry applied
			// for both directions of the target line. This is not necessarily
			// true, e.g. when changing from S41 to U9 at Bundesplatz. Our model
			// needs to support matching of target towards and an additional
			// 'derive-to-reverse'.
			if (!first.getStation().getName().equals(change.getAt())) {
				print(lineFrom, lineTo, change, true);
			}
			if (!last.getStation().getName().equals(change.getAt())) {
				print(lineFrom, lineTo, change, false);
			}
		}
	}

	private static void print(Line lineFrom, Line lineTo, Change change,
			boolean toReverse)
	{
		// Example output:
		//
		// 900000024201,Bismarckstraße,
		// U7,
		// 900000022202,Richard-Wagner-Platz,,0.2,
		// U2,
		// 900000022101,Sophie-Charlotte-Platz,,0.5
		// false"

		String fromTowards = change.getTowards();
		List<Stop> fromStops = lineFrom.getStops();
		int fromIndex = findStop(fromStops, change.getAt());
		if (fromIndex < 0) {
			throw new IllegalArgumentException(
					"change station not found on from line");
		}

		boolean fromReverse;
		Stop first = lineFrom.getStops().get(0);
		Stop last = ListUtil.last(lineFrom.getStops());
		if (first.getStation().getName().equals(fromTowards)) {
			fromReverse = true;
		} else if (last.getStation().getName().equals(fromTowards)) {
			fromReverse = false;
		} else {
			throw new IllegalArgumentException(String.format(
					"change's towards value does not match the line."
							+ " Looking for '%s', having '%s' and '%s'",
					fromTowards, first.getStation().getName(),
					last.getStation().getName()));
		}

		int before = fromReverse ? fromIndex + 1 : fromIndex - 1;
		Stop fromBefore = fromStops.get(before);

		List<Stop> toStops = lineTo.getStops();
		int toIndex = findStop(toStops, change.getAt());
		if (toIndex < 0) {
			throw new IllegalArgumentException(
					"change station not found on to line");
		}

		int after = toReverse ? toIndex - 1 : toIndex + 1;
		Stop toAfter = toStops.get(after);

		DecimalFormat df = (DecimalFormat) NumberFormat
				.getNumberInstance(Locale.US);
		df.setMaximumFractionDigits(3);

		Location fromLocation = change.getLocation();
		String valueFromPosition;
		if (fromLocation == null) {
			valueFromPosition = "";
		} else {
			double fromPos = position(fromLocation);
			valueFromPosition = df.format(fromPos);
		}

		boolean isSamePlatform = false;

		// TODO: retrieve <ID> values somehow
		String station = "<ID>";
		String stationName = change.getAt();
		String fromLine = lineFrom.getName();
		String fromStation = "<ID>";
		String fromStationName = fromBefore.getStation().getName();
		String fromTrack = ""; // TODO: not supported by our format
		String fromPosition = valueFromPosition;
		String toLine = lineTo.getName();
		String toStation = "<ID>";
		String toStationName = toAfter.getStation().getName();
		String toTrack = ""; // TODO: not supported by our format
		String toPosition = ""; // TODO: not supported by our format
		String samePlatform = Boolean.toString(isSamePlatform);

		List<String> values = Arrays.asList(station, stationName, fromLine,
				fromStation, fromStationName, fromTrack, fromPosition, toLine,
				toStation, toStationName, toTrack, toPosition, samePlatform);

		String line = Joiner.on(",").join(values);
		System.out.println(line);
	}

	// TODO: all methods below should be moved to some utility class
	private static double position(Location location)
	{
		switch (location) {
		case FRONT:
			return 1;
		case ALMOST_FRONT:
			return 0.833;
		case MIDDLE_MIDDLE_FRONT:
			return 0.666;
		case MIDDLE:
			return 0.5;
		case MIDDLE_MIDDLE_BACK:
			return 0.333;
		case ALMOST_BACK:
			return 0.167;
		case BACK:
			return 0;
		default:
			return 0.5;
		}
	}

	private static List<Line> match(Matcher matcher, Collection<Line> lines)
	{
		List<Line> results = new ArrayList<>();
		for (Line line : lines) {
			if (matches(matcher, line)) {
				results.add(line);
			}
		}
		return results;
	}

	private static boolean matches(Matcher matcher, Line line)
	{
		if (matcher instanceof SimpleMatcher) {
			SimpleMatcher sm = (SimpleMatcher) matcher;
			if (sm.getName().equals(line.getName())) {
				return true;
			}
		} else if (matcher instanceof RegexMatcher) {
			RegexMatcher rm = (RegexMatcher) matcher;
			Pattern pattern = Pattern.compile(rm.getPattern());
			java.util.regex.Matcher m = pattern.matcher(line.getName());
			if (m.matches()) {
				return true;
			}
		}
		return false;
	}

	private static Set<Line> getLines(Node node)
	{
		Set<Line> results = new HashSet<>();
		for (Edge edge : node.edges) {
			for (NetworkLine line : edge.lines) {
				results.add(line.line);
			}
		}
		return results;
	}

	private static Station findStation(List<Station> stations, String name)
	{
		for (Station station : stations) {
			if (station.getName().equals(name)) {
				return station;
			}
		}
		return null;
	}

	private static int findStop(List<Stop> stops, String stationName)
	{
		for (int i = 0; i < stops.size(); i++) {
			Stop stop = stops.get(i);
			if (stop.getStation().getName().equals(stationName)) {
				return i;
			}
		}
		return -1;
	}

}
