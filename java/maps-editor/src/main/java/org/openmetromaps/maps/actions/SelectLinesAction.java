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
import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.MapEditor;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.swing.util.EmptyIcon;

public class SelectLinesAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory
			.getLogger(SelectLinesAction.class);

	private static final long serialVersionUID = 1L;

	public SelectLinesAction(MapEditor mapEditor)
	{
		super(mapEditor, "Select Lines",
				"Choose lines and select all their stations");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		LineNetwork lineNetwork = mapEditor.getModel().getViews().get(0)
				.getLineNetwork();

		// TODO: show dialog, let user pick lines and get the list of nodes from
		// that selection

		List<Node> nodes = new ArrayList<>();

		for (Node node : nodes) {
			mapEditor.getMapViewStatus().selectNode(node);
		}
		mapEditor.updateStationPanel();
		mapEditor.getMap().repaint();
	}

}
