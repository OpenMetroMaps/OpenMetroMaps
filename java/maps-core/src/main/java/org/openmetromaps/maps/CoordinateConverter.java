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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.Coordinate;
import de.topobyte.geomath.WGS84;

public class CoordinateConverter
{

	final static Logger logger = LoggerFactory
			.getLogger(CoordinateConverter.class);

	private double worldsize = 1;
	private double factor;
	private double minX;
	private double minY;

	public CoordinateConverter(BBox bbox, double size)
	{
		double x1 = WGS84.lon2merc(bbox.getLon1(), worldsize);
		double x2 = WGS84.lon2merc(bbox.getLon2(), worldsize);

		double y1 = WGS84.lat2merc(bbox.getLat1(), worldsize);
		double y2 = WGS84.lat2merc(bbox.getLat2(), worldsize);

		double spanX = Math.abs(x1 - x2);
		double spanY = Math.abs(y1 - y2);

		double biggerSpan = Math.max(spanX, spanY);
		factor = size / biggerSpan;

		minX = Math.min(x1, x2);
		minY = Math.min(y1, y2);

		logger.debug(String.format("coordinates: %f,%f:%f,%f", x1, x2, y1, y2));
		logger.debug(String.format("spanX: %f, spanY: %f", spanX, spanY));
	}

	public Coordinate convert(Coordinate coordinate)
	{
		double x = WGS84.lon2merc(coordinate.getLongitude(), worldsize);
		double y = WGS84.lat2merc(coordinate.getLatitude(), worldsize);
		double dx = x - minX;
		double dy = y - minY;
		double sx = dx * factor;
		double sy = dy * factor;
		return new Coordinate(sx, sy);
	}

}
