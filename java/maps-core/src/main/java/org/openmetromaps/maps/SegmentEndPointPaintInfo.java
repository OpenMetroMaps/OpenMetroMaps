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

public class SegmentEndPointPaintInfo
{

	public int nLines;
	public float shift;
	// (ndx,ndy) is the normalized vector in the direction of the segment
	public double ndx;
	public double ndy;
	public float nShift;
	public float sw;

	// endpoints of the line perpendicular to the direction of the segment
	public double sx;
	public double sy;
	public double ex;
	public double ey;

	public SegmentEndPointPaintInfo()
	{
		// empty
	}

	public SegmentEndPointPaintInfo(double ax, double ay, double ndx,
			double ndy, float shift, int nLines)
	{
		set(ax, ay, ndx, ndy, shift, nLines);
	}

	public void set(double ax, double ay, double ndx, double ndy, float shift,
			int nLines)
	{
		this.shift = shift;
		this.nLines = nLines;
		this.ndx = ndx;
		this.ndy = ndy;
		calc(nLines);
	}

	private void calc(int nLines)
	{
		sx = 0;
		sy = 0;
		nShift = 0;
		if ((nLines % 2) == 0) {
			nShift = nLines / 2 - 0.5f;
		} else {
			nShift = nLines / 2;
		}
		sw = nShift * shift;
		sx = -sw * ndy;
		sy = sw * ndx;
		ex = sw * ndy;
		ey = -sw * ndx;
	}

}
