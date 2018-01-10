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

package org.openmetromaps.maps.editor.config;

public class PermanentConfiguration
{

	private String lookAndFeel = null;
	private String dockingFramesTheme = null;

	public static PermanentConfiguration createDefaultConfiguration()
	{
		PermanentConfiguration configuration = new PermanentConfiguration();

		return configuration;
	}

	public String getLookAndFeel()
	{
		return lookAndFeel;
	}

	public void setLookAndFeel(String lookAndFeel)
	{
		this.lookAndFeel = lookAndFeel;
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
