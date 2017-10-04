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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.geo.Coordinate;

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
			List<Coordinate> prevs = new ArrayList<>();
			List<Coordinate> nexts = new ArrayList<>();
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
				Coordinate lp = Coordinate.mean(prevs);
				edge.setPrev(lp);
			}
			if (!nexts.isEmpty()) {
				Coordinate ln = Coordinate.mean(nexts);
				edge.setNext(ln);
			}
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
				logger.info(String.format("Updating edge: %s - %s",
						edge.n1.station.getName(), edge.n2.station.getName()));
				LineNetworkUtil.calculateNeighborLocations(edge);
			}
		}
	}

}
