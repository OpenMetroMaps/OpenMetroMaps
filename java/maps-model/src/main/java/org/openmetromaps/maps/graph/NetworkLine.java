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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.model.Line;

public class NetworkLine
{

	public Line line;

	public NetworkLine(Line line)
	{
		this.line = line;
	}

	Map<Edge, Node> nexts = new HashMap<>();
	Map<Edge, Node> prevs = new HashMap<>();
	Map<Edge, NeighborInfo> neighbors = new HashMap<>();

	public void setEdges(List<Edge> edges)
	{
		int nEdges = edges.size();

		if (nEdges == 1) {
			Edge edge = edges.get(0);
			neighbors.put(edge, new NeighborInfo(null, null));
			return;
		}

		{
			Edge edge = edges.get(0);
			Edge next = edges.get(1);
			add(edge, next);
			if (line.isCircular()) {
				Edge prev = edges.get(nEdges - 1);
				add(edge, prev);
			}
		}
		for (int i = 1; i < nEdges - 1; i++) {
			Edge prev = edges.get(i - 1);
			Edge edge = edges.get(i);
			Edge next = edges.get(i + 1);

			add(edge, prev);
			add(edge, next);
		}
		{
			Edge prev = edges.get(nEdges - 2);
			Edge edge = edges.get(nEdges - 1);
			add(edge, prev);
			if (line.isCircular()) {
				Edge next = edges.get(0);
				add(edge, next);
			}
		}

		for (int i = 0; i < nEdges; i++) {
			Edge edge = edges.get(i);
			Node prev = prevs.get(edge);
			Node next = nexts.get(edge);
			neighbors.put(edge, new NeighborInfo(prev, next));
		}
	}

	private void add(Edge edge, Edge other)
	{
		Node start = edge.n1;
		Node end = edge.n2;
		if (end == other.n1) {
			nexts.put(edge, other.n2);
		} else if (end == other.n2) {
			nexts.put(edge, other.n1);
		}

		if (start == other.n1) {
			prevs.put(edge, other.n2);
		} else if (start == other.n2) {
			prevs.put(edge, other.n1);
		}
	}

	public NeighborInfo getNeighbors(Edge edge)
	{
		return neighbors.get(edge);
	}

	public Node getNext(Edge edge)
	{
		return nexts.get(edge);
	}

	public Node getPrev(Edge edge)
	{
		return prevs.get(edge);
	}

}
