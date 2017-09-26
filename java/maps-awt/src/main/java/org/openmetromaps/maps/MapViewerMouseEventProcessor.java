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

package org.openmetromaps.maps;

import java.awt.event.MouseEvent;

import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.swing.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapViewerMouseEventProcessor extends BaseMouseEventProcessor
{

	final static Logger logger = LoggerFactory
			.getLogger(MapViewerMouseEventProcessor.class);

	private MapViewer mapViewer;

	public MapViewerMouseEventProcessor(MapViewer mapViewer)
	{
		super(mapViewer.getMap(), mapViewer.getMap().getMapWindow());
		this.mapViewer = mapViewer;
	}

	private java.awt.Point lastPoint;
	private boolean draggingNode = false;
	private Node dragNode = null;

	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (!Util.isControlPressed(e)) {
				return;
			}
			Node node = mapViewer.mouseNode(e.getX(), e.getY());
			if (node == null) {
				return;
			}
			lastPoint = e.getPoint();
			draggingNode = true;
			dragNode = node;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);
		if (e.getButton() == MouseEvent.BUTTON1) {
			draggingNode = false;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		mapViewer.updateStatusBar(e.getX(), e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (!draggingNode) {
			super.mouseDragged(e);
			return;
		}

		if (draggingNode) {
			java.awt.Point currentPoint = e.getPoint();
			int dx = lastPoint.x - currentPoint.x;
			int dy = lastPoint.y - currentPoint.y;
			lastPoint = currentPoint;
			// down right movement is negative for both
			logger.info(String.format("Move %s: %d,%d",
					dragNode.station.getName(), dx, dy));
			// TODO: implement coordinate update
			c.repaint();
		}
	}

}
