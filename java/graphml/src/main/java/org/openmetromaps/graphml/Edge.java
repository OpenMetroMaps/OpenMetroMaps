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

import java.util.List;

public class Edge
{

	private String id;
	private Vertex source;
	private Vertex target;
	private List<String> lines;

	public Edge(String id, Vertex source, Vertex target, List<String> lines)
	{
		this.id = id;
		this.source = source;
		this.target = target;
		this.lines = lines;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public Vertex getSource()
	{
		return source;
	}

	public void setSource(Vertex source)
	{
		this.source = source;
	}

	public Vertex getTarget()
	{
		return target;
	}

	public void setTarget(Vertex target)
	{
		this.target = target;
	}

	public List<String> getLines()
	{
		return lines;
	}

	public void setLines(List<String> lines)
	{
		this.lines = lines;
	}

}
