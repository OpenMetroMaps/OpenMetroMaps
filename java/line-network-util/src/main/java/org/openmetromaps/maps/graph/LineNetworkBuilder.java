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

package org.openmetromaps.maps.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmetromaps.maps.Edges;
import org.openmetromaps.maps.Interval;
import org.openmetromaps.maps.Segment;
import org.openmetromaps.maps.StationUtil;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slimjars.dist.gnu.trove.set.TIntSet;
import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import de.topobyte.formatting.Formatting;

public class LineNetworkBuilder
{

	final static Logger logger = LoggerFactory
			.getLogger(LineNetworkBuilder.class);

	private LineNetwork graph = new LineNetwork();

	private Map<Station, Node> stationToNode = new HashMap<>();
	private Map<Segment, Edge> segmentToEdge = new HashMap<>();

	public LineNetworkBuilder(ModelData data, List<Edges> edges)
	{
		graph.setStationToNode(stationToNode);

		addStations(data);

		addLines(data, edges);

		sortEdgeLines();

		computeRanks();

		sortNodesByRank();
	}

	private void addStations(ModelData data)
	{
		for (Station station : data.stations) {
			Node node = new Node(station);
			graph.nodes.add(node);
			stationToNode.put(station, node);
		}
	}

	private void addLines(ModelData data, List<Edges> edgesDefs)
	{
		final int nLines = data.lines.size();

		Map<String, Line> nameToLine = new HashMap<>();
		SetMultimap<String, NetworkLine> nameToNetworkLine = new SetMultimap<>();
		for (int i = 0; i < nLines; i++) {
			Line line = data.lines.get(i);
			nameToLine.put(line.getName(), line);

			NetworkLine networkLine = new NetworkLine(line);
			graph.lines.add(networkLine);
			nameToNetworkLine.put(line.getName(), networkLine);
		}

		for (Edges edgesDef : edgesDefs) {
			String lineName = edgesDef.getLine();
			Set<NetworkLine> networkLines = nameToNetworkLine.get(lineName);
			if (networkLines == null) {
				continue;
			}

			for (NetworkLine networkLine : networkLines) {
				graph.lines.add(networkLine);

				if (edgesDef.getIntervals().isEmpty()) {
					addAllEdges(networkLine);
				} else {
					addIntervalEdges(networkLine, edgesDef.getIntervals());
				}
			}
		}
	}

	private void addAllEdges(NetworkLine networkLine)
	{
		List<Stop> stops = networkLine.line.getStops();
		if (stops.isEmpty()) {
			return;
		}
		Stop prev = stops.get(0);
		List<Edge> edges = new ArrayList<>();
		for (int k = 1; k < stops.size(); k++) {
			Stop next = stops.get(k);

			edges.add(addSegment(networkLine, prev, next));

			prev = next;
		}
		if (networkLine.line.isCircular()) {
			edges.add(addSegment(networkLine, prev, stops.get(0)));
		}
		networkLine.setEdges(edges);
	}

	private void addIntervalEdges(NetworkLine networkLine,
			List<Interval> intervals)
	{
		for (Interval interval : intervals) {
			addIntervalEdges(networkLine, interval);
		}
	}

	private void addIntervalEdges(NetworkLine networkLine, Interval interval)
	{
		String nameFrom = interval.getFrom();
		String nameTo = interval.getTo();

		int from = -1;
		int to = -1;

		List<Stop> stops = networkLine.line.getStops();
		for (int i = 0; i < stops.size(); i++) {
			String stopName = stops.get(i).getStation().getName();
			if (stopName.equals(nameFrom)) {
				from = i;
			}
			if (stopName.equals(nameTo)) {
				to = i;
			}
		}

		if (from < 0 || to < 0) {
			return;
		}

		// make sure from <= to
		if (from > to) {
			int tmp = from;
			from = to;
			to = tmp;
		}

		logger.debug(Formatting.format("Line %s, interval: %d - %d",
				networkLine.line.getName(), from, to));

		Stop prev = stops.get(from);
		List<Edge> edges = new ArrayList<>();
		for (int k = from + 1; k <= to; k++) {
			Stop next = stops.get(k);

			Node node1 = stationToNode.get(prev.getStation());
			Node node2 = stationToNode.get(next.getStation());
			logger.debug(Formatting.format("Segment: %s - %s",
					node1.station.getName(), node2.station.getName()));

			edges.add(addSegment(networkLine, prev, next));

			prev = next;
		}

		networkLine.setEdges(edges);
	}

	private void sortEdgeLines()
	{
		final int nEdges = graph.edges.size();
		for (int i = 0; i < nEdges; i++) {
			Edge edge = graph.edges.get(i);
			Collections.sort(edge.lines, Edge.COMPARATOR);
		}
	}

	private void calculateEdgeNeighborLocations()
	{
		final int nEdges = graph.edges.size();
		for (int i = 0; i < nEdges; i++) {
			Edge edge = graph.edges.get(i);
			LineNetworkUtil.calculateNeighborLocations(edge);
		}
	}

	private void computeRanks()
	{
		final int nNodes = graph.nodes.size();
		for (int i = 0; i < nNodes; i++) {
			Node node = graph.nodes.get(i);
			boolean isLastStop = StationUtil.isLastStopOfALine(node.station);
			node.setIsLastStopOfALine(isLastStop);

			int rank = isLastStop ? 1 : 0;
			List<Edge> edges = node.edges;
			final int nNodeEdges = edges.size();
			if (nNodeEdges == 1) {
				rank += 1;
			} else if (nNodeEdges == 2) {
				Edge e1 = edges.get(0);
				Edge e2 = edges.get(1);
				List<NetworkLine> lines1 = e1.lines;
				List<NetworkLine> lines2 = e2.lines;
				if (lines1.size() == 1 && lines2.size() == 1) {
					NetworkLine line1 = lines1.get(0);
					NetworkLine line2 = lines2.get(0);
					rank += line1 == line2 ? 1 : 2;
				} else {
					TIntHashSet ids1 = lineIds(edges.get(0));
					TIntHashSet ids2 = lineIds(edges.get(1));
					rank += equal(e1, ids1, e2, ids2) ? 1 : 2;
				}
			} else {
				rank += rank(edges);
			}
			node.setRank(rank);
		}
	}

	private void sortNodesByRank()
	{
		Collections.sort(graph.nodes, new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2)
			{
				return o2.rank - o1.rank;
			}
		});
	}

	private int rank(List<Edge> edges)
	{
		int rank = 0;
		final int nEdges = edges.size();
		TIntSet[] ids = new TIntSet[nEdges];
		for (int i = 0; i < nEdges; i++) {
			Edge e = edges.get(i);
			ids[i] = lineIds(e);
		}
		for (int i = 0; i < nEdges; i++) {
			Edge e1 = edges.get(i);
			TIntSet ids1 = ids[i];
			for (int j = i + 1; j < nEdges; j++) {
				Edge e2 = edges.get(j);
				TIntSet ids2 = ids[j];
				if (!equal(e1, ids1, e2, ids2)) {
					rank += 1;
				}
			}
		}
		return rank;
	}

	private boolean equal(Edge e1, TIntSet ids1, Edge e2, TIntSet ids2)
	{
		lineIds(e1);
		int n1 = e1.lines.size();
		for (int i = 0; i < n1; i++) {
			int id = e1.lines.get(i).line.getId();
			if (!ids2.contains(id)) {
				return false;
			}
		}
		int n2 = e2.lines.size();
		for (int i = 0; i < n2; i++) {
			int id = e2.lines.get(i).line.getId();
			if (!ids1.contains(id)) {
				return false;
			}
		}
		return true;
	}

	private TIntHashSet lineIds(Edge edge)
	{
		final int n = edge.lines.size();
		TIntHashSet set = new TIntHashSet();
		for (int i = 0; i < n; i++) {
			set.add(edge.lines.get(i).line.getId());
		}
		return set;
	}

	public LineNetwork getGraph()
	{
		return graph;
	}

	private Edge addSegment(NetworkLine line, Stop stop1, Stop stop2)
	{
		Segment segment = new Segment(stop1.getStation(), stop2.getStation());
		Edge edge = segmentToEdge.get(segment);
		if (edge == null) {
			Node node1 = stationToNode.get(stop1.getStation());
			Node node2 = stationToNode.get(stop2.getStation());
			edge = new Edge(node1, node2);
			graph.edges.add(edge);
			segmentToEdge.put(segment, edge);
			node1.edges.add(edge);
			node2.edges.add(edge);
		}
		edge.addLine(line);
		return edge;
	}

}
