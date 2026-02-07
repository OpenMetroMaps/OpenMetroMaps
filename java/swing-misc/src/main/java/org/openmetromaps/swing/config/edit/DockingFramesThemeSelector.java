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

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.openmetromaps.swing.config.Configuration;

import bibliothek.gui.dock.common.theme.ThemeMap;

public class DockingFramesThemeSelector
		extends JComboBox<DockingFramesThemeSelector.Entry>
{

	private static final long serialVersionUID = 1L;

	private final List<Entry> entries = new ArrayList<>();

	public DockingFramesThemeSelector(Configuration configuration)
	{
		entries.add(new Entry(ThemeMap.KEY_BASIC_THEME, "Basic"));
		entries.add(new Entry(ThemeMap.KEY_BUBBLE_THEME, "Bubble"));
		entries.add(new Entry(ThemeMap.KEY_ECLIPSE_THEME, "Eclipse"));
		entries.add(new Entry(ThemeMap.KEY_FLAT_THEME, "Flat"));
		entries.add(new Entry(ThemeMap.KEY_SMOOTH_THEME, "Smooth"));

		setModel(new DefaultComboBoxModel<>(entries.toArray(new Entry[0])));
		setEditable(false);

		String theme = configuration.getDockingFramesTheme();
		int index = 0;
		if (theme != null) {
			for (int i = 0; i < entries.size(); i++) {
				if (theme.equals(entries.get(i).key)) {
					index = i;
					break;
				}
			}
		}
		setSelectedIndex(index);
	}

	public String getSelectedTheme()
	{
		Entry entry = (Entry) getSelectedItem();
		return entry == null ? null : entry.key;
	}

	static class Entry
	{

		private final String key;
		private final String value;

		Entry(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString()
		{
			return value;
		}
	}

}
