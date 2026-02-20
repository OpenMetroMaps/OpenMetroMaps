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

import java.util.List;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lina.Matrix;
import de.topobyte.lina.Vector;
import de.topobyte.lina.VectorType;

/**
 * A class for estimating positions for new stations on the view based on the
 * existing stations. We use Ordinary Least Squares (OLS) to come up with an
 * estimation.
 */
public class AffineTransformEstimator
{

	public static class ControlPoint
	{

		public Point source;
		public Point target;

		public ControlPoint(Point source, Point target)
		{
			this.source = source;
			this.target = target;
		}

	}

	public static Matrix estimate(List<ControlPoint> points)
	{
		int n = points.size();
		if (n < 3) {
			throw new IllegalArgumentException(
					"At least 3 control points are required");
		}

		/*-
		 We use Ordinary Least Squares (OLS) to estimate the 6 parameters of
		 the affine transform. The x' and y' equations are treated independently:
		
		 x' = ax + by + c
		 y' = dx + ey + f
		
		 For x': given n reference point pairs (x_i, y_i) -> (tx_i, ty_i),
		 we want to find a, b, c that minimize the total squared error:
		 S = sum over i of (a*x_i + b*y_i + c - tx_i)^2
		
		 To find the minimum we take the partial derivative of S with respect
		 to each unknown (a, b, c), set each derivative to zero, and solve
		 the resulting system. This is the standard OLS "Normal Equations"
		 approach. After expanding and rearranging those three equations, we
		 get a 3x3 linear system where every coefficient is a sum over all
		 reference points:
		
		 [ sum(x^2)  sum(x*y)  sum(x) ] [ a ]   [ sum(tx * x) ]
		 [ sum(x*y)  sum(y^2)  sum(y) ] [ b ] = [ sum(tx * y) ]
		 [ sum(x)    sum(y)    n      ] [ c ]   [ sum(tx)     ]
		
		 The left-hand matrix (often called A^T * A) depends only on the
		 source coordinates and is the same for both x' and y'. Solving the
		 system twice - once with the tx target values on the right-hand
		 side, once with the ty values - gives all 6 parameters.
		 */

		double sumX = 0, sumY = 0, sumX2 = 0, sumY2 = 0, sumXY = 0;
		double sumX_X = 0, sumX_Y = 0, sumX_1 = 0; // sums for x'
		double sumY_X = 0, sumY_Y = 0, sumY_1 = 0; // sums for y'

		for (ControlPoint cp : points) {
			double x = cp.source.getX();
			double y = cp.source.getY();
			double tx = cp.target.getX();
			double ty = cp.target.getY();

			sumX += x;
			sumY += y;
			sumX2 += x * x;
			sumY2 += y * y;
			sumXY += x * y;

			sumX_X += tx * x;
			sumX_Y += tx * y;
			sumX_1 += tx;

			sumY_X += ty * x;
			sumY_Y += ty * y;
			sumY_1 += ty;
		}

		Matrix matrix = LinaUtil.matrixFromColumns(new double[][] { //
				{ sumX2, sumXY, sumX }, //
				{ sumXY, sumY2, sumY }, //
				{ sumX, sumY, n } //
		});

		// Solve for (a, b, c) and (d, e, f)

		Vector abc = new Vector(3, VectorType.Column);
		abc.setValue(0, sumX_X);
		abc.setValue(1, sumX_Y);
		abc.setValue(2, sumX_1);
		Vector resX = matrix.solve(abc).toVector();

		Vector def = new Vector(3, VectorType.Column);
		def.setValue(0, sumY_X);
		def.setValue(1, sumY_Y);
		def.setValue(2, sumY_1);
		Vector resY = matrix.solve(def).toVector();

		return LinaUtil.matrixFromRows(new double[][] {
				{ resX.getValue(0), resX.getValue(1), resX.getValue(2) }, //
				{ resY.getValue(0), resY.getValue(1), resY.getValue(2) } //
		});
	}

}
