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

package org.openmetromaps.swing.config;

import org.openmetromaps.swing.Theme;

import bibliothek.gui.dock.common.theme.ThemeMap;

public class Configuration
{

	private Theme theme = Theme.FLATLAF_LIGHT;
	private String dockingFramesTheme = ThemeMap.KEY_BASIC_THEME;

	public Configuration()
	{
		// defaults set in field initializers
	}

	public Configuration(Configuration other)
	{
		this.theme = other.theme;
		this.dockingFramesTheme = other.dockingFramesTheme;
	}

	public static Configuration createDefaultConfiguration()
	{
		return new Configuration();
	}

	public Theme getTheme()
	{
		return theme;
	}

	public void setTheme(Theme theme)
	{
		this.theme = theme;
	}

	public String getDockingFramesTheme()
	{
		return dockingFramesTheme;
	}

	public void setDockingFramesTheme(String dockingFramesTheme)
	{
		this.dockingFramesTheme = dockingFramesTheme;
	}

}
