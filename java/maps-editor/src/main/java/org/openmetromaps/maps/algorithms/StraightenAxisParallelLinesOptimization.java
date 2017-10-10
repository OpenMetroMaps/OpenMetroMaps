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
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import de.topobyte.adt.geo.Coordinate;

public class StraightenAxisParallelLinesOptimization
{

	private Map<Station, Node> stationToNode;

	public void runOptimization(MapEditor mapEditor)
	{
		MapModel model = mapEditor.getModel();
		ModelData data = model.getData();

		MapView view = mapEditor.getView();
		LineNetwork network = view.getLineNetwork();

		stationToNode = network.getStationToNode();

		for (Line line : data.lines) {
			checkLine(line);
		}
	}

	private void checkLine(Line line)
	{
		List<Node> nodes = new ArrayList<>();

		List<Stop> stops = line.getStops();
		for (Stop stop : stops) {
			nodes.add(stationToNode.get(stop.getStation()));
		}

		checkNodes(nodes);
	}

	private void checkNodes(List<Node> nodes)
	{
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			Coordinate location = node.location;
			// TODO: somehow determine straightness property
		}
	}

}
