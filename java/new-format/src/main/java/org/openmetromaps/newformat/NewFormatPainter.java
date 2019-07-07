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

import org.openmetromaps.maps.painting.core.AbstractPainter;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.geom.Circle;
import org.openmetromaps.maps.painting.core.geom.LineSegment;
import org.openmetromaps.maps.painting.core.geom.Path;
import org.openmetromaps.newformat.painting.NewFormatPath;
import org.w3c.dom.Element;

import de.topobyte.lightgeom.curves.spline.CubicSpline;
import de.topobyte.lightgeom.curves.spline.QuadraticSpline;

public class NewFormatPainter extends AbstractPainter
{

	private Element eRoot;

	public NewFormatPainter(Element eRoot)
	{
		this.eRoot = eRoot;
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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}
