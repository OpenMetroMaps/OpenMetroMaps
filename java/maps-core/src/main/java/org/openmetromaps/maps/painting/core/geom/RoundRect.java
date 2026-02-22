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

package org.openmetromaps.maps.painting.core.geom;

/**
 * An axis-aligned rectangle with optional rounded corners defined by an arc
 * radius. The position (x, y) is the top-left corner.
 */
public class RoundRect
{

	private double x;
	private double y;
	private double width;
	private double height;
	private double arcWidth;
	private double arcHeight;

	public RoundRect(double x, double y, double width, double height,
			double arcWidth, double arcHeight)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double getWidth()
	{
		return width;
	}

	public void setWidth(double width)
	{
		this.width = width;
	}

	public double getHeight()
	{
		return height;
	}

	public void setHeight(double height)
	{
		this.height = height;
	}

	public double getArcWidth()
	{
		return arcWidth;
	}

	public void setArcWidth(double arcWidth)
	{
		this.arcWidth = arcWidth;
	}

	public double getArcHeight()
	{
		return arcHeight;
	}

	public void setArcHeight(double arcHeight)
	{
		this.arcHeight = arcHeight;
	}

}
