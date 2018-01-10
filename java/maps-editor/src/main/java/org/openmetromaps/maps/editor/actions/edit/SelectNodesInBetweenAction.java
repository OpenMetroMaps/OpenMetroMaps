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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.openmetromaps.maps.graph.LineConnectionResult;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.graph.NodeConnectionResult;
import org.openmetromaps.maps.graph.NodesInBetweenResult;
import org.openmetromaps.maps.model.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.swing.util.EmptyIcon;

public class SelectNodesInBetweenAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory
			.getLogger(SelectNodesInBetweenAction.class);

	private static final long serialVersionUID = 1L;

	public SelectNodesInBetweenAction(MapEditor mapEditor)
	{
		super(mapEditor, "Select Nodes In Between",
				"Add the nodes between the two selected ones to the selection");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Set<Node> nodes = mapEditor.getMapViewStatus().getSelectedNodes();

		if (nodes.size() != 2) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Please select exactly two stations.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Iterator<Node> iterator = nodes.iterator();
		Node node1 = iterator.next();
		Node node2 = iterator.next();
		logger.debug(String.format("Trying to connect: '%s' and '%s'",
				node1.station.getName(), node2.station.getName()));

		NodeConnectionResult connection = LineNetworkUtil.findConnection(node1,
				node2);

		if (!connection.isConnected()) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Please select two stations that are connected with a line.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Line line = connection.getCommonLines().iterator().next();

		logger.debug("Common line: " + line.getName());

		LineConnectionResult lineConnection = LineNetworkUtil
				.findConnection(line, node1, node2);

		if (!lineConnection.isValid()) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Unable to determine connection between stations.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int idxNode1 = lineConnection.getIdxNode1();
		int idxNode2 = lineConnection.getIdxNode2();

		LineNetwork lineNetwork = mapEditor.getMap().getLineNetwork();
		NodesInBetweenResult nodesBetween = LineNetworkUtil
				.getNodesBetween(lineNetwork, line, idxNode1, idxNode2);

		List<Node> between = nodesBetween.getNodes();

		for (Node node : between) {
			mapEditor.getMapViewStatus().selectNode(node);
		}

		mapEditor.updateStationPanel();
		mapEditor.getMap().repaint();
	}

}
