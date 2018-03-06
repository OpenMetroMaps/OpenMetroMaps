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

package org.openmetromaps.cli.osm;

import java.util.List;
import java.util.Map;

import org.openmetromaps.imports.config.osm.OsmSource;
import org.openmetromaps.imports.config.osm.Routes;
import org.openmetromaps.imports.config.osm.Tag;
import org.openmetromaps.model.osm.filter.RouteFilter;

import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public class OsmSourceRouteFilter implements RouteFilter
{

	private OsmSource source;

	public OsmSourceRouteFilter(OsmSource source)
	{
		this.source = source;
	}

	@Override
	public boolean useRoute(OsmRelation relation)
	{
		Map<String, String> rTags = OsmModelUtil.getTagsAsMap(relation);
		routes: for (Routes routes : source.getRoutes()) {
			List<Tag> tags = routes.getTags();
			for (Tag tag : tags) {
				if (!tag.getValue().equals(rTags.get(tag.getKey()))) {
					continue routes;
				}
			}
			return true;
		}
		return false;
	}

}
