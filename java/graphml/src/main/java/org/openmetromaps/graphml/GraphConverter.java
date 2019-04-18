// Copyright 2019 Sebastian Kuerten
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

package org.openmetromaps.graphml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import edu.uci.ics.jung.graph.UndirectedGraph;

public class GraphConverter
{

	protected List<Line> linesList = new ArrayList<>();
	protected List<Station> stationsList = new ArrayList<>();
	protected List<Stop> stopsList = new ArrayList<>();

	protected Map<Line, Integer> lineToIndex = new HashMap<>();
	protected Map<Station, Integer> stationToIndex = new HashMap<>();
	protected Map<Stop, Integer> stopToIndex = new HashMap<>();

	public ModelData convert(UndirectedGraph<Vertex, Edge> graph)
	{
		Set<String> allLinesSet = new HashSet<>();

		for (Edge edge : graph.getEdges()) {
			allLinesSet.addAll(edge.getLines());
		}

		List<String> allLines = new ArrayList<>(allLinesSet);
		Collections.sort(allLines);

		System.out.println("lines: " + allLines);

		Map<String, List<Edge>> lineToEdges = new HashMap<>();
		for (String line : allLines) {
			lineToEdges.put(line, new ArrayList<>());
		}

		for (Edge edge : graph.getEdges()) {
			for (String line : edge.getLines()) {
				lineToEdges.get(line).add(edge);
			}
		}

		Map<String, Line> nameToLine = new HashMap<>();
		Map<String, Station> nameToStation = new HashMap<>();

		int id = 0;
		for (String name : allLines) {
			String color = "#FFFFFF";

			Line line = new Line(id++, name, color, false, null);
			linesList.add(line);
			nameToLine.put(name, line);
		}

		for (String lineName : allLines) {
			Line line = nameToLine.get(lineName);
			List<Stop> stops = new ArrayList<>();
			line.setStops(stops);

			// TODO: determine correct order of edges
			List<Edge> edges = lineToEdges.get(lineName);
			for (Edge edge : edges) {
				Vertex source = edge.getSource();
				Vertex target = edge.getSource();

				String stopName = source.getLabel();

				Station station = nameToStation.get(stopName);
				if (station == null) {
					Coordinate location = new Coordinate(source.getX(),
							source.getY());
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
