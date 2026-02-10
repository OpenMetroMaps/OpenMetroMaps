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

import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Station;

import de.topobyte.lightgeom.lina.Point;

public class NodePositionsCommand implements HistoryCommand
{

	private final String name;
	private final Map<Station, Point> before;
	private final Map<Station, Point> after;

	public static Map<Station, Point> capture(Iterable<Node> nodes)
	{
		Map<Station, Point> positions = new HashMap<>();
		for (Node node : nodes) {
			positions.put(node.station,
					new Point(node.location.getX(), node.location.getY()));
		}
		return positions;
	}

	public static NodePositionsCommand create(String name,
			Map<Station, Point> before, Map<Station, Point> after)
	{
		if (before == null || after == null) {
			return null;
		}
		if (positionsEqual(before, after)) {
			return null;
		}
		return new NodePositionsCommand(name, before, after);
	}

	private NodePositionsCommand(String name, Map<Station, Point> before,
			Map<Station, Point> after)
	{
		this.name = name;
		this.before = before;
		this.after = after;
	}

	@Override
	public void undo(MapEditor mapEditor)
	{
		applyPositions(mapEditor, before);
	}

	@Override
	public void redo(MapEditor mapEditor)
	{
		applyPositions(mapEditor, after);
	}

	@Override
	public String getName()
	{
		return name;
	}

	private static void applyPositions(MapEditor mapEditor,
			Map<Station, Point> positions)
	{
		Map<Station, Node> stationToNode = mapEditor.getView().getLineNetwork()
				.getStationToNode();
		for (Map.Entry<Station, Point> entry : positions.entrySet()) {
			Node node = stationToNode.get(entry.getKey());
			if (node != null) {
				Point location = entry.getValue();
				node.location = new Point(location.getX(), location.getY());
			}
		}

		for (Station station : positions.keySet()) {
			Node node = stationToNode.get(station);
			if (node != null) {
				LineNetworkUtil.updateEdges(node);
			}
		}

		mapEditor.triggerDataChanged();
		mapEditor.getMap().repaint();
		mapEditor.updateStationPanel();
	}

	private static boolean positionsEqual(Map<Station, Point> a,
			Map<Station, Point> b)
	{
		if (a.size() != b.size()) {
			return false;
		}
		for (Map.Entry<Station, Point> entry : a.entrySet()) {
			Point other = b.get(entry.getKey());
			if (other == null) {
				return false;
			}
			Point point = entry.getValue();
			if (Double.compare(point.getX(), other.getX()) != 0
					|| Double.compare(point.getY(), other.getY()) != 0) {
				return false;
			}
		}
		return true;
	}

}
