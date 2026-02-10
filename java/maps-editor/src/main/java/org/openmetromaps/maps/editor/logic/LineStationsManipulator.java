// Copyright 2026 Sebastian Kuerten
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

package org.openmetromaps.maps.editor.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmetromaps.maps.Edges;
import org.openmetromaps.maps.Interval;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import de.topobyte.lightgeom.lina.Point;

public class LineStationsManipulator
{

	public void applyStops(MapEditor mapEditor, Line line, List<Stop> oldStops,
			List<Stop> newStops)
	{
		MapModel model = mapEditor.getModel();
		ModelData data = model.getData();
		MapView view = mapEditor.getView();

		String oldFirst = oldStops.isEmpty() ? null
				: oldStops.get(0).getStation().getName();
		String oldLast = oldStops.isEmpty() ? null
				: oldStops.get(oldStops.size() - 1).getStation().getName();

		String newFirst = newStops.isEmpty() ? null
				: newStops.get(0).getStation().getName();
		String newLast = newStops.isEmpty() ? null
				: newStops.get(newStops.size() - 1).getStation().getName();

		line.setStops(newStops);

		synchronizeStationStops(oldStops, newStops);

		ensureCanonicalSortingOfStopsAtStation(model, data, oldStops, newStops);

		extendEdges(view, line, oldFirst, oldLast, newFirst, newLast);

		update(mapEditor, view, data, line);
		mapEditor.updateStationPanel();
	}

	private void extendEdges(MapView view, Line line, String oldFirst,
			String oldLast, String newFirst, String newLast)
	{
		if (oldFirst != null && oldLast != null && newFirst != null
				&& newLast != null) {
			for (Edges edges : view.getEdges()) {
				if (edges.getLine().equals(line.getName())) {
					for (Interval interval : edges.getIntervals()) {
						if (interval.getFrom().equals(oldFirst)) {
							interval.setFrom(newFirst);
						}
						if (interval.getTo().equals(oldLast)) {
							interval.setTo(newLast);
						}
					}
				}
			}
		}
	}

	private void synchronizeStationStops(List<Stop> oldStops,
			List<Stop> newStops)
	{
		for (Stop oldStop : oldStops) {
			if (!newStops.contains(oldStop)) {
				oldStop.getStation().getStops().remove(oldStop);
			}
		}
		for (Stop newStop : newStops) {
			if (!oldStops.contains(newStop)) {
				newStop.getStation().getStops().add(newStop);
			}
		}
	}

	private void ensureCanonicalSortingOfStopsAtStation(MapModel model,
			ModelData data, List<Stop> oldStops, List<Stop> newStops)
	{
		Map<Line, Integer> lineOrder = new HashMap<>();
		for (int i = 0; i < data.lines.size(); i++) {
			lineOrder.put(data.lines.get(i), i);
		}

		Set<Station> affectedStations = new HashSet<>();
		for (Stop stop : oldStops) {
			affectedStations.add(stop.getStation());
		}
		for (Stop stop : newStops) {
			affectedStations.add(stop.getStation());
		}

		for (Station station : affectedStations) {
			Collections.sort(station.getStops(), (a, b) -> {
				Integer orderA = lineOrder.get(a.getLine());
				Integer orderB = lineOrder.get(b.getLine());
				if (orderA == null) {
					return orderB == null ? 0 : 1;
				}
				if (orderB == null) {
					return -1;
				}
				return Integer.compare(orderA, orderB);
			});
		}
	}

	private void update(MapEditor mapEditor, MapView view, ModelData data,
			Line line)
	{
		LineNetwork oldNetwork = view.getLineNetwork();
		Map<Station, Node> oldNodes = oldNetwork.getStationToNode();

		LineNetworkBuilder builder = new LineNetworkBuilder(data,
				view.getEdges());
		LineNetwork lineNetwork = builder.getGraph();
		List<Node> nodes = lineNetwork.getNodes();

		for (Node node : nodes) {
			Node oldNode = oldNodes == null ? null : oldNodes.get(node.station);
			if (oldNode != null && oldNode.location != null) {
				node.location = oldNode.location;
			} else if (node.station.getLocation() != null) {
				Coordinate coord = node.station.getLocation();
				node.location = new Point(coord.getLongitude(),
						coord.getLatitude());
			}
		}
		LineNetworkUtil.calculateAllNeighborLocations(lineNetwork);

		view.setLineNetwork(lineNetwork);
		mapEditor.getMap().setData(data, lineNetwork,
				mapEditor.getMapViewStatus());

		mapEditor.triggerDataChanged();
		mapEditor.getMap().repaint();
	}

}
