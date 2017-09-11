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

package org.openmetromaps.maps.painting.core.geom;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;

public class LineSegment
{

	private double x1;
	private double y1;
	private double x2;
	private double y2;

	public LineSegment(double x1, double y1, double x2, double y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public LineSegment(Point p1, Point p2)
	{
		x1 = p1.x;
		y1 = p1.y;
		x2 = p2.x;
		y2 = p2.y;
	}

	public LineSegment(Vector2 p1, Vector2 p2)
	{
		x1 = p1.getX();
		y1 = p1.getY();
		x2 = p2.getX();
		y2 = p2.getY();
	}

	public double getX1()
	{
		return x1;
	}

	public void setX1(double x1)
	{
		this.x1 = x1;
	}

	public double getY1()
	{
		return y1;
	}

	public void setY1(double y1)
	{
		this.y1 = y1;
	}

	public double getX2()
	{
		return x2;
	}

	public void setX2(double x2)
	{
		this.x2 = x2;
	}

	public double getY2()
	{
		return y2;
	}

	public void setY2(double y2)
	{
		this.y2 = y2;
	}

}
