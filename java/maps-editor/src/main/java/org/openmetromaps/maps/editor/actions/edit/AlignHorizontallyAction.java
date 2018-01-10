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

package org.openmetromaps.maps.editor.actions.edit;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openmetromaps.maps.Points;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.swing.util.EmptyIcon;

public class AlignHorizontallyAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory
			.getLogger(AlignHorizontallyAction.class);

	private static final long serialVersionUID = 1L;

	public AlignHorizontallyAction(MapEditor mapEditor)
	{
		super(mapEditor, "Align Horizontally",
				"Align selected stations horizontally (same x coordinate)");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Set<Node> nodes = mapEditor.getMapViewStatus().getSelectedNodes();

		List<Point> locations = new ArrayList<>();
		for (Node node : nodes) {
			locations.add(node.location);
		}
		Point mean = Points.mean(locations);

		for (Node node : nodes) {
			node.location = new Point(mean.getX(), node.location.getY());
		}

		for (Node node : nodes) {
			LineNetworkUtil.updateEdges(node);
		}

		mapEditor.getMap().repaint();
	}

}
