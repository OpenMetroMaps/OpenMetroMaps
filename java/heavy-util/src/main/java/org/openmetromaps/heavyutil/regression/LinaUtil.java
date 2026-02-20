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

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lina.Matrix;
import de.topobyte.lina.Vector;
import de.topobyte.lina.VectorType;

public class LinaUtil
{

	public static Matrix matrixFromColumns(double[][] cols)
	{
		Matrix matrix = new Matrix(cols[0].length, cols.length);
		for (int x = 0; x < cols.length; x++) {
			for (int y = 0; y < cols[0].length; y++) {
				matrix.setValue(x, y, cols[x][y]);
			}
		}
		return matrix;
	}

	public static Matrix matrixFromRows(double[][] rows)
	{
		Matrix matrix = new Matrix(rows.length, rows[0].length);
		for (int y = 0; y < rows.length; y++) {
			for (int x = 0; x < rows[0].length; x++) {
				matrix.setValue(x, y, rows[y][x]);
			}
		}
		return matrix;
	}

	// Transform a point 'p = (x, y)' with the specified affine transformation
	// such that x' = ax + by + c, y' = dx + ey + f.
	public static Point transform(Matrix affineTransform, Point p)
	{
		Vector vector = new Vector(3, VectorType.Column);
		vector.setValue(0, p.getX());
		vector.setValue(1, p.getY());
		vector.setValue(2, 1);
		Vector result = affineTransform.multiplyFromRight(vector).toVector();
		return new Point(result.getValue(0), result.getValue(1));
	}

}
