// Copyright 2026 Sebastian Kuerten
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

package org.openmetromaps.swing.config.edit;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import org.openmetromaps.swing.config.Configuration;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.swing.util.BorderHelper;

public class AppearancePane extends JPanel implements Scrollable
{

	private static final long serialVersionUID = 1L;

	private final ThemeSelector themeSelector;
	private final DockingFramesThemeSelector dockingFramesThemeSelector;

	public AppearancePane(Configuration configuration)
	{
		setLayout(new GridBagLayout());
		BorderHelper.addEmptyBorder(this, 0, 5, 0, 5);

		themeSelector = new ThemeSelector(configuration);
		dockingFramesThemeSelector = new DockingFramesThemeSelector(
				configuration);

		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor ce = new GridBagConstraintsEditor(c);
		ce.fill(GridBagConstraints.BOTH).gridY(0);

		JLabel labelTheme = new JLabel("Theme:");
		JLabel labelDocking = new JLabel("Docking Frames Theme:");
		BorderHelper.addEmptyBorder(labelTheme, 5, 0, 5, 0);
		BorderHelper.addEmptyBorder(labelDocking, 5, 0, 5, 0);

		addAsRow(labelTheme, ce);
		addAsRow(themeSelector, ce);
		addAsRow(labelDocking, ce);
		addAsRow(dockingFramesThemeSelector, ce);
	}

	private void addAsRow(javax.swing.JComponent component,
			GridBagConstraintsEditor ce)
	{
		ce.weightX(1).gridWidth(2).gridX(0);
		add(component, ce.getConstraints());
		ce.getConstraints().gridy++;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction)
	{
		return 1;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction)
	{
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}

	public void setValues(Configuration configuration)
	{
		configuration.setTheme(themeSelector.getSelectedTheme());
		configuration.setDockingFramesTheme(
				dockingFramesThemeSelector.getSelectedTheme());
	}

}
