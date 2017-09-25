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

import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.model.Station;

import de.topobyte.adt.geo.Coordinate;

public class LineNetworkUtil
{

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
			List<Station> prevs = new ArrayList<>();
			List<Station> nexts = new ArrayList<>();
			for (NetworkLine line : lines) {
				NeighborInfo neighbors = line.getNeighbors(edge);

				Node prev = neighbors.prev;
				Node next = neighbors.next;

				if (prev != null) {
					prevs.add(prev.station);
				}
				if (next != null) {
					nexts.add(next.station);
				}
			}

			Coordinate lp = prevs.size() == 0 ? null
					: ModelUtil.meanOfStations(prevs);
			Coordinate ln = nexts.size() == 0 ? null
					: ModelUtil.meanOfStations(nexts);
			edge.setPrev(lp);
			edge.setNext(ln);
		}
	}

}
