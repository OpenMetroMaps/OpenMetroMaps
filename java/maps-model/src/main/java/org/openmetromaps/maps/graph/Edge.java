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
import java.util.Comparator;
import java.util.List;

import de.topobyte.lightgeom.lina.Point;

public class Edge
{

	public static Comparator<NetworkLine> COMPARATOR = new Comparator<NetworkLine>() {

		@Override
		public int compare(NetworkLine o1, NetworkLine o2)
		{
			return o1.line.getName().compareTo(o2.line.getName());
		}
	};

	public Node n1;
	public Node n2;

	public Point prev;
	public Point next;

	public List<NetworkLine> lines = new ArrayList<>();

	public Edge(Node n1, Node n2)
	{
		this.n1 = n1;
		this.n2 = n2;
	}

	public void addLine(NetworkLine line)
	{
		lines.add(line);
	}

	public void setNext(Point next)
	{
		this.next = next;
	}

	public void setPrev(Point prev)
	{
		this.prev = prev;
	}

}
