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
import java.util.Collection;
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

import com.google.common.base.Splitter;

import edu.uci.ics.jung.graph.UndirectedGraph;

public class GraphConverter
{

	protected List<Line> linesList = new ArrayList<>();
	protected List<Station> stationsList = new ArrayList<>();
	protected List<Stop> stopsList = new ArrayList<>();

	protected Map<Line, Integer> lineToIndex = new HashMap<>();
	protected Map<Station, Integer> stationToIndex = new HashMap<>();
	protected Map<Stop, Integer> stopToIndex = new HashMap<>();

	protected Map<String, Station> nameToStation = new HashMap<>();

	public ModelData convert(GraphWithData graphWithData)
	{
		UndirectedGraph<Vertex, Edge> graph = graphWithData.getGraph();

		Map<String, String> metadata = graphWithData.getData();

		for (Vertex vertex : graph.getVertices()) {
			vertex.setY(vertex.getY() * -1);
		}

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

		Map<String, Line> idToLine = new HashMap<>();

		int id = 0;
		for (String lineId : allLines) {

			String name = metadata.get(String.format("name.%s", lineId));
			String sColor = metadata.get(String.format("color.%s", lineId));

			String color = null;
			if (sColor != null) {
				color = parseColor(sColor);
			}

			if (name == null) {
				name = lineId;
			}

			if (color == null) {
				color = "#AAAAAA";
			}

			Line line = new Line(id++, name, color, false, null);
			linesList.add(line);
			idToLine.put(lineId, line);
		}

		for (String lineName : allLines) {
			Line line = idToLine.get(lineName);

			List<Edge> edges = lineToEdges.get(lineName);
			lineFromEdges(graph, line, edges);
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

	private String parseColor(String input)
	{
		List<String> parts = Splitter.on(",").splitToList(input);
		if (parts.size() != 3) {
			return null;
		}
		// TODO: range check (>0, <256)
		int r = Integer.parseInt(parts.get(0));
		int g = Integer.parseInt(parts.get(1));
		int b = Integer.parseInt(parts.get(2));
		return String.format("#%02X%02X%02X", r, g, b);
	}

	private void lineFromEdges(UndirectedGraph<Vertex, Edge> graph, Line line,
			List<Edge> edges)
	{
		List<Stop> stops = new ArrayList<>();
		line.setStops(stops);

		if (edges.isEmpty()) {
			return;
		}

		// add any of the edges to the line to initialize
		Set<Edge> todo = new HashSet<>(edges);
		Edge firstEdge = todo.iterator().next();
		todo.remove(firstEdge);

		Vertex start = firstEdge.getSource();
		Vertex end = firstEdge.getTarget();

		append(line, start);
		append(line, end);

		// determine edges that can be prepended
		outer: while (!todo.isEmpty()) {
			Collection<Edge> out = graph.getIncidentEdges(start);
			for (Edge edge : out) {
				if (todo.contains(edge)) {
					todo.remove(edge);
					start = prepend(line, start, edge);
					continue outer;
				}
			}
			break outer;
		}

		// determine edges that can be appended
		outer: while (!todo.isEmpty()) {
			Collection<Edge> out = graph.getIncidentEdges(end);
			for (Edge edge : out) {
				if (todo.contains(edge)) {
					todo.remove(edge);
					end = append(line, end, edge);
					continue outer;
				}
			}
			break outer;
		}
	}

	private Vertex prepend(Line line, Vertex firstVertex, Edge edge)
	{
		if (edge.getSource() == firstVertex) {
			prepend(line, edge.getTarget());
			return edge.getTarget();
		} else if (edge.getTarget() == firstVertex) {
			prepend(line, edge.getSource());
			return edge.getSource();
		}
		throw new IllegalArgumentException(
				"vertex is neither source nor target of the edge");
	}

	private Vertex append(Line line, Vertex lastVertex, Edge edge)
	{
		if (edge.getSource() == lastVertex) {
			append(line, edge.getTarget());
			return edge.getTarget();
		} else if (edge.getTarget() == lastVertex) {
			append(line, edge.getSource());
			return edge.getSource();
		}
		throw new IllegalArgumentException(
				"vertex is neither source nor target of the edge");
	}

	private void prepend(Line line, Vertex vertex)
	{
		Station station = station(vertex);
		Stop stop = new Stop(station, line);

		List<Stop> stops = line.getStops();
		stops.add(0, stop);
	}

	private void append(Line line, Vertex vertex)
	{
		Station station = station(vertex);
		Stop stop = new Stop(station, line);

		List<Stop> stops = line.getStops();
		stops.add(stop);
	}

	private Station station(Vertex vertex)
	{
		String stopName = vertex.getLabel();
		Station station = nameToStation.get(stopName);

		if (station == null) {
			Coordinate location = new Coordinate(vertex.getX(), vertex.getY());
			station = new Station(0, stopName, location, new ArrayList<>());
			stationsList.add(station);
			nameToStation.put(stopName, station);
		}

		return station;
	}

}
