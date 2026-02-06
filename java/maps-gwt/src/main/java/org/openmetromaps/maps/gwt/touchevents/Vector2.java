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

package org.openmetromaps.maps.gwt.touchevents;

import de.topobyte.formatting.Formatting;

public class Vector2
{

	private float x;
	private float y;

	public Vector2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public Vector2(Point c)
	{
		this(c.getX(), c.getY());
	}

	public Vector2(Point from, Point to)
	{
		this(to.getX() - from.getX(), to.getY() - from.getY());
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public void set(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString()
	{
		return Formatting.format("%f,%f", x, y);
	}

	public Vector2 copy()
	{
		return new Vector2(x, y);
	}

	public void add(Vector2 other)
	{
		x += other.x;
		y += other.y;
	}

	public void add(float ox, float oy)
	{
		x += ox;
		y += oy;
	}

	public void sub(Vector2 other)
	{
		x -= other.x;
		y -= other.y;
	}

	public void sub(float ox, float oy)
	{
		x -= ox;
		y -= oy;
	}

	public void mult(float lambda)
	{
		x *= lambda;
		y *= lambda;
	}

	public void divide(float lambda)
	{
		x /= lambda;
		y /= lambda;
	}

	public float dotProduct(Vector2 other)
	{
		return x * other.x + y * other.y;
	}

	public float norm()
	{
		return (float) Math.sqrt(x * x + y * y);
	}

	public float length()
	{
		return (float) Math.sqrt(x * x + y * y);
	}

	public float length2()
	{
		return x * x + y * y;
	}

	public void normalize()
	{
		float norm = norm();
		divide(norm);
	}

	public void perpendicularLeft()
	{
		float ox = x;
		x = -y;
		y = ox;
	}

	public void perpendicularRight()
	{
		float ox = x;
		x = y;
		y = -ox;
	}

}