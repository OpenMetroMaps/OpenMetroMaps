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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openmetromaps.maps.graph.Node;

import de.topobyte.awt.util.GridBagConstraintsEditor;

public class StationPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	private JTextField inputName;
	private JTextField inputX;
	private JTextField inputY;

	public StationPanel()
	{
		super(new GridBagLayout());

		JLabel labelName = new JLabel("name:");
		JLabel labelX = new JLabel("x:");
		JLabel labelY = new JLabel("y:");

		inputName = new JTextField();
		inputX = new JTextField();
		inputY = new JTextField();

		GridBagConstraintsEditor ce = new GridBagConstraintsEditor();
		GridBagConstraints c = ce.getConstraints();

		ce.fill(GridBagConstraints.BOTH);
		ce.weight(0, 0);
		ce.gridPos(0, 0);
		add(labelName, c);
		ce.gridPos(0, 1);
		add(labelX, c);
		ce.gridPos(0, 2);
		add(labelY, c);

		ce.weight(1, 0);
		ce.gridPos(1, 0);
		add(inputName, c);
		ce.gridPos(1, 1);
		add(inputX, c);
		ce.gridPos(1, 2);
		add(inputY, c);

		ce.gridPos(0, 3);
		ce.weight(1, 1);
		ce.gridWidth(2);
		add(new JPanel(), c);
	}

	public void setNode(Node node)
	{
		boolean nonNullNode = node != null;

		inputName.setEnabled(nonNullNode);
		inputX.setEnabled(nonNullNode);
		inputY.setEnabled(nonNullNode);

		if (!nonNullNode) {
			inputName.setText("");
			inputX.setText("");
			inputY.setText("");
			return;
		}

		inputName.setText(node.station.getName());
		String x = String.format("%.4f", node.location.lon);
		String y = String.format("%.4f", node.location.lat);
		inputX.setText(x);
		inputY.setText(y);
	}

}