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

package org.openmetromaps.osm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.utils.OsmFile;
import de.topobyte.osm4j.utils.OsmOutputConfig;

public class FilterRelevantData extends Filter
{

	public FilterRelevantData(OsmFile input, OsmFile output,
			OsmOutputConfig outputConfig)
	{
		super(input, output, outputConfig);
	}

	private Set<String> interstingRouteTypes = new HashSet<>();
	{
		interstingRouteTypes
				.addAll(Arrays.asList(new String[] { "light_rail", "subway" }));
	}

	@Override
	protected boolean take(OsmNode node)
	{
		Map<String, String> tags = OsmModelUtil.getTagsAsMap(node);
		String railway = tags.get("railway");
		if (railway == null) {
			return false;
		}
		if (!railway.equals("station")) {
			return false;
		}
		String stationName = tags.get("name");
		if (stationName == null) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean take(OsmWay way)
	{
		return false;
	}

	@Override
	protected boolean take(OsmRelation relation)
	{
		Map<String, String> rTags = OsmModelUtil.getTagsAsMap(relation);
		String route = rTags.get("route");
		if (route == null) {
			return false;
		}
		if (!interstingRouteTypes.contains(route)) {
			return false;
		}
		return true;
	}

}
