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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.graphml.EdgeMetadata;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import edu.uci.ics.jung.io.graphml.HyperEdgeMetadata;
import edu.uci.ics.jung.io.graphml.NodeMetadata;

public class GraphMLReader
{

	private Map<String, Vertex> idToVertex = new HashMap<>();
	private Map<String, String> propertiesGraph;

	private Function<GraphMetadata, UndirectedGraph<Vertex, Edge>> graphTransformer = new Function<GraphMetadata, UndirectedGraph<Vertex, Edge>>() {

		@Override
		public UndirectedSparseMultigraph<Vertex, Edge> apply(
				GraphMetadata input)
		{
			propertiesGraph = input.getProperties();
			return new UndirectedSparseMultigraph<>();
		}

	};

	private Function<NodeMetadata, Vertex> vertexTransformer = new Function<NodeMetadata, Vertex>() {

		@Override
		public Vertex apply(NodeMetadata input)
		{
			String id = input.getId();
			double x = Double.parseDouble(input.getProperty("x"));
			double y = Double.parseDouble(input.getProperty("y"));
			String label = input.getProperty("label");

			Vertex vertex = new Vertex(id, x, y, label);

			idToVertex.put(id, vertex);

			return vertex;
		}

	};

	private Function<EdgeMetadata, Edge> edgeTransformer = new Function<EdgeMetadata, Edge>() {

		@Override
		public Edge apply(EdgeMetadata input)
		{
			String id = input.getId();

			String sourceId = input.getSource();
			String targetId = input.getTarget();

			List<String> lines = new ArrayList<>();
			for (String key : input.getProperties().keySet()) {
				String value = input.getProperty(key);
				if ("true".equalsIgnoreCase(value)) {
					lines.add(key);
				}
			}

			Vertex source = idToVertex.get(sourceId);
			Vertex target = idToVertex.get(targetId);

			Edge edge = new Edge(id, source, target, lines);

			return edge;
		}

	};

	private Function<HyperEdgeMetadata, Edge> hyperEdgeTransformer = new Function<HyperEdgeMetadata, Edge>() {

		@Override
		public Edge apply(HyperEdgeMetadata input)
		{
			return new Edge(null, null, null, null);
		}

	};

	public GraphWithData read(Path path) throws IOException, GraphIOException
	{
		InputStream input = Files.newInputStream(path);
		return read(input);
	}

	public GraphWithData read(InputStream input) throws GraphIOException
	{
		GraphMLReader2<UndirectedGraph<Vertex, Edge>, Vertex, Edge> reader = new GraphMLReader2<>(
				input, graphTransformer, vertexTransformer, edgeTransformer,
				hyperEdgeTransformer);
		UndirectedGraph<Vertex, Edge> graph = reader.readGraph();

		return new GraphWithData(graph, propertiesGraph);
	}

}
