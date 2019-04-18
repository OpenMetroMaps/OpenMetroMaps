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

import java.util.Map;

import edu.uci.ics.jung.graph.UndirectedGraph;

public class GraphWithData
{

	private UndirectedGraph<Vertex, Edge> graph;
	private Map<String, String> data;

	public GraphWithData(UndirectedGraph<Vertex, Edge> graph,
			Map<String, String> data)
	{
		this.graph = graph;
		this.data = data;
	}

	public UndirectedGraph<Vertex, Edge> getGraph()
	{
		return graph;
	}

	public void setGraph(UndirectedGraph<Vertex, Edge> graph)
	{
		this.graph = graph;
	}

	public Map<String, String> getData()
	{
		return data;
	}

	public void setData(Map<String, String> data)
	{
		this.data = data;
	}

}
