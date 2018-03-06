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

import org.openmetromaps.imports.config.osm.OsmSource;
import org.openmetromaps.imports.config.osm.Routes;
import org.openmetromaps.imports.config.osm.Tag;

import de.topobyte.adt.geo.BBox;
import de.topobyte.formatting.Formatting;

public class OverpassQueryBuilder
{

	public static String build(OsmSource source)
	{
		String newline = "\n";

		StringBuilder buf = new StringBuilder();
		buf.append("(");
		buf.append(newline);
		buf.append("(");
		buf.append(newline);
		for (Routes routes : source.getRoutes()) {
			buf.append("  relation");
			buf.append(newline);
			for (Tag tag : routes.getTags()) {
				buf.append(Formatting.format("    [%s=\"%s\"]", tag.getKey(),
						tag.getValue()));
				buf.append(newline);
			}
			BBox bbox = routes.getBbox();
			buf.append(Formatting.format("    (%f,%f,%f,%f);", bbox.getLat2(),
					bbox.getLon1(), bbox.getLat1(), bbox.getLon2()));
			buf.append(newline);
		}
		buf.append(");");
		buf.append(newline);
		buf.append(">;");
		buf.append(newline);
		buf.append(");");
		buf.append(newline);
		buf.append("out;");

		return buf.toString();
	}

}
