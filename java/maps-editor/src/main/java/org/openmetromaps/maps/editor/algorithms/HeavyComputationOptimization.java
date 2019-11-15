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

package org.openmetromaps.maps.editor.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.SwingUtilities;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.lightgeom.lina.Point;

public class HeavyComputationOptimization
{

	final static Logger logger = LoggerFactory
			.getLogger(HeavyComputationOptimization.class);

	private MapEditor mapEditor;
	private int numSteps;
	private int lengthPause;

	public void runOptimization(MapEditor mapEditor, int numSteps,
			int lengthPause)
	{
		this.mapEditor = mapEditor;
		this.numSteps = numSteps;
		this.lengthPause = lengthPause;

		// run the computation in a separate thread
		Runnable r = new Runnable() {

			@Override
			public void run()
			{
				performOptimization();
			}
		};

		new Thread(r).start();
	}

	private void performOptimization()
	{
		for (int i = 0; i < numSteps; i++) {
			logger.info("Iteration " + i);
			// pretend to let the computation take some time by sleeping for a
			// while
			try {
				Thread.sleep(lengthPause);
			} catch (InterruptedException e) {
				// ignore interruption
			}
			// now do the actual computation
			performIteration();
			// and dispatch a repaint() on the UI thread
			SwingUtilities.invokeLater(() -> {
				mapEditor.getMap().repaint();
			});
		}
	}

	/*
	 * Demo computation that picks a random line and a random station and
	 * changes the station's position by interpolating a position from the
	 * neighbor stations on the selected line.
	 */

	private Random random = new Random();

	private void performIteration()
	{
		// For each iteration, loop until we found a line and station for which
		// we successfully performed the change of position.
		while (true) {
			boolean found = findNodeAndChangePosition();
			if (found) {
				break;
			}
		}
	}

	private boolean findNodeAndChangePosition()
	{
		MapView view = mapEditor.getView();
		LineNetwork network = view.getLineNetwork();
		Map<Station, Node> stationToNode = network.getStationToNode();

		MapModel model = mapEditor.getModel();
		ModelData data = model.getData();

		// pick a random line
		int idxLine = random.nextInt(data.lines.size());
		Line line = data.lines.get(idxLine);

		// pick a random station on that line, excluding end-of-the-line stops
		List<Stop> stops = line.getStops();
		int idxStop = random.nextInt(stops.size() - 2) + 1;
		Stop stop = stops.get(idxStop);

		// determine edges in the graph that contain the selected line
		Node node = stationToNode.get(stop.getStation());
		List<Edge> edges = new ArrayList<>();
		for (Edge edge : node.edges) {
			for (NetworkLine networkLine : edge.lines) {
				if (networkLine.line == line) {
					edges.add(edge);
				}
			}
		}

		// only continue if we found exactly two edges
		if (edges.size() != 2) {
			return false;
		}

		// determine the neighboring nodes
		List<Node> otherNodes = new ArrayList<>();
		for (Edge edge : edges) {
			Node otherNode = otherNode(edge, node);
			otherNodes.add(otherNode);
		}

		// change the station node's position
		changePosition(line, node, otherNodes);
		return true;
	}

	private Node otherNode(Edge edge, Node node)
	{
		if (edge.n1 == node) {
			return edge.n2;
		}
		return edge.n1;
	}

	private void changePosition(Line line, Node node, List<Node> otherNodes)
	{
		Node neighbor1 = otherNodes.get(0);
		Node neighbor2 = otherNodes.get(1);

		logger.info(String.format("Line %s, station %s, neighbors: %s and %s",
				line.getName(), node.station.getName(),
				neighbor1.station.getName(), neighbor2.station.getName()));

		double factor = 0.3;
		double x = factor * neighbor1.location.getX()
				+ (1 - factor) * neighbor2.location.getX();
		double y = factor * neighbor1.location.getY()
				+ (1 - factor) * neighbor2.location.getY();

		node.location = new Point(x, y);
		LineNetworkUtil.updateEdges(node);
	}

}
