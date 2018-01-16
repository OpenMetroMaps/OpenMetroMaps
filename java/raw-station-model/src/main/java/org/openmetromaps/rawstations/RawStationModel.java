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

import java.util.List;

public class RawStationModel
{

	private String version;
	private List<Change> changes;
	private List<Exit> exits;

	public RawStationModel(String version, List<Change> changes,
			List<Exit> exits)
	{
		this.version = version;
		this.changes = changes;
		this.exits = exits;
	}

	public String getVersion()
	{
		return version;
	}

	public List<Change> getChanges()
	{
		return changes;
	}

	public List<Exit> getExits()
	{
		return exits;
	}

}
