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

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import org.openmetromaps.swing.Theme;
import org.openmetromaps.swing.config.Configuration;

public class ThemeSelector extends JComboBox<Theme>
{

	private static final long serialVersionUID = 1L;

	public ThemeSelector(Configuration configuration)
	{
		super(Theme.values());
		setEditable(false);
		setRenderer(new ThemeRenderer());

		Theme theme = configuration.getTheme();
		setSelectedItem(theme == null ? Theme.FLATLAF_LIGHT : theme);
	}

	public Theme getSelectedTheme()
	{
		return (Theme) getSelectedItem();
	}

	static class ThemeRenderer extends DefaultListCellRenderer
	{

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus)
		{
			Component c = super.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);
			if (value instanceof Theme) {
				setText(toLabel((Theme) value));
			}
			return c;
		}

		private String toLabel(Theme theme)
		{
			switch (theme) {
			default:
			case DEFAULT:
				return "Default";
			case METAL:
				return "Metal";
			case FLATLAF_LIGHT:
				return "FlatLaf Light";
			case FLATLAF_DARK:
				return "FlatLaf Dark";
			}
		}
	}

}
