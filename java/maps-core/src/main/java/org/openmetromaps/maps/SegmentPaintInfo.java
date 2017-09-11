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

public class SegmentPaintInfo
{

	public double shift;
	public double ndx;
	public double ndy;

	public double sx;
	public double sy;
	public double ex;
	public double ey;

	public SegmentPaintInfo(double ax, double ay, double bx, double by,
			double shift, int nLines)
	{
		this.shift = shift;
		double dx = (bx - ax);
		double dy = (by - ay);
		double d = Math.sqrt(dx * dx + dy * dy);
		ndx = dx / d;
		ndy = dy / d;
		calc(nLines);

	}

	private void calc(int nLines)
	{
		sx = 0;
		sy = 0;
		float nShift = 0;
		if ((nLines % 2) == 0) {
			nShift = nLines / 2 - 0.5f;
		} else {
			nShift = nLines / 2;
		}
		sx = -nShift * shift * ndy;
		sy = nShift * shift * ndx;
		ex = nShift * shift * ndy;
		ey = -nShift * shift * ndx;
	}

}
