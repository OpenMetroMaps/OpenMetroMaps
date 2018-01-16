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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.stations.Change;
import org.openmetromaps.stations.StationModel;
import org.openmetromaps.stations.StationModels;
import org.openmetromaps.stations.StationUtil;
import org.openmetromaps.stations.LineWithOrientation;
import org.openmetromaps.stations.Location;

import com.google.common.base.Joiner;

import de.topobyte.collections.util.ListUtil;

/**
 * This class converts our change model to the CSV format specified and used in
 * this repository: <a href=
 * "https://github.com/juliuste/vbb-change-positions">juliuste/vbb-change-positions</a>.
 */
public class StationModelToCsvExporter
{

	private MapModel mapModel;
	private LineNetwork lineNetwork;
	private StationModel model;
	private Map<String, String> nameToId;

	public StationModelToCsvExporter(MapModel mapModel, LineNetwork lineNetwork,
			StationModel model, Map<String, String> nameToId)
	{
		this.mapModel = mapModel;
		this.lineNetwork = lineNetwork;
		this.model = model;
		this.nameToId = nameToId;
	}

	public void print()
	{
		for (Change change : model.getChanges()) {
			print(change, mapModel, lineNetwork);
		}
	}

	private void print(Change change, MapModel mapModel,
			LineNetwork lineNetwork)
	{
		List<Line> lines = mapModel.getData().lines;
		List<Station> stations = mapModel.getData().stations;

		Station station = MapModelUtil.findStation(stations, change.getAt());

		Node node = lineNetwork.getStationToNode().get(station);
		Set<Line> linesAtStation = LineNetworkUtil.getLines(node);

		Line lineFrom = StationUtil.findLine(lines, change.getLine());
		List<LineWithOrientation> matchingLines = StationModels
				.match(change.getMatcher(), linesAtStation);
		for (LineWithOrientation lineTo : matchingLines) {
			List<Stop> stopsTo = lineTo.getLine().getStops();
			Stop first = stopsTo.get(0);
			Stop last = ListUtil.last(stopsTo);
			if (!lineTo.isReverse()) {
				if (!last.getStation().getName().equals(change.getAt())) {
					print(lineFrom, lineTo, change, false);
				}
			} else {
				if (!first.getStation().getName().equals(change.getAt())) {
					print(lineFrom, lineTo, change, true);
				}
			}
		}
	}

	private void print(Line lineFrom, LineWithOrientation lineTo, Change change,
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
		int fromIndex = MapModelUtil.findStop(fromStops, change.getAt());
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
							+ " Looking for '%s' on '%s', having '%s' and '%s'",
					fromTowards, lineFrom.getName(),
					first.getStation().getName(), last.getStation().getName()));
		}

		int before = fromReverse ? fromIndex + 1 : fromIndex - 1;
		Stop fromBefore = fromStops.get(before);

		List<Stop> toStops = lineTo.getLine().getStops();
		int toIndex = MapModelUtil.findStop(toStops, change.getAt());
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

		String stationNameFrom = fromBefore.getStation().getName();
		String stationNameTo = toAfter.getStation().getName();

		String station = getId(change.getAt());
		String stationName = change.getAt();
		String fromLine = lineFrom.getName();
		String fromStation = getId(stationNameFrom);
		String fromStationName = stationNameFrom;
		String fromTrack = ""; // TODO: not supported by our format
		String fromPosition = valueFromPosition;
		String toLine = lineTo.getLine().getName();
		String toStation = getId(stationNameTo);
		String toStationName = stationNameTo;
		String toTrack = ""; // TODO: not supported by our format
		String toPosition = ""; // TODO: not supported by our format
		String samePlatform = Boolean.toString(isSamePlatform);

		List<String> values = Arrays.asList(station, stationName, fromLine,
				fromStation, fromStationName, fromTrack, fromPosition, toLine,
				toStation, toStationName, toTrack, toPosition, samePlatform);

		String line = Joiner.on(",").join(values);
		System.out.println(line);
	}

	private String getId(String name)
	{
		String id = nameToId.get(name);
		return id != null ? id : "<ID>";
	}

	private double position(Location location)
	{
		switch (location) {
		case FRONT:
			return 1;
		case ALMOST_FRONT:
			return 0.833;
		case MIDDLE_MIDDLE_FRONT:
			return 0.667;
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

}
