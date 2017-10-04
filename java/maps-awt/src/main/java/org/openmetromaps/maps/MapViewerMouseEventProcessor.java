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

import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.swing.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.geo.Coordinate;

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

	private boolean draggingNode = false;
	private Node dragNode = null;

	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		Node node = mapViewer.mouseNode(e.getX(), e.getY());

		if (e.getButton() == MouseEvent.BUTTON1) {
			mapViewer.select(node);
			if (Util.isControlPressed(e)) {
				if (node == null) {
					return;
				}
				draggingNode = true;
				dragNode = node;
			}
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

			double lon = mapWindow.getPositionLon(currentPoint.x);
			double lat = mapWindow.getPositionLat(currentPoint.y);
			dragNode.location = new Coordinate(lon, lat);

			LineNetworkUtil.updateEdges(dragNode);

			mapViewer.triggerDataChanged();
			c.repaint();
		}
	}

}
