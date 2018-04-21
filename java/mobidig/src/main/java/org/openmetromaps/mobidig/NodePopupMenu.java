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

package org.openmetromaps.mobidig;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.swing.JPopupMenuWithTitle;

import de.topobyte.jeography.viewer.core.Viewer;

public class NodePopupMenu extends JPopupMenuWithTitle
{

	private static final long serialVersionUID = 1L;

	private MapViewer mapViewer;

	public NodePopupMenu(MapViewer mapViewer, Node node)
	{
		super(node.station.getName());
		this.mapViewer = mapViewer;

		JMenuItem itemShowOnMap = new JMenuItem(new OpenOnMapAction(node));
		JMenuItem itemProperties = new JMenuItem(
				new ShowPropertiesAction(node));

		add(itemShowOnMap);
		add(itemProperties);
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

			Viewer viewer = mapViewer.getMapViewer();
			if (viewer == null) {
				return;
			}
			viewer.getMapWindow().gotoLonLat(location.getLongitude(),
					location.getLatitude());
			viewer.getMapWindow().zoom(15);
			viewer.repaint();
		}

	}

}
