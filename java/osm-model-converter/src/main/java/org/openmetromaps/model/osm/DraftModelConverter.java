// Copyright 2017 Sebastian Kuerten
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

package org.openmetromaps.model.osm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public class DraftModelConverter
{

	protected List<Line> linesList = new ArrayList<>();
	protected List<Station> stationsList = new ArrayList<>();
	protected List<Stop> stopsList = new ArrayList<>();

	protected Map<Line, Integer> lineToIndex = new HashMap<>();
	protected Map<Station, Integer> stationToIndex = new HashMap<>();
	protected Map<Stop, Integer> stopToIndex = new HashMap<>();

	public ModelData convert(DraftModel draftModel)
	{
		List<DraftLine> draftLines = draftModel.getLines();

		Map<DraftLine, Line> draftToLine = new HashMap<>();
		Map<String, Station> nameToStation = new HashMap<>();

		int id = 0;
		for (DraftLine draftLine : draftLines) {
			Map<String, String> tags = OsmModelUtil
					.getTagsAsMap(draftLine.getSource());
			String name = draftLine.getName();
			String color = tags.get("colour");

			Line line = new Line(id++, name, color, false, null);
			linesList.add(line);
			draftToLine.put(draftLine, line);
		}

		for (DraftLine draftLine : draftLines) {
			Line line = draftToLine.get(draftLine);
			List<Stop> stops = new ArrayList<>();
			line.setStops(stops);

			for (DraftStation draftStation : draftLine.getStations()) {
				String stopName = draftStation.getName();

				Station station = nameToStation.get(stopName);
				if (station == null) {
					OsmNode node = draftStation.getSource();
					Coordinate location = new Coordinate(node.getLongitude(),
							node.getLatitude());
					station = new Station(0, stopName, location,
							new ArrayList<Stop>());
					stationsList.add(station);
					nameToStation.put(stopName, station);
				}

				Stop stop = new Stop(station, line);
				stops.add(stop);
				station.getStops().add(stop);
			}
		}

		MapModelUtil.sortStationsByName(stationsList);

		for (int i = 0; i < linesList.size(); i++) {
			Line line = linesList.get(i);
			lineToIndex.put(line, i);
		}

		int k = -1;
		for (int i = 0; i < stationsList.size(); i++) {
			Station station = stationsList.get(i);
			stationToIndex.put(station, i);

			List<Stop> stops = station.getStops();
			for (Stop stop : stops) {
				k++;
				stopToIndex.put(stop, k);
				stopsList.add(stop);
			}
		}

		return new ModelData(linesList, stationsList);
	}

}
