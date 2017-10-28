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

public class Station extends Entity
{

	private String name;
	private Coordinate location;
	private List<Stop> stops;

	public Station(int id, String name, Coordinate location, List<Stop> stops)
	{
		super(id);
		this.name = name;
		this.location = location;
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

	public Coordinate getLocation()
	{
		return location;
	}

	public void setLocation(Coordinate location)
	{
		this.location = location;
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
