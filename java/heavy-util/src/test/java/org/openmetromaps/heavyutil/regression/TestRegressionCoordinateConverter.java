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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmetromaps.heavyutil.regression.AffineTransformEstimator.ControlPoint;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lina.Matrix;

public class TestRegressionCoordinateConverter
{

	@Test
	public void testEstimation()
	{
		// Define a known transformation:
		// x' = 2x + 3y + 10
		// y' = -1x + 4y + 20
		double a = 2, b = 3, c = 10;
		double d = -1, e = 4, f = 20;
		Matrix original = LinaUtil
				.matrixFromRows(new double[][] { { a, b, c }, { d, e, f } });

		List<ControlPoint> points = new ArrayList<>();
		points.add(new ControlPoint(new Point(0, 0),
				LinaUtil.transform(original, new Point(0, 0))));
		points.add(new ControlPoint(new Point(10, 0),
				LinaUtil.transform(original, new Point(10, 0))));
		points.add(new ControlPoint(new Point(0, 10),
				LinaUtil.transform(original, new Point(0, 10))));
		points.add(new ControlPoint(new Point(10, 10),
				LinaUtil.transform(original, new Point(10, 10))));

		Matrix estimated = AffineTransformEstimator.estimate(points);

		Point testPoint = new Point(5, 5);
		Point pOrig = LinaUtil.transform(original, testPoint);
		Point pEst = LinaUtil.transform(estimated, testPoint);

		Assert.assertEquals(pOrig.getX(), pEst.getX(), 1e-9);
		Assert.assertEquals(pOrig.getY(), pEst.getY(), 1e-9);
	}

}
