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
import java.awt.Window;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.openmetromaps.maps.actions.ExitAction;
import org.openmetromaps.maps.actions.ShowLabelsAction;
import org.openmetromaps.maps.model.ModelData;

import de.topobyte.awt.util.GridBagConstraintsEditor;

public class MapViewer
{

	private ModelData model;

	private ViewConfig viewConfig;

	private JFrame frame;

	private ScrollableAdvancedPanel map;

	public MapViewer(ModelData model)
	{
		this.model = model;

		viewConfig = ModelUtil.viewConfig(model);
	}

	public ModelData getModel()
	{
		return model;
	}

	public Window getFrame()
	{
		return frame;
	}

	public ScrollableAdvancedPanel getMap()
	{
		return map;
	}

	public void show()
	{
		frame = new JFrame("Map Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);

		build();

		frame.setVisible(true);
	}

	private void build()
	{
		setupContent();
		setupMenu();
	}

	private void setupMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);

		JMenu menuView = new JMenu("View");
		menuBar.add(menuView);

		menuFile.add(new ExitAction());

		addCheckbox(menuView, new ShowLabelsAction(this));
	}

	private void addCheckbox(JMenu menu, Action action)
	{
		menu.add(new JCheckBoxMenuItem(action));
	}

	private void setupContent()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		frame.setContentPane(panel);

		map = new ScrollableAdvancedPanel(model,
				PlanRenderer.StationMode.CONVEX, PlanRenderer.SegmentMode.CURVE,
				viewConfig.getStartPosition(), 10, 15, viewConfig.getBbox());

		GridBagConstraintsEditor c = new GridBagConstraintsEditor();
		c.weight(1, 1).fill(GridBagConstraints.BOTH);
		panel.add(map, c.getConstraints());
	}

}
