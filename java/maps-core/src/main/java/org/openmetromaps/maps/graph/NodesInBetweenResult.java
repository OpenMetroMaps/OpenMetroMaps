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

public class NodesInBetweenResult
{

	private List<Node> nodes;
	private Node start;
	private Node end;

	public void setNodes(List<Node> nodes)
	{
		this.nodes = nodes;
	}

	public List<Node> getNodes()
	{
		return nodes;
	}

	public void setStart(Node start)
	{
		this.start = start;
	}

	public Node getStart()
	{
		return start;
	}

	public void setEnd(Node end)
	{
		this.end = end;
	}

	public Node getEnd()
	{
		return end;
	}

}
