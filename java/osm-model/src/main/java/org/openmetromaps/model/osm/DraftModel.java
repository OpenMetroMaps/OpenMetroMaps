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

package org.openmetromaps.model.osm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.topobyte.osm4j.core.model.iface.OsmNode;

public class DraftModel
{

	private Map<String, OsmNode> stationMap = new HashMap<>();
	private List<DraftLine> lines = new ArrayList<>();

	public Map<String, OsmNode> getStationMap()
	{
		return stationMap;
	}

	public void setStationMap(Map<String, OsmNode> stationMap)
	{
		this.stationMap = stationMap;
	}

	public List<DraftLine> getLines()
	{
		return lines;
	}

	public void setLines(List<DraftLine> lines)
	{
		this.lines = lines;
	}

}
