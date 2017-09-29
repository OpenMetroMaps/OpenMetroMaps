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

import de.topobyte.adt.geo.Coordinate;

public class Stop
{

	private Coordinate location;
	private Station station;
	private Line line;

	public Stop(Station station, Line line)
	{
		this.station = station;
		this.line = line;
	}

	public Coordinate getLocation()
	{
		return location;
	}

	public void setLocation(Coordinate location)
	{
		this.location = location;
	}

	public Station getStation()
	{
		return station;
	}

	public void setStation(Station station)
	{
		this.station = station;
	}

	public Line getLine()
	{
		return line;
	}

	public void setLine(Line line)
	{
		this.line = line;
	}

}
