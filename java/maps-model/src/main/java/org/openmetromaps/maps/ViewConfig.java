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

import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

public class ViewConfig
{

	private Rectangle scene;
	private Coordinate startPosition;

	public ViewConfig(Rectangle scene, Coordinate startPosition)
	{
		this.scene = scene;
		this.startPosition = startPosition;
	}

	public Rectangle getScene()
	{
		return scene;
	}

	public void setScene(Rectangle scene)
	{
		this.scene = scene;
	}

	public Coordinate getStartPosition()
	{
		return startPosition;
	}

	public void setStartPosition(Coordinate startPosition)
	{
		this.startPosition = startPosition;
	}

}
