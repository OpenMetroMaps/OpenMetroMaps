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

package org.openmetromaps.rawstations;

public class Change
{

	private String line;
	private String towards;
	private String reverseLine;
	private String reverseTowards;
	private String at;
	private Location location;
	private String changeLine;
	private String changeTowards;
	private String changeLineRegex;
	private boolean deriveReverseFrom;

	public Change(String line, String towards, String reverseLine,
			String reverseTowards, String at, Location location,
			String changeLine, String changeTowards, String changeLineRegex,
			boolean deriveReverseFrom)
	{
		this.line = line;
		this.towards = towards;
		this.reverseLine = reverseLine;
		this.reverseTowards = reverseTowards;
		this.at = at;
		this.location = location;
		this.changeLine = changeLine;
		this.changeTowards = changeTowards;
		this.changeLineRegex = changeLineRegex;
		this.deriveReverseFrom = deriveReverseFrom;
	}

	public String getLine()
	{
		return line;
	}

	public String getTowards()
	{
		return towards;
	}

	public String getReverseLine()
	{
		return reverseLine;
	}

	public String getReverseTowards()
	{
		return reverseTowards;
	}

	public String getAt()
	{
		return at;
	}

	public Location getLocation()
	{
		return location;
	}

	public String getChangeLine()
	{
		return changeLine;
	}

	public String getChangeTowards()
	{
		return changeTowards;
	}

	public String getChangeLineRegex()
	{
		return changeLineRegex;
	}

	public boolean isDeriveReverseFrom()
	{
		return deriveReverseFrom;
	}

}
