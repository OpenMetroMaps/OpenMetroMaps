// Copyright 2018 Sebastian Kuerten
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

package org.openmetromaps.maps.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

import org.openmetromaps.maps.BaseMapWindowPanel;
import org.openmetromaps.maps.BaseMouseEventProcessor;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.swing.JPopupMenuWithTitle;
import org.openmetromaps.swing.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapViewerMouseEventProcessor
		extends BaseMouseEventProcessor<BaseMapWindowPanel>
{

	final static Logger logger = LoggerFactory
			.getLogger(MapViewerMouseEventProcessor.class);

	private MapViewer mapViewer;

	public MapViewerMouseEventProcessor(MapViewer mapViewer)
	{
		super(mapViewer.getMap());
		this.mapViewer = mapViewer;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		Node node = mapViewer.mouseNode(e.getX(), e.getY());

		boolean control = Util.isControlPressed(e);
		boolean onNode = node != null;

		if (e.getButton() == MouseEvent.BUTTON1) {
			if (onNode && control) {
				Station station = node.station;
				Coordinate location = station.getLocation();
				System.out.println(String.format("press: %s @ %f,%f",
						station.getName(), location.getLongitude(),
						location.getLatitude()));
			}
		}

		if (e.getButton() == MouseEvent.BUTTON3) {
			if (onNode) {
				showPopup(e, node);
			}
		}

		mapViewer.getMap().repaint();
	}

	private void showPopup(MouseEvent e, Node node)
	{
		Station station = node.station;

		JPopupMenuWithTitle menu = new JPopupMenuWithTitle(station.getName());

		JMenuItem itemShowOnMap = new JMenuItem(new OpenOnMapAction(node));
		JMenuItem itemProperties = new JMenuItem(
				new ShowPropertiesAction(node));
		menu.add(itemShowOnMap);
		menu.add(itemProperties);

		menu.show(mapViewer.getMap(), e.getX(), e.getY());
		menu.setVisible(true);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		mapViewer.updateStatusBar(e.getX(), e.getY());
	}

	private class ShowPropertiesAction extends NodeAction
	{

		private static final long serialVersionUID = 1L;

		public ShowPropertiesAction(Node node)
		{
			super(node);
			setName("Properties");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Station station = node.station;
			System.out.println(
					String.format("show properties: %s", station.getName()));
		}

	}

	private class OpenOnMapAction extends NodeAction
	{

		private static final long serialVersionUID = 1L;

		public OpenOnMapAction(Node node)
		{
			super(node);
			setName("Show on map");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Station station = node.station;
			Coordinate location = station.getLocation();
			System.out.println(
					String.format("open on map: %s @ %f,%f", station.getName(),
							location.getLongitude(), location.getLatitude()));
		}

	}

}
