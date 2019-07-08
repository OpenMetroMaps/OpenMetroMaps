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

package org.openmetromaps.newformat;

import java.util.HashMap;
import java.util.Map;

import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;

public class Ids
{

	private Map<Station, String> nodeToId = new HashMap<>();
	private Map<Line, String> lineToId = new HashMap<>();
	private Map<Edge, String> edgeToId = new HashMap<>();

	public String getNodeId(Station node)
	{
		return nodeToId.get(node);
	}

	public String getLineId(Line line)
	{
		return lineToId.get(line);
	}

	public String getEdgeId(Edge edge)
	{
		return edgeToId.get(edge);
	}

	public void setNodeId(Station node, String id)
	{
		nodeToId.put(node, id);
	}

	public void setLineId(Line line, String id)
	{
		lineToId.put(line, id);
	}

	public void setEdgeId(Edge edge, String id)
	{
		edgeToId.put(edge, id);
	}

}
