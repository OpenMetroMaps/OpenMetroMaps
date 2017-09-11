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

import java.util.ArrayDeque;
import java.util.Deque;

public class SegmentEndPointPool
{

	private Deque<SegmentEndPointPaintInfo> seppis = new ArrayDeque<>();

	public SegmentEndPointPaintInfo get()
	{
		if (seppis.isEmpty()) {
			return new SegmentEndPointPaintInfo();
		}
		return seppis.pop();
	}

	public void give(SegmentEndPointPaintInfo info)
	{
		seppis.push(info);
	}

}
