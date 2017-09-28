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

package org.openmetromaps.maps.xml;

import java.util.List;

public class XmlLine
{

	private String name;
	private String color;
	private boolean circular;
	private List<XmlStation> stops;

	public XmlLine(String name, String color, boolean circular,
			List<XmlStation> stops)
	{
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

	public List<XmlStation> getStops()
	{
		return stops;
	}

	public void setStops(List<XmlStation> stops)
	{
		this.stops = stops;
	}

}
