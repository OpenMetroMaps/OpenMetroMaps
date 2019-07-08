// Copyright 2019 Sebastian Kuerten
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

package org.openmetromaps.newformat;

import java.util.Locale;

import org.openmetromaps.maps.painting.core.AbstractPainter;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.GenericPaintInfo;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.geom.Circle;
import org.openmetromaps.maps.painting.core.geom.LineSegment;
import org.openmetromaps.maps.painting.core.geom.Path;
import org.openmetromaps.maps.painting.core.ref.LineEdgeReference;
import org.openmetromaps.maps.painting.core.ref.NodeReference;
import org.openmetromaps.newformat.painting.NewFormatPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.topobyte.lightgeom.curves.spline.CubicSpline;
import de.topobyte.lightgeom.curves.spline.QuadraticSpline;

public class NewFormatPainter extends AbstractPainter
{

	private Document doc;
	private Element eRoot;
	private Ids ids;
	private GenericPaintInfo paint;

	public NewFormatPainter(Document doc, Element eRoot, Ids ids)
	{
		this.doc = doc;
		this.eRoot = eRoot;
		this.ids = ids;
	}

	private Element element()
	{
		Element e = null;
		if (reference instanceof NodeReference) {
			e = doc.createElement("node");
			e.setAttribute("type", "ipe");
			NodeReference nodeReference = (NodeReference) reference;
			e.setAttribute("id",
					ids.getNodeId(nodeReference.getNode().station));
		} else if (reference instanceof LineEdgeReference) {
			e = doc.createElement("metroline");
			e.setAttribute("type", "ipe");
			LineEdgeReference lineEdgeReference = (LineEdgeReference) reference;
			e.setAttribute("id",
					ids.getLineId(lineEdgeReference.getLine().line));
			e.setAttribute("edge", ids.getEdgeId(lineEdgeReference.getEdge()));
		}
		return e;
	}

	@Override
	public void draw(Path path)
	{
		NewFormatPath p = (NewFormatPath) path;
	}

	@Override
	public Path createPath()
	{
		return new NewFormatPath();
	}

	@Override
	public void draw(Circle circle)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawCircle(double x, double y, double radius)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(QuadraticSpline spline)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(CubicSpline spline)
	{
		Element e = element();

		if (e == null) {
			return;
		}

		StringBuilder buffer = new StringBuilder();
		buffer.append(
				String.format("%f %f m", spline.getP1X(), spline.getP1Y()));
		buffer.append(" ");
		buffer.append(String.format("%f %f %f %f %f %f c", spline.getC1X(),
				spline.getC1Y(), spline.getC2X(), spline.getC2Y(),
				spline.getP2X(), spline.getP2Y()));

		eRoot.appendChild(e);

		Element ePath = doc.createElement("path");
		ePath.setAttribute("stroke", getColor(paint.getColor()));

		ePath.setTextContent(buffer.toString());
		e.appendChild(ePath);
	}

	@Override
	public void draw(LineSegment l)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(String string, float x, float y)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void outlineString(String string, float x, float y)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getStringWidth(String string)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPaintInfo(IPaintInfo paint)
	{
		this.paint = (GenericPaintInfo) paint;
	}

	private String getColor(ColorCode color)
	{
		float r = color.getRed() / 255.0f;
		float g = color.getGreen() / 255.0f;
		float b = color.getBlue() / 255.0f;
		return String.format(Locale.US, "%f %f %f", r, g, b);
	}

}
