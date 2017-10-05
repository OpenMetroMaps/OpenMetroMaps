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
import java.util.Set;

import javax.swing.JOptionPane;

import org.openmetromaps.maps.MapEditor;
import org.openmetromaps.maps.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.swing.util.EmptyIcon;
import de.topobyte.swing.util.action.SimpleAction;

public class DistributeEvenlyAction extends SimpleAction
{

	final static Logger logger = LoggerFactory
			.getLogger(DistributeEvenlyAction.class);

	private static final long serialVersionUID = 1L;

	private MapEditor mapEditor;

	public DistributeEvenlyAction(MapEditor mapEditor)
	{
		super("Distribute Evenly",
				"Distribute the stations between two selected ones evenly");
		this.mapEditor = mapEditor;
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

		boolean connected = false;
		// TODO: find out if stations are connected with a line

		if (!connected) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Please select two stations that are connected with a line.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// TODO: calculate and perform action
	}

}
