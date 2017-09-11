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

package org.openmetromaps.maps.painting.android;

import org.openmetromaps.maps.painting.core.Cap;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.Join;
import org.openmetromaps.maps.painting.core.PaintFactory;

public class AndroidPaintFactory implements PaintFactory
{

	@Override
	public IPaintInfo create()
	{
		return new AndroidPaintInfo();
	}

	@Override
	public IPaintInfo create(ColorCode color)
	{
		AndroidPaintInfo paint = new AndroidPaintInfo();
		paint.setColor(color);
		return paint;
	}

	@Override
	public IPaintInfo create(ColorCode color, float width)
	{
		AndroidPaintInfo paint = new AndroidPaintInfo();
		paint.setColor(color);
		paint.setWidth(width);
		return paint;
	}

	@Override
	public IPaintInfo create(ColorCode color, float width, Cap cap)
	{
		AndroidPaintInfo paint = new AndroidPaintInfo();
		paint.setColor(color);
		paint.setWidth(width);
		paint.setCap(cap);
		return paint;
	}

	@Override
	public IPaintInfo create(ColorCode color, float width, Join join)
	{
		AndroidPaintInfo paint = new AndroidPaintInfo();
		paint.setColor(color);
		paint.setWidth(width);
		paint.setJoin(join);
		return paint;
	}

	@Override
	public IPaintInfo create(ColorCode color, float width, Cap cap, Join join)
	{
		AndroidPaintInfo paint = new AndroidPaintInfo();
		paint.setColor(color);
		paint.setWidth(width);
		paint.setCap(cap);
		paint.setJoin(join);
		return paint;
	}

}
