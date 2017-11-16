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

package org.openmetromaps.maps.painting.gwt;

import org.openmetromaps.maps.painting.core.Cap;
import org.openmetromaps.maps.painting.core.Join;

import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;

public class GwtPaintInfo
{

	public static LineJoin getJoin(Join join)
	{
		if (join == null) {
			return LineJoin.ROUND;
		}
		switch (join) {
		default:
		case ROUND:
			return LineJoin.ROUND;
		case BEVEL:
			return LineJoin.BEVEL;
		case MITER:
			return LineJoin.MITER;
		}
	}

	public static LineCap getCap(Cap cap)
	{
		if (cap == null) {
			return LineCap.ROUND;
		}
		switch (cap) {
		default:
		case ROUND:
			return LineCap.ROUND;
		case BUTT:
			return LineCap.BUTT;
		case SQUARE:
			return LineCap.SQUARE;
		}
	}

}
