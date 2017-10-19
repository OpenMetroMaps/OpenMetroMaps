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

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;

public class EdgeUtil
{

	private static Point lpp, lnp;
	private static Vector2 v1, v2;

	static {
		lpp = new Point(0, 0);
		lnp = new Point(0, 0);
		v1 = new Vector2(0, 0);
		v2 = new Vector2(0, 0);
	}

	public static void segmentInfo(SegmentEndPointPaintInfo spiA,
			SegmentEndPointPaintInfo spiB, double ax, double ay, double bx,
			double by, Point lp, Point ln, LocationToPoint ltp, float lineWidth,
			float spreadFactor, int nLines)
	{
		if (lp != null) {
			lpp = ltp.getPoint(lp, lpp);
			v1.set(lpp.x, lpp.y, bx, by);
		} else {
			v1.set(ax, ay, bx, by);
		}
		v1.normalize();

		if (ln != null) {
			lnp = ltp.getPoint(ln, lnp);
			v2.set(ax, ay, lnp.x, lnp.y);
		} else {
			v2.set(ax, ay, bx, by);
		}
		v2.normalize();

		spiA.set(ax, ay, v1.getX(), v1.getY(), lineWidth * spreadFactor,
				nLines);
		spiB.set(bx, by, v2.getX(), v2.getY(), lineWidth * spreadFactor,
				nLines);
	}

	public static SegmentEndPointPaintInfo endpointInfo(
			SegmentEndPointPaintInfo spi, double ax, double ay, double bx,
			double by, Point lp, LocationToPoint ltp, float lineWidth,
			float spreadFactor, int nLines)
	{
		if (lp != null) {
			lpp = ltp.getPoint(lp, lpp);
			v1.set(lpp.x, lpp.y, bx, by);
		} else {
			v1.set(ax, ay, bx, by);
		}
		v1.normalize();

		spi.set(ax, ay, v1.getX(), v1.getY(), lineWidth * spreadFactor, nLines);

		return spi;
	}

}
