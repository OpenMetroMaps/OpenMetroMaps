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

package org.openmetromaps.maps.painting.core;

import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.painting.core.geom.Circle;
import org.openmetromaps.maps.painting.core.geom.LineSegment;
import org.openmetromaps.maps.painting.core.geom.Path;

import de.topobyte.lightgeom.curves.spline.CubicSpline;
import de.topobyte.lightgeom.curves.spline.QuadraticSpline;

public interface Painter
{

	public void setRef(Node node);

	public void setRef(Edge edge, NetworkLine line);

	public void setNoRef();

	public void draw(Path path);

	public Path createPath();

	public void draw(Circle circle);

	public void drawCircle(double x, double y, double radius);

	public void draw(QuadraticSpline spline);

	public void draw(CubicSpline spline);

	public void draw(LineSegment l);

	public void drawLine(double x1, double y1, double x2, double y2);

	public void drawString(String string, float x, float y);

	public void outlineString(String string, float x, float y);

	public int getStringWidth(String string);

	public void setPaintInfo(IPaintInfo paint);

}
