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

package org.openmetromaps.maps;

import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;

public class TestDataUtil
{

	public static void ensureView(MapModel model)
	{
		if (!model.getViews().isEmpty()) {
			return;
		}

		LineNetworkBuilder builder = new LineNetworkBuilder(model.getData());
		LineNetwork lineNetwork = builder.getGraph();
		ViewConfig viewConfig = ModelUtil.viewConfig(model.getData());
		model.getViews().add(new MapView("Test", lineNetwork, viewConfig));
	}

}
