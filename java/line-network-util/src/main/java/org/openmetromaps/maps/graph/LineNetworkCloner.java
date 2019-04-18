// Copyright 2018 Sebastian Kuerten
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
import java.util.Map;

import org.openmetromaps.maps.Points;
import org.openmetromaps.maps.model.Station;

public class LineNetworkCloner
{

	private LineNetwork lineNetwork;

	private Map<Node, Node> nodeToNode = new HashMap<>();
	private Map<Edge, Edge> edgeToEdge = new HashMap<>();
	private Map<NetworkLine, NetworkLine> lineToLine = new HashMap<>();

	private LineNetwork copy = new LineNetwork();

	public LineNetworkCloner(LineNetwork lineNetwork)
	{
		this.lineNetwork = lineNetwork;
	}

	public LineNetwork cloneLineNetwork()
	{
		cloneNodes();
		setupStationToNodes();
		cloneEdges();
		copyNodeEdges();
		cloneLines();
		copyEdgeLines();

		return copy;
	}

	private void cloneNodes()
	{
		for (Node node : lineNetwork.getNodes()) {
			Node nodeCopy = new Node(node.station);
			copy.nodes.add(nodeCopy);

			nodeToNode.put(node, nodeCopy);

			nodeCopy.location = Points.clonePoint(node.location);
			nodeCopy.setRank(node.rank);
			nodeCopy.setIsLastStopOfALine(node.isLastStopOfALine);
		}
	}

	private void setupStationToNodes()
	{
		Map<Station, Node> stationToNode = new HashMap<>();
		copy.setStationToNode(stationToNode);
		for (Node node : lineNetwork.getNodes()) {
			Node nodeCopy = nodeToNode.get(node);
			stationToNode.put(nodeCopy.station, nodeCopy);
		}
	}

	private void cloneEdges()
	{
		for (Edge edge : lineNetwork.getEdges()) {
			Edge edgeCopy = new Edge(nodeToNode.get(edge.n1),
					nodeToNode.get(edge.n2));
			copy.edges.add(edgeCopy);

			edgeToEdge.put(edge, edgeCopy);

			edgeCopy.setNext(Points.clonePoint(edge.next));
			edgeCopy.setPrev(Points.clonePoint(edge.prev));
		}
	}

	private void cloneLines()
	{
		for (NetworkLine line : lineNetwork.getLines()) {
			NetworkLine lineCopy = new NetworkLine(line.line);

			copy.lines.add(lineCopy);

			for (Edge edge : line.nexts.keySet()) {
				lineCopy.nexts.put(edgeToEdge.get(edge),
						nodeToNode.get(line.nexts.get(edge)));
			}

			for (Edge edge : line.prevs.keySet()) {
				lineCopy.prevs.put(edgeToEdge.get(edge),
						nodeToNode.get(line.prevs.get(edge)));
			}

			for (Edge edge : line.neighbors.keySet()) {
				NeighborInfo info = line.neighbors.get(edge);
				NeighborInfo infoCopy = new NeighborInfo(
						nodeToNode.get(info.prev), nodeToNode.get(info.next));
				lineCopy.neighbors.put(edgeToEdge.get(edge), infoCopy);
			}

			lineToLine.put(line, lineCopy);
		}
	}

	private void copyNodeEdges()
	{
		for (Node node : lineNetwork.getNodes()) {
			Node nodeCopy = nodeToNode.get(node);
			for (Edge edge : node.edges) {
				nodeCopy.edges.add(edgeToEdge.get(edge));
			}
		}
	}

	private void copyEdgeLines()
	{
		for (Edge edge : lineNetwork.getEdges()) {
			Edge edgeCopy = edgeToEdge.get(edge);
			for (NetworkLine line : edge.lines) {
				edgeCopy.lines.add(lineToLine.get(line));
			}
		}
	}

}
