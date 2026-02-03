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

import de.topobyte.lightgeom.lina.Point;

public class NodePositionsCommand implements HistoryCommand
{

	private final String name;
	private final Map<Node, Point> before;
	private final Map<Node, Point> after;

	public static Map<Node, Point> capture(Iterable<Node> nodes)
	{
		Map<Node, Point> positions = new HashMap<>();
		for (Node node : nodes) {
			positions.put(node,
					new Point(node.location.getX(), node.location.getY()));
		}
		return positions;
	}

	public static NodePositionsCommand create(String name,
			Map<Node, Point> before, Map<Node, Point> after)
	{
		if (before == null || after == null) {
			return null;
		}
		if (positionsEqual(before, after)) {
			return null;
		}
		return new NodePositionsCommand(name, before, after);
	}

	private NodePositionsCommand(String name, Map<Node, Point> before,
			Map<Node, Point> after)
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
			Map<Node, Point> positions)
	{
		for (Map.Entry<Node, Point> entry : positions.entrySet()) {
			Point location = entry.getValue();
			entry.getKey().location = new Point(location.getX(),
					location.getY());
		}

		for (Node node : positions.keySet()) {
			LineNetworkUtil.updateEdges(node);
		}

		mapEditor.triggerDataChanged();
		mapEditor.getMap().repaint();
		mapEditor.updateStationPanel();
	}

	private static boolean positionsEqual(Map<Node, Point> a,
			Map<Node, Point> b)
	{
		if (a.size() != b.size()) {
			return false;
		}
		for (Map.Entry<Node, Point> entry : a.entrySet()) {
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
