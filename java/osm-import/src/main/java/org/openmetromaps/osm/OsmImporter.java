// Copyright 2026 Sebastian Kuerten
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

import java.util.List;

import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.model.osm.DraftModel;
import org.openmetromaps.model.osm.DraftModelConverter;
import org.openmetromaps.model.osm.Fix;
import org.openmetromaps.model.osm.ModelBuilder;
import org.openmetromaps.model.osm.filter.RouteFilter;

import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;

public class OsmImporter
{

	public static ModelData execute(InMemoryMapDataSet data,
			RouteFilter routeFilter, List<String> prefixes,
			List<String> suffixes, List<Fix> fixes)
	{
		ModelBuilder modelBuilder = new ModelBuilder(data, routeFilter,
				prefixes, suffixes, fixes);
		modelBuilder.run(true, true);

		DraftModel draftModel = modelBuilder.getModel();
		ModelData model = new DraftModelConverter().convert(draftModel);

		return model;
	}

}
