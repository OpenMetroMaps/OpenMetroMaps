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

package org.openmetromaps.heavyutil.regression;

import org.openmetromaps.maps.CoordinateConverter;
import org.openmetromaps.maps.model.Coordinate;

import de.topobyte.geomath.WGS84;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lina.Matrix;

public class RegressionCoordinateConverter implements CoordinateConverter
{

	private Matrix transform;
	private double width;
	private double height;

	public RegressionCoordinateConverter(Matrix transform, double width,
			double height)
	{
		this.transform = transform;
		this.width = width;
		this.height = height;
	}

	@Override
	public double getWidth()
	{
		return width;
	}

	@Override
	public double getHeight()
	{
		return height;
	}

	@Override
	public Point convert(Coordinate coordinate)
	{
		double x = WGS84.lon2merc(coordinate.getLongitude(), 1.0);
		double y = WGS84.lat2merc(coordinate.getLatitude(), 1.0);
		return LinaUtil.transform(transform, new Point(x, y));
	}

	@Override
	public Point convert(Point point)
	{
		double x = WGS84.lon2merc(point.getX(), 1.0);
		double y = WGS84.lat2merc(point.getY(), 1.0);
		return LinaUtil.transform(transform, new Point(x, y));
	}

}
