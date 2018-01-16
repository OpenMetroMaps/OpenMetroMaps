// Copyright 2018 Sebastian Kuerten
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

package org.openmetromaps.stations;

public class Change
{

	private String line;
	private String towards;
	private String at;
	private Location location;
	private Matcher matcher;

	public Change(String line, String towards, String at, Location location,
			Matcher matcher)
	{
		this.line = line;
		this.towards = towards;
		this.at = at;
		this.location = location;
		this.matcher = matcher;
	}

	public String getLine()
	{
		return line;
	}

	public String getTowards()
	{
		return towards;
	}

	public String getAt()
	{
		return at;
	}

	public Location getLocation()
	{
		return location;
	}

	public Matcher getMatcher()
	{
		return matcher;
	}

}
