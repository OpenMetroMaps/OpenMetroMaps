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

package org.openmetromaps.imports.config;

import org.openmetromaps.imports.config.osm.OsmSource;
import org.openmetromaps.imports.config.osm.Routes;
import org.openmetromaps.imports.config.osm.Tag;

import de.topobyte.adt.geo.BBox;

public class ConfigTests
{

	public static void print(ImportConfig config)
	{
		Source source = config.getSource();
		if (source instanceof OsmSource) {
			System.out.println("OSM configuration");
			OsmSource osm = (OsmSource) source;
			for (Routes routes : osm.getRoutes()) {
				System.out.println("  Routes");
				BBox bbox = routes.getBbox();
				System.out.println("    bbox: " + bbox);
				for (Tag tag : routes.getTags()) {
					System.out.println(String.format("    tag '%s':'%s'",
							tag.getKey(), tag.getValue()));
				}
			}
		}

		Processing processing = config.getProcessing();
		System.out.println("  Processing");

		for (String prefix : processing.getPrefixes()) {
			System.out.println("    prefix: '" + prefix + "'");
		}

		for (String suffix : processing.getSuffixes()) {
			System.out.println("    suffix: '" + suffix + "'");
		}
	}

}
