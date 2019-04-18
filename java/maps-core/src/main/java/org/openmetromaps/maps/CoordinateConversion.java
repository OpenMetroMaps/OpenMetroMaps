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

import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.Node;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.BBoxHelper;
import de.topobyte.adt.geo.Coordinate;

public class CoordinateConversion
{

	public static void convertViews(MapModel model,
			CoordinateConversionType conversionType)
	{
		for (MapView view : model.getViews()) {
			convertView(view, conversionType);
		}
	}

	public static void convertView(MapView view,
			CoordinateConversionType conversionType)
	{
		List<Coordinate> coordinates = new ArrayList<>();
		for (Node node : view.getLineNetwork().getNodes()) {
			coordinates.add(new Coordinate(node.location.x, node.location.y));
		}

		if (coordinates.isEmpty()) {
			return;
		}

		BBox bbox = BBoxHelper.minimumBoundingBox(coordinates);

		CoordinateConverter converter = null;
		if (conversionType == CoordinateConversionType.IDENTITY) {
			converter = new IdentityCoordinateConverter(bbox, 1000, 50);
		} else if (conversionType == CoordinateConversionType.WGS84) {
			converter = new Wgs84CoordinateConverter(bbox, 1000, 50);
		} else {
			throw new IllegalArgumentException(
					"Invalid coordinate conversion specified");
		}

		for (Node node : view.getLineNetwork().getNodes()) {
			node.location = converter.convert(node.location);
		}

		for (Edge edge : view.getLineNetwork().getEdges()) {
			if (edge.prev != null) {
				edge.prev = converter.convert(edge.prev);
			}
			if (edge.next != null) {
				edge.next = converter.convert(edge.next);
			}
		}

		view.setConfig(ModelUtil.viewConfig(view.getLineNetwork(),
				converter.getWidth(), converter.getHeight()));
	}

}
