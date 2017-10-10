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

package org.openmetromaps.maps.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.MapEditor;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.topobyte.adt.geo.Coordinate;
import de.topobyte.collections.util.ListUtil;

public class StraightenAxisParallelLinesOptimization
{

	final static Logger logger = LoggerFactory
			.getLogger(StraightenAxisParallelLinesOptimization.class);

	private double tolerance;

	private Map<Station, Node> stationToNode;

	private static enum Direction {
		X,
		Y
	}

	public void runOptimization(MapEditor mapEditor, double tolerance)
	{
		this.tolerance = tolerance;

		MapModel model = mapEditor.getModel();
		ModelData data = model.getData();

		MapView view = mapEditor.getView();
		LineNetwork network = view.getLineNetwork();

		stationToNode = network.getStationToNode();

		for (Line line : data.lines) {
			checkLine(line);
		}

		for (Node node : network.nodes) {
			LineNetworkUtil.updateEdges(node);
		}
	}

	private void checkLine(Line line)
	{
		List<Node> nodes = new ArrayList<>();

		List<Stop> stops = line.getStops();
		for (Stop stop : stops) {
			nodes.add(stationToNode.get(stop.getStation()));
		}

		optimize(line, nodes, Direction.X);
		optimize(line, nodes, Direction.Y);
	}

	private void optimize(Line line, List<Node> nodes, Direction direction)
	{
		List<Integer> ids = new ArrayList<>();
		for (int i = 0; i < nodes.size() - 1; i++) {
			Node node1 = nodes.get(i);
			Node node2 = nodes.get(i + 1);
			Coordinate location1 = node1.location;
			Coordinate location2 = node2.location;
			if (direction == Direction.X) {
				double dx = Math.abs(
						location1.getLongitude() - location2.getLongitude());
				if (dx <= tolerance) {
					ids.add(i);
				}
			} else {
				double dy = Math
						.abs(location1.getLatitude() - location2.getLatitude());
				if (dy <= tolerance) {
					ids.add(i);
				}
			}
		}
		if (ids.isEmpty()) {
			return;
		}
		straighten(line, nodes, ids, direction);
	}

	private void straighten(Line line, List<Node> nodes, List<Integer> ids,
			Direction direction)
	{
		List<List<Integer>> consecutives = Util.findConsecutive(ids);
		for (List<Integer> list : consecutives) {
			List<String> names = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				int k = list.get(i);
				names.add(nodes.get(k).station.getName());
			}
			names.add(nodes.get(ListUtil.last(list) + 1).station.getName());
			logger.info(String.format("%s: %s", line.getName(),
					Joiner.on(", ").join(names)));

			List<Node> adjust = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				int k = list.get(i);
				adjust.add(nodes.get(k));
			}
			int last = list.get(list.size() - 1);
			adjust.add(nodes.get(last + 1));

			adjust(adjust, direction);
		}
	}

	private void adjust(List<Node> nodes, Direction direction)
	{
		List<Coordinate> coordinates = new ArrayList<>();
		for (Node node : nodes) {
			coordinates.add(node.location);
		}

		Coordinate mean = Coordinate.mean(coordinates);

		for (Node node : nodes) {
			if (direction == Direction.X) {
				node.location = new Coordinate(mean.getLongitude(),
						node.location.getLatitude());
			}
			if (direction == Direction.Y) {
				node.location = new Coordinate(node.location.getLongitude(),
						mean.getLatitude());
			}
		}
	}

}
