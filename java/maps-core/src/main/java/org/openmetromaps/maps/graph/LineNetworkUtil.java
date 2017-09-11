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

import java.util.List;

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

}
