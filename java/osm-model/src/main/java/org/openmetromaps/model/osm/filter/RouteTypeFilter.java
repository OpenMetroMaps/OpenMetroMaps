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

package org.openmetromaps.model.osm.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public class RouteTypeFilter implements RouteFilter
{

	private Set<String> interstingRouteTypes = new HashSet<>();

	public RouteTypeFilter(String... types)
	{
		interstingRouteTypes.addAll(Arrays.asList(types));
	}

	@Override
	public boolean useRoute(OsmRelation relation)
	{
		Map<String, String> tags = OsmModelUtil.getTagsAsMap(relation);
		String route = tags.get("route");
		if (route == null) {
			return false;
		}
		return interstingRouteTypes.contains(route);
	}

}
