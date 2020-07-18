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

import java.io.InputStream;
import java.util.Map;

import de.topobyte.melon.resources.Resources;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.io.GraphIOException;

public class TestPrintGraph
{

	public static void main(String[] args) throws GraphIOException
	{
		InputStream input = Resources.stream("wien.graphml");

		GraphMLReader graphMLReader = new GraphMLReader();
		GraphWithData graphWithData = graphMLReader.read(input);
		UndirectedGraph<Vertex, Edge> graph = graphWithData.getGraph();

		Map<String, String> data = graphWithData.getData();
		for (String key : data.keySet()) {
			System.out.println(String.format("'%s'='%s'", key, data.get(key)));
		}

		for (Vertex vertex : graph.getVertices()) {
			System.out.println(String.format("vertex: %s", vertex.getLabel()));
		}

		for (Edge edge : graph.getEdges()) {
			Vertex source = edge.getSource();
			Vertex target = edge.getTarget();
			System.out.println(String.format("edge %s - %s", source.getLabel(),
					target.getLabel()));
		}
	}

}
