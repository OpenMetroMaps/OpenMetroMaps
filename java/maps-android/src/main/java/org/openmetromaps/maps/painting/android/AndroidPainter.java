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

import org.openmetromaps.maps.painting.core.AbstractPainter;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.geom.Circle;
import org.openmetromaps.maps.painting.core.geom.LineSegment;
import org.openmetromaps.maps.painting.core.geom.Path;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import de.topobyte.lightgeom.curves.spline.CubicSpline;
import de.topobyte.lightgeom.curves.spline.QuadraticSpline;
import de.topobyte.lightgeom.curves.spline.android.LightGeomAndroid;

public class AndroidPainter extends AbstractPainter
{

	private Canvas c;
	private Paint p;
	private android.graphics.Path cpath = new android.graphics.Path();

	public AndroidPainter(Canvas c)
	{
		this.c = c;
	}

	@Override
	public Path createPath()
	{
		return new AndroidPath();
	}

	@Override
	public void draw(Path path)
	{
		AndroidPath androidPath = (AndroidPath) path;
		c.drawPath(androidPath, p);
	}

	@Override
	public void draw(Circle circle)
	{
		c.drawCircle((float) circle.getX(), (float) circle.getY(),
				(float) circle.getRadius(), p);
	}

	@Override
	public void drawCircle(double x, double y, double radius)
	{
		c.drawCircle((float) x, (float) y, (float) radius, p);
	}

	@Override
	public void draw(QuadraticSpline spline)
	{
		cpath.reset();
		LightGeomAndroid.convert(cpath, spline);
		c.drawPath(cpath, p);
	}

	@Override
	public void draw(CubicSpline spline)
	{
		cpath.reset();
		LightGeomAndroid.convert(cpath, spline);
		c.drawPath(cpath, p);
	}

	@Override
	public void draw(LineSegment ls)
	{
		c.drawLine((float) ls.getX1(), (float) ls.getY1(), (float) ls.getX2(),
				(float) ls.getY2(), p);
	}

	@Override
	public void drawString(String string, float x, float y)
	{
		p.setStyle(Style.FILL);
		c.drawText(string, x, y, p);
	}

	@Override
	public void outlineString(String string, float x, float y)
	{
		p.setStyle(Style.STROKE);
		c.drawText(string, x, y, p);
	}

	@Override
	public int getStringWidth(String string)
	{
		float width = p.measureText(string);
		return Math.round(width);
	}

	@Override
	public void setPaintInfo(IPaintInfo paintInfo)
	{
		AndroidPaintInfo api = (AndroidPaintInfo) paintInfo;
		p = api.getPaintObject();
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		c.drawLine((float) x1, (float) y1, (float) x2, (float) y2, p);
	}

}
