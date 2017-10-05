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

package org.openmetromaps.maps.actions;

import java.awt.event.ActionEvent;

import org.openmetromaps.maps.MapEditor;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.swing.util.EmptyIcon;
import de.topobyte.swing.util.action.SimpleAction;

public class SelectAllAction extends SimpleAction
{

	final static Logger logger = LoggerFactory.getLogger(SelectAllAction.class);

	private static final long serialVersionUID = 1L;

	private MapEditor mapEditor;

	public SelectAllAction(MapEditor mapEditor)
	{
		super("Select All", "Select all stations");
		this.mapEditor = mapEditor;
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		LineNetwork lineNetwork = mapEditor.getModel().getViews().get(0)
				.getLineNetwork();
		for (Node node : lineNetwork.nodes) {
			mapEditor.getMapViewStatus().selectNode(node);
		}
		mapEditor.updateStationPanel();
		mapEditor.getMap().repaint();
	}

}
