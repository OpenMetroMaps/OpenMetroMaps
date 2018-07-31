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

import java.util.Collection;

import de.topobyte.lightgeom.lina.Point;

public class Points
{

	/**
	 * Calculate the mean of this collection of points.
	 * 
	 * @param points
	 *            a collection of points.
	 * @return the mean of the given points.
	 */
	public static Point mean(Collection<Point> points)
	{
		Point m = new Point(0.0, 0.0);
		for (Point p : points) {
			m.x += p.x;
			m.y += p.y;
		}
		m.x /= points.size();
		m.y /= points.size();
		return m;
	}

	/**
	 * Calculate a new point that has the minimum in both dimensions.
	 * 
	 * @param points
	 *            a collection of points.
	 * @return a point representing the minimum.
	 */
	public static Point minimum(Collection<Point> points)
	{
		double lon = Double.POSITIVE_INFINITY;
		double lat = Double.POSITIVE_INFINITY;
		for (Point p : points) {
			if (p.x < lon) {
				lon = p.x;
			}
			if (p.y < lat) {
				lat = p.y;
			}
		}
		return new Point(lon, lat);
	}

	/**
	 * Calculate a new point that has the maximum in both dimensions.
	 * 
	 * @param points
	 *            a collection of points.
	 * @return a point representing the maximum.
	 */
	public static Point maximum(Collection<Point> points)
	{
		double x = Double.NEGATIVE_INFINITY;
		double y = Double.NEGATIVE_INFINITY;
		for (Point p : points) {
			if (p.x > x) {
				x = p.x;
			}
			if (p.y > y) {
				y = p.y;
			}
		}
		return new Point(x, y);
	}

	/**
	 * Create an exact copy of the specified point.
	 * 
	 * @return a new Point with the same coordinates or null, if <code>p</code>
	 *         is null.
	 */
	public static Point clonePoint(Point p)
	{
		if (p == null) {
			return null;
		}
		return new Point(p.getX(), p.getY());
	}

}
