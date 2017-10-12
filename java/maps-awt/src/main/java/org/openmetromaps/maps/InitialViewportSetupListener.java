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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import de.topobyte.viewports.geometry.Coordinate;

public class InitialViewportSetupListener extends ComponentAdapter
{

	private BaseMapWindowPanel panel;
	private Coordinate start;

	public InitialViewportSetupListener(BaseMapWindowPanel panel, Coordinate start)
	{
		this.panel = panel;
		this.start = start;
		panel.addComponentListener(this);
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		super.componentResized(e);
		panel.removeComponentListener(this);
		panel.setPositionX(-start.getX() + panel.getWidth() / 2);
		panel.setPositionY(-start.getY() + panel.getHeight() / 2);
	}

}
