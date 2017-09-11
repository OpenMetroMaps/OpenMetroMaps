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

package org.openmetromaps.maps.painting.awt;

import java.awt.BasicStroke;

import org.openmetromaps.maps.painting.core.Cap;
import org.openmetromaps.maps.painting.core.Join;

public class AwtPaintInfo
{

	public static int getJoin(Join join)
	{
		if (join == null) {
			return BasicStroke.JOIN_ROUND;
		}
		switch (join) {
		default:
		case ROUND:
			return BasicStroke.JOIN_ROUND;
		case BEVEL:
			return BasicStroke.JOIN_BEVEL;
		case MITER:
			return BasicStroke.JOIN_MITER;
		}
	}

	public static int getCap(Cap cap)
	{
		if (cap == null) {
			return BasicStroke.CAP_ROUND;
		}
		switch (cap) {
		default:
		case ROUND:
			return BasicStroke.CAP_ROUND;
		case BUTT:
			return BasicStroke.CAP_BUTT;
		case SQUARE:
			return BasicStroke.CAP_SQUARE;
		}
	}

}
