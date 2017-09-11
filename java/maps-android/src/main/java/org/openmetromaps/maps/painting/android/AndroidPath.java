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

package org.openmetromaps.maps.painting.android;

import org.openmetromaps.maps.painting.core.geom.Path;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;

public class AndroidPath extends android.graphics.Path implements Path
{

	@Override
	public void moveTo(double x, double y)
	{
		moveTo((float) x, (float) y);
	}

	@Override
	public void lineTo(double x, double y)
	{
		lineTo((float) x, (float) y);
	}

	@Override
	public void moveTo(Point p)
	{
		moveTo((float) p.x, (float) p.y);
	}

	@Override
	public void lineTo(Point p)
	{
		lineTo((float) p.x, (float) p.y);
	}

	@Override
	public void moveTo(Vector2 p)
	{
		moveTo((float) p.getX(), (float) p.getY());
	}

	@Override
	public void lineTo(Vector2 p)
	{
		lineTo((float) p.getX(), (float) p.getY());
	}

}
