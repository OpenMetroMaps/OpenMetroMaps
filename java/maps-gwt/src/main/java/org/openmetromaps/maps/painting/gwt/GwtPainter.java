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

import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.GenericPaintInfo;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintType;
import org.openmetromaps.maps.painting.core.Painter;
import org.openmetromaps.maps.painting.core.geom.Circle;
import org.openmetromaps.maps.painting.core.geom.LineSegment;
import org.openmetromaps.maps.painting.core.geom.Path;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.canvas.dom.client.TextMetrics;

import de.topobyte.formatting.IntHexFormatter;
import de.topobyte.lightgeom.curves.spline.CubicSpline;
import de.topobyte.lightgeom.curves.spline.QuadraticSpline;

public class GwtPainter implements Painter
{

	private Context2d c;
	private GenericPaintInfo paintInfo;

	public GwtPainter(Context2d c)
	{
		this.c = c;
	}

	private void fillOrStroke()
	{
		if (paintInfo.getStyle() == PaintType.FILL) {
			c.fill();
		} else {
			c.stroke();
		}
	}

	@Override
	public Path createPath()
	{
		return new GwtPath();
	}

	@Override
	public void draw(Path path)
	{
		GwtPath gwtPath = (GwtPath) path;
		c.beginPath();
		gwtPath.render(c);
		fillOrStroke();
	}

	@Override
	public void draw(Circle circle)
	{
		c.beginPath();
		c.arc(circle.getX(), circle.getY(), circle.getRadius(), 0, 360);
		fillOrStroke();
	}

	@Override
	public void drawCircle(double x, double y, double radius)
	{
		c.beginPath();
		c.arc(x, y, radius, 0, 360);
		fillOrStroke();
	}

	@Override
	public void draw(QuadraticSpline spline)
	{
		c.beginPath();
		c.moveTo(spline.getP1X(), spline.getP1Y());
		c.quadraticCurveTo(spline.getCX(), spline.getCY(), spline.getP2X(),
				spline.getP2Y());
		fillOrStroke();
	}

	@Override
	public void draw(CubicSpline spline)
	{
		c.beginPath();
		c.moveTo(spline.getP1X(), spline.getP1Y());
		c.bezierCurveTo(spline.getC1X(), spline.getC1Y(), spline.getC2X(),
				spline.getC2Y(), spline.getP2X(), spline.getP2Y());
		fillOrStroke();
	}

	@Override
	public void draw(LineSegment l)
	{
		c.beginPath();
		c.moveTo(l.getX1(), l.getY1());
		c.lineTo(l.getX2(), l.getY2());
		fillOrStroke();
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		c.beginPath();
		c.moveTo(x1, y1);
		c.lineTo(x2, y2);
		fillOrStroke();
	}

	@Override
	public void drawString(String string, float x, float y)
	{
		c.fillText(string, x, y);
	}

	@Override
	public void outlineString(String string, float x, float y)
	{
		c.strokeText(string, x, y);
	}

	@Override
	public int getStringWidth(String string)
	{
		TextMetrics metrics = c.measureText(string);
		return (int) metrics.getWidth();
	}

	private IntHexFormatter hex = new IntHexFormatter();
	{
		hex.setMinWidth(6);
		hex.setPadChar('0');
	}

	@Override
	public void setPaintInfo(IPaintInfo paintInfo)
	{
		this.paintInfo = (GenericPaintInfo) paintInfo;
		GenericPaintInfo paint = (GenericPaintInfo) paintInfo;

		ColorCode color = paint.getColor();
		LineJoin join = GwtPaintInfo.getJoin(paint.getJoin());
		LineCap cap = GwtPaintInfo.getCap(paint.getCap());
		c.setLineJoin(join);
		c.setLineCap(cap);
		c.setLineWidth(paint.getWidth());

		int colorValue = color.getValue() & 0xFFFFFF;
		String colorCode = "#" + hex.format(colorValue);

		c.setFillStyle(colorCode);
		c.setStrokeStyle(colorCode);

		int fontSize = paint.getFontSize();
		c.setFont("bold " + fontSize + "px Arial");
	}

	@Override
	public void setRef(Node node)
	{
		// ignore
	}

	@Override
	public void setRef(Edge edge, NetworkLine line)
	{
		// ignore
	}

	@Override
	public void setNoRef()
	{
		// ignore
	}

}
