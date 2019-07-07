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
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;

import org.openmetromaps.maps.painting.core.AbstractPainter;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.GenericPaintInfo;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintType;
import org.openmetromaps.maps.painting.core.geom.Circle;
import org.openmetromaps.maps.painting.core.geom.LineSegment;
import org.openmetromaps.maps.painting.core.geom.Path;

import de.topobyte.lightgeom.curves.spline.CubicSpline;
import de.topobyte.lightgeom.curves.spline.QuadraticSpline;
import de.topobyte.lightgeom.curves.spline.awt.LightGeomAwt;

public class AwtPainter extends AbstractPainter
{

	private Graphics2D g;
	private GenericPaintInfo paintInfo;

	public AwtPainter(Graphics2D g)
	{
		this.g = g;
	}

	@Override
	public Path createPath()
	{
		return new AwtPath();
	}

	@Override
	public void draw(Path path)
	{
		AwtPath awtPath = (AwtPath) path;
		if (paintInfo.getStyle() == PaintType.FILL) {
			g.fill(awtPath);
		} else {
			g.draw(awtPath);
		}
	}

	private void drawShape(Shape shape)
	{
		if (paintInfo.getStyle() == PaintType.FILL) {
			g.fill(shape);
		} else {
			g.draw(shape);
		}
	}

	@Override
	public void draw(Circle circle)
	{
		Arc2D arc = new Arc2D.Double(circle.getX() - circle.getRadius(),
				circle.getY() - circle.getRadius(), circle.getRadius() * 2,
				circle.getRadius() * 2, 0, 360, Arc2D.CHORD);
		drawShape(arc);
	}

	@Override
	public void drawCircle(double x, double y, double radius)
	{
		Arc2D arc = new Arc2D.Double(x - radius, y - radius, radius * 2,
				radius * 2, 0, 360, Arc2D.CHORD);
		drawShape(arc);
	}

	@Override
	public void draw(QuadraticSpline spline)
	{
		QuadCurve2D curve = LightGeomAwt.convert(spline);
		drawShape(curve);
	}

	@Override
	public void draw(CubicSpline spline)
	{
		CubicCurve2D curve = LightGeomAwt.convert(spline);
		drawShape(curve);
	}

	@Override
	public void draw(LineSegment ls)
	{
		Line2D line = new Line2D.Double(ls.getX1(), ls.getY1(), ls.getX2(),
				ls.getY2());
		drawShape(line);
	}

	@Override
	public void drawString(String string, float x, float y)
	{
		g.drawString(string, x, y);
	}

	@Override
	public void outlineString(String string, float x, float y)
	{
		outlineShapes(string, x, y);
	}

	private void outlineSimple(String string, float x, float y)
	{
		g.drawString(string, x - 1, y - 1);
		g.drawString(string, x - 1, y + 1);
		g.drawString(string, x + 1, y - 1);
		g.drawString(string, x + 1, y + 1);
		g.drawString(string, x - 2, y - 2);
		g.drawString(string, x - 2, y + 2);
		g.drawString(string, x + 2, y - 2);
		g.drawString(string, x + 2, y + 2);
	}

	private void outlineShapes(String string, float x, float y)
	{
		AffineTransform backup = g.getTransform();

		FontRenderContext frc = g.getFontRenderContext();
		Font f = g.getFont();

		TextLayout layout = new TextLayout(string, f, frc);
		Shape outline = layout.getOutline(null);
		g.translate(x, y);
		g.draw(outline);

		g.setTransform(backup);
	}

	@Override
	public int getStringWidth(String string)
	{
		FontMetrics metrics = g.getFontMetrics();
		return metrics.stringWidth(string);
	}

	@Override
	public void setPaintInfo(IPaintInfo paintInfo)
	{
		this.paintInfo = (GenericPaintInfo) paintInfo;
		GenericPaintInfo paint = (GenericPaintInfo) paintInfo;
		ColorCode color = paint.getColor();
		int join = AwtPaintInfo.getJoin(paint.getJoin());
		int cap = AwtPaintInfo.getCap(paint.getCap());
		g.setStroke(new BasicStroke(paint.getWidth(), cap, join));
		g.setColor(new Color(color.getValue(), true));

		String family = Font.SANS_SERIF;
		int style = Font.BOLD;
		Font font = new Font(family, style, paint.getFontSize());
		g.setFont(font);
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		Line2D line = new Line2D.Double(x1, y1, x2, y2);
		drawShape(line);
	}

}
