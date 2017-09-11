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
import org.openmetromaps.maps.painting.core.PaintType;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;

public class AndroidPaintInfo implements IPaintInfo
{

	public static Paint.Join getJoin(Join join)
	{
		if (join == null) {
			return Paint.Join.ROUND;
		}
		switch (join) {
		default:
		case ROUND:
			return Paint.Join.ROUND;
		case BEVEL:
			return Paint.Join.BEVEL;
		case MITER:
			return Paint.Join.MITER;
		}
	}

	public static Paint.Cap getCap(Cap cap)
	{
		if (cap == null) {
			return Paint.Cap.ROUND;
		}
		switch (cap) {
		default:
		case ROUND:
			return Paint.Cap.ROUND;
		case BUTT:
			return Paint.Cap.BUTT;
		case SQUARE:
			return Paint.Cap.SQUARE;
		}
	}

	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public AndroidPaintInfo()
	{
		Typeface family = Typeface.SANS_SERIF;
		int style = Typeface.BOLD;
		Typeface typeface = Typeface.create(family, style);
		paint.setTypeface(typeface);
	}

	@Override
	public void setWidth(float width)
	{
		paint.setStrokeWidth(width);
	}

	@Override
	public void setColor(ColorCode color)
	{
		paint.setColor(color.getValue());
	}

	@Override
	public void setCap(Cap cap)
	{
		paint.setStrokeCap(getCap(cap));
	}

	@Override
	public void setJoin(Join join)
	{
		paint.setStrokeJoin(getJoin(join));
	}

	@Override
	public void setFontSize(int fontSize)
	{
		paint.setTextSize(fontSize);
	}

	@Override
	public Paint getPaintObject()
	{
		return paint;
	}

	@Override
	public void setStyle(PaintType type)
	{
		if (type == PaintType.FILL) {
			paint.setStyle(Style.FILL);
		} else {
			paint.setStyle(Style.STROKE);
		}
	}

}
