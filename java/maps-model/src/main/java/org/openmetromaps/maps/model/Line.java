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

package org.openmetromaps.maps.model;

import java.util.List;

public class Line extends Entity
{

	private String name;
	private String color;
	private boolean circular;
	private List<Stop> stops;

	/**
	 * @param id
	 *            a unique identifier among a set of lines
	 * @param color
	 *            a color value in the format '#RRGGBB'
	 */
	public Line(int id, String name, String color, boolean circular,
			List<Stop> stops)
	{
		super(id);
		this.name = name;
		this.color = color;
		this.circular = circular;
		this.stops = stops;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	public boolean isCircular()
	{
		return circular;
	}

	public void setCircular(boolean circular)
	{
		this.circular = circular;
	}

	public List<Stop> getStops()
	{
		return stops;
	}

	public void setStops(List<Stop> stops)
	{
		this.stops = stops;
	}

}
