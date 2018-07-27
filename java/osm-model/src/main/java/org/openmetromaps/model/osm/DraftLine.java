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

import java.util.List;
import java.util.Map;

import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public class DraftLine
{

	private OsmRelation source;
	private List<DraftStation> stations;

	public DraftLine(OsmRelation source, List<DraftStation> stations)
	{
		this.source = source;
		this.stations = stations;
	}

	public OsmRelation getSource()
	{
		return source;
	}

	public List<DraftStation> getStations()
	{
		return stations;
	}

	public String getName()
	{
		Map<String, String> tags = OsmModelUtil.getTagsAsMap(source);
		String ref = tags.get("ref");
		if (ref != null) {
			return ref;
		}
		String name = tags.get("name");
		return name;
	}

}
