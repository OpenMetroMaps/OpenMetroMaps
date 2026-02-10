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

package org.openmetromaps.maps.editor;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import org.openmetromaps.maps.BaseMapWindowPanel;
import org.openmetromaps.maps.BaseMouseEventProcessor;
import org.openmetromaps.maps.editor.history.NodePositionsCommand;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.swing.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class MapEditorMouseEventProcessor
		extends BaseMouseEventProcessor<BaseMapWindowPanel>
{

	final static Logger logger = LoggerFactory
			.getLogger(MapEditorMouseEventProcessor.class);

	private MapEditor mapEditor;

	public MapEditorMouseEventProcessor(MapEditor mapEditor)
	{
		super(mapEditor.getMap());
		this.mapEditor = mapEditor;
	}

	private boolean draggingNodes = false;
	private java.awt.Point lastPoint = null;
	private Map<Station, Point> dragStartPositions = null;

	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		Node node = mapEditor.mouseNode(e.getX(), e.getY());

		boolean control = Util.isControlPressed(e);
		boolean shift = Util.isShiftPressed(e);
		boolean onNode = node != null;
		int numSelected = mapEditor.getMapViewStatus().getNumSelectedNodes();
		boolean someSelected = numSelected != 0;

		if (e.getButton() == MouseEvent.BUTTON1) {
			if (onNode && !control) {
				if (!shift) {
					mapEditor.select(node);
				} else {
					mapEditor.toggleSelected(node);
				}
			} else if (control && someSelected) {
				draggingNodes = true;
				lastPoint = e.getPoint();
				dragStartPositions = NodePositionsCommand.capture(
						mapEditor.getMapViewStatus().getSelectedNodes());
			}
		}

		if (e.getButton() == MouseEvent.BUTTON3) {
			mapEditor.selectNone();
		}

		mapEditor.getMap().repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);
		if (e.getButton() == MouseEvent.BUTTON1) {
			boolean wasDragging = draggingNodes;
			draggingNodes = false;
			if (wasDragging) {
				Map<Station, Point> dragEndPositions = capturePositions(
						dragStartPositions);
				NodePositionsCommand command = NodePositionsCommand.create(
						"Move stations", dragStartPositions, dragEndPositions);
				mapEditor.getHistory().record(command);
				dragStartPositions = null;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		mapEditor.updateStatusBar(e.getX(), e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (!draggingNodes) {
			super.mouseDragged(e);
			return;
		}

		if (draggingNodes) {
			java.awt.Point currentPoint = e.getPoint();
			int dx = currentPoint.x - lastPoint.x;
			int dy = currentPoint.y - lastPoint.y;
			lastPoint = currentPoint;

			for (Node node : mapEditor.getMapViewStatus().getSelectedNodes()) {
				update(node, dx, dy);
			}

			for (Node node : mapEditor.getMapViewStatus().getSelectedNodes()) {
				LineNetworkUtil.updateEdges(node);
			}

			mapEditor.triggerDataChanged();
			c.repaint();
		}
	}

	private Map<Station, Point> capturePositions(Map<Station, Point> reference)
	{
		if (reference == null) {
			return null;
		}
		Map<Station, Point> positions = new HashMap<>();
		Map<Station, Node> stationToNode = mapEditor.getView().getLineNetwork()
				.getStationToNode();
		for (Station station : reference.keySet()) {
			Node node = stationToNode.get(station);
			if (node != null) {
				positions.put(station,
						new Point(node.location.getX(), node.location.getY()));
			}
		}
		return positions;
	}

	private void update(Node node, int dx, int dy)
	{
		Point old = node.location;

		double oldX = ViewportUtil.getViewX(mapEditor.getMap(), old.getX());
		double oldY = ViewportUtil.getViewY(mapEditor.getMap(), old.getY());

		double newX = oldX + dx;
		double newY = oldY + dy;

		double x = ViewportUtil.getRealX(mapEditor.getMap(), newX);
		double y = ViewportUtil.getRealY(mapEditor.getMap(), newY);

		node.location = new Point(x, y);
	}

}
