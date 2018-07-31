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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmetromaps.maps.Points;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.collections.util.SetUtil;
import de.topobyte.formatting.Formatting;
import de.topobyte.lightgeom.lina.Point;

public class LineNetworkUtil
{

	final static Logger logger = LoggerFactory.getLogger(LineNetworkUtil.class);

	public static Node previousStation(NetworkLine line, Edge edge)
	{
		return stop(line, edge, edge.n1);
	}

	public static Node nextStation(NetworkLine line, Edge edge)
	{
		return stop(line, edge, edge.n2);
	}

	private static Node stop(NetworkLine line, Edge edge, Node start)
	{
		Edge found = null;
		List<Edge> edges = start.edges;
		final int nEdges = edges.size();
		for (int i = 0; i < nEdges; i++) {
			Edge out = edges.get(i);
			if (out == edge) {
				continue;
			}
			List<NetworkLine> lines = out.lines;
			int position = lines.indexOf(line);
			// int position = Collections.binarySearch(out.getLines(), line,
			// Edge.COMPARATOR);
			if (position >= 0) {
				found = out;
				break;
			}
		}
		if (found == null) {
			return null;
		}

		return found.n1 != start ? found.n1 : found.n2;
	}

	public static void calculateNeighborLocations(Edge edge)
	{
		List<NetworkLine> lines = edge.lines;
		if (lines.size() == 1) {
			// Optimize anything here?
		} else {
			List<Point> prevs = new ArrayList<>();
			List<Point> nexts = new ArrayList<>();
			for (NetworkLine line : lines) {
				NeighborInfo neighbors = line.getNeighbors(edge);

				Node prev = neighbors.prev;
				Node next = neighbors.next;

				if (prev != null) {
					prevs.add(prev.location);
				}
				if (next != null) {
					nexts.add(next.location);
				}
			}

			if (!prevs.isEmpty()) {
				Point lp = Points.mean(prevs);
				edge.setPrev(lp);
			}
			if (!nexts.isEmpty()) {
				Point ln = Points.mean(nexts);
				edge.setNext(ln);
			}
		}
	}

	public static void calculateAllNeighborLocations(LineNetwork lineNetwork)
	{
		for (Edge edge : lineNetwork.edges) {
			LineNetworkUtil.calculateNeighborLocations(edge);
		}
	}

	public static void updateEdges(Node node)
	{
		// Update all edges connected to neighbor nodes in the network graph
		List<Node> neighbors = new ArrayList<>();
		for (Edge edge : node.edges) {
			if (edge.n1 != node) {
				neighbors.add(edge.n1);
			}
			if (edge.n2 != node) {
				neighbors.add(edge.n2);
			}
		}
		for (Node n : neighbors) {
			for (Edge edge : n.edges) {
				logger.info(Formatting.format("Updating edge: %s - %s",
						edge.n1.station.getName(), edge.n2.station.getName()));
				LineNetworkUtil.calculateNeighborLocations(edge);
			}
		}
	}

	public static NodeConnectionResult findConnection(Node node1, Node node2)
	{
		NodeConnectionResult result = new NodeConnectionResult();

		Set<Line> node1Lines = lines(node1);
		Set<Line> node2Lines = lines(node2);

		Set<Line> commonLines = SetUtil.intersection(node1Lines, node2Lines);
		boolean connected = !commonLines.isEmpty();

		result.setConnected(connected);
		result.setCommonLines(commonLines);

		if (!connected) {
			return result;
		}

		return result;
	}

	private static Set<Line> lines(Node node)
	{
		Set<Line> lines = new HashSet<>();
		for (Stop stop : node.station.getStops()) {
			lines.add(stop.getLine());
		}
		return lines;
	}

	public static LineConnectionResult findConnection(Line line, Node node1,
			Node node2)
	{
		LineConnectionResult result = new LineConnectionResult();

		int idxNode1 = -1;
		int idxNode2 = -1;

		List<Stop> stops = line.getStops();
		for (int i = 0; i < stops.size(); i++) {
			Station station = stops.get(i).getStation();
			if (station == node1.station) {
				idxNode1 = i;
			} else if (station == node2.station) {
				idxNode2 = i;
			}
		}

		result.setIndex1(idxNode1);
		result.setIndex2(idxNode2);

		result.setValid(idxNode1 >= 0 && idxNode2 >= 0);

		return result;
	}

	public static List<Node> getNodes(LineNetwork lineNetwork, Line line)
	{
		int length = line.getStops().size();

		NodesInBetweenResult result = getNodesBetween(lineNetwork, line, 0,
				length - 1);

		List<Node> all = new ArrayList<>();
		all.add(result.getStart());
		all.addAll(result.getNodes());
		all.add(result.getEnd());
		return all;
	}

	public static NodesInBetweenResult getNodesBetween(LineNetwork lineNetwork,
			Line line, int idxNode1, int idxNode2)
	{
		NodesInBetweenResult result = new NodesInBetweenResult();

		List<Stop> stops = line.getStops();
		Stop stop1 = stops.get(idxNode1);
		Stop stop2 = stops.get(idxNode2);
		Node node1 = getNode(lineNetwork, stop1);
		Node node2 = getNode(lineNetwork, stop2);

		Node start, end;
		int min, max;
		if (idxNode1 < idxNode2) {
			min = idxNode1;
			max = idxNode2;
			start = node1;
			end = node2;
		} else {
			min = idxNode2;
			max = idxNode1;
			start = node2;
			end = node1;
		}

		result.setStart(start);
		result.setEnd(end);

		int num = max - min - 1;

		logger.debug(Formatting.format("Stops min: %d, max: %d, num: %d", min,
				max, num));

		List<Node> nodes = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			Stop stop = stops.get(min + i);
			Node node = getNode(lineNetwork, stop);
			nodes.add(node);
		}

		result.setNodes(nodes);

		return result;
	}

	public static Node getNode(LineNetwork lineNetwork, Stop stop)
	{
		// TODO: this is pretty inefficient
		for (Node node : lineNetwork.getNodes()) {
			for (Stop s : node.station.getStops()) {
				if (s == stop) {
					return node;
				}
			}
		}
		return null;
	}

	public static Set<Line> getLines(Node node)
	{
		Set<Line> results = new HashSet<>();
		for (Edge edge : node.edges) {
			for (NetworkLine line : edge.lines) {
				results.add(line.line);
			}
		}
		return results;
	}

}
