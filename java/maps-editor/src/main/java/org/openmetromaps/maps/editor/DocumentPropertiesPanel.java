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

package org.openmetromaps.maps.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openmetromaps.maps.MapView;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

public class DocumentPropertiesPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	private MapView view;

	private JTextField inputWidth;
	private JTextField inputHeight;
	private JTextField inputStartX;
	private JTextField inputStartY;

	public DocumentPropertiesPanel(MapView view)
	{
		super(new GridBagLayout());
		this.view = view;

		setupLayout();

		initValues();
	}

	private void setupLayout()
	{
		JLabel labelWidth = new JLabel("width:");
		JLabel labelHeight = new JLabel("height:");
		JLabel labelStartX = new JLabel("start x:");
		JLabel labelStartY = new JLabel("start y:");

		inputWidth = new JTextField();
		inputHeight = new JTextField();
		inputStartX = new JTextField();
		inputStartY = new JTextField();

		GridBagConstraintsEditor ce = new GridBagConstraintsEditor();
		GridBagConstraints c = ce.getConstraints();

		ce.fill(GridBagConstraints.BOTH);
		ce.weight(0, 0);
		ce.gridPos(0, 0);
		add(labelWidth, c);
		ce.gridPos(0, 1);
		add(labelHeight, c);
		ce.gridPos(0, 2);
		add(labelStartX, c);
		ce.gridPos(0, 3);
		add(labelStartY, c);

		ce.weight(1, 0);
		ce.gridPos(1, 0);
		add(inputWidth, c);
		ce.gridPos(1, 1);
		add(inputHeight, c);
		ce.gridPos(1, 2);
		add(inputStartX, c);
		ce.gridPos(1, 3);
		add(inputStartY, c);

		ce.gridPos(0, 4);
		ce.weight(1, 1);
		ce.gridWidth(2);
		add(new JPanel(), c);
	}

	private void initValues()
	{
		Rectangle scene = view.getConfig().getScene();
		Coordinate start = view.getConfig().getStartPosition();
		inputWidth.setText(String.format("%.2f", scene.getWidth()));
		inputHeight.setText(String.format("%.2f", scene.getHeight()));
		inputStartX.setText(String.format("%.2f", start.getX()));
		inputStartY.setText(String.format("%.2f", start.getY()));
	}

	public MapView getView()
	{
		return view;
	}

	public String getWidthValue()
	{
		return inputWidth.getText();
	}

	public String getHeightValue()
	{
		return inputHeight.getText();
	}

	public String getStartXValue()
	{
		return inputStartX.getText();
	}

	public String getStartYValue()
	{
		return inputStartY.getText();
	}

}
