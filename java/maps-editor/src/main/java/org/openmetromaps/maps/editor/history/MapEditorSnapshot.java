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

package org.openmetromaps.maps.editor.history;

import java.util.HashMap;
import java.util.Map;

import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ViewConfig;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Station;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

public class MapEditorSnapshot
{

	private final Map<Station, Point> nodeLocations;
	private final Map<Station, String> stationNames;

	private final double sceneX1;
	private final double sceneY1;
	private final double sceneX2;
	private final double sceneY2;
	private final double startX;
	private final double startY;

	private MapEditorSnapshot(Map<Station, Point> nodeLocations,
			Map<Station, String> stationNames, double sceneX1, double sceneY1,
			double sceneX2, double sceneY2, double startX, double startY)
	{
		this.nodeLocations = nodeLocations;
		this.stationNames = stationNames;
		this.sceneX1 = sceneX1;
		this.sceneY1 = sceneY1;
		this.sceneX2 = sceneX2;
		this.sceneY2 = sceneY2;
		this.startX = startX;
		this.startY = startY;
	}

	public static MapEditorSnapshot capture(MapEditor mapEditor)
	{
		MapView view = mapEditor.getView();
		ViewConfig config = view.getConfig();
		Rectangle scene = config.getScene();
		Coordinate start = config.getStartPosition();

		Map<Station, Point> nodeLocations = new HashMap<>();
		Map<Station, String> stationNames = new HashMap<>();

		for (Node node : view.getLineNetwork().nodes) {
			nodeLocations.put(node.station,
					new Point(node.location.getX(), node.location.getY()));
			stationNames.put(node.station, node.station.getName());
		}

		return new MapEditorSnapshot(nodeLocations, stationNames, scene.getX1(),
				scene.getY1(), scene.getX2(), scene.getY2(), start.getX(),
				start.getY());
	}

	public void apply(MapEditor mapEditor)
	{
		MapView view = mapEditor.getView();
		ViewConfig config = view.getConfig();
		Rectangle scene = config.getScene();
		Coordinate start = config.getStartPosition();

		scene.setX1(sceneX1);
		scene.setY1(sceneY1);
		scene.setX2(sceneX2);
		scene.setY2(sceneY2);
		start.setX(startX);
		start.setY(startY);

		for (Map.Entry<Station, String> entry : stationNames.entrySet()) {
			entry.getKey().setName(entry.getValue());
		}

		Map<Station, Node> stationToNode = view.getLineNetwork()
				.getStationToNode();
		for (Map.Entry<Station, Point> entry : nodeLocations.entrySet()) {
			Node node = stationToNode.get(entry.getKey());
			if (node != null) {
				Point location = entry.getValue();
				node.location = new Point(location.getX(), location.getY());
			}
		}

		for (Station station : nodeLocations.keySet()) {
			Node node = stationToNode.get(station);
			if (node != null) {
				LineNetworkUtil.updateEdges(node);
			}
		}

		mapEditor.triggerDataChanged();
		mapEditor.getMap().repaint();
		mapEditor.updateStationPanel();
	}

	public boolean isSameAs(MapEditorSnapshot other)
	{
		if (other == null) {
			return false;
		}
		if (Double.compare(sceneX1, other.sceneX1) != 0
				|| Double.compare(sceneY1, other.sceneY1) != 0
				|| Double.compare(sceneX2, other.sceneX2) != 0
				|| Double.compare(sceneY2, other.sceneY2) != 0
				|| Double.compare(startX, other.startX) != 0
				|| Double.compare(startY, other.startY) != 0) {
			return false;
		}
		if (nodeLocations.size() != other.nodeLocations.size()
				|| stationNames.size() != other.stationNames.size()) {
			return false;
		}
		for (Map.Entry<Station, Point> entry : nodeLocations.entrySet()) {
			Point otherPoint = other.nodeLocations.get(entry.getKey());
			if (otherPoint == null) {
				return false;
			}
			Point point = entry.getValue();
			if (Double.compare(point.getX(), otherPoint.getX()) != 0
					|| Double.compare(point.getY(), otherPoint.getY()) != 0) {
				return false;
			}
		}
		for (Map.Entry<Station, String> entry : stationNames.entrySet()) {
			String otherName = other.stationNames.get(entry.getKey());
			if (otherName == null || !otherName.equals(entry.getValue())) {
				return false;
			}
		}
		return true;
	}

}
