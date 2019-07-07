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

package org.openmetromaps.maps.rendering.components;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.LocationToPoint;
import org.openmetromaps.maps.SegmentPaintInfo;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintFactory;
import org.openmetromaps.maps.painting.core.Painter;

import de.topobyte.lightgeom.lina.Point;

public class SegmentDrawerStraight extends AbstractSegmentDrawer
{

	public SegmentDrawerStraight(PaintFactory pf, LineNetwork lineNetwork,
			Map<NetworkLine, ColorCode> colors, float scale,
			LocationToPoint ltp, float spreadFactor, float lineWidth)
	{
		super(pf, lineNetwork, colors, scale, ltp, spreadFactor, lineWidth);
	}

	@Override
	public void drawSegment(Painter g, List<NetworkLine> lines, Edge edge)
	{
		Point locationA = edge.n1.location;
		Point locationB = edge.n2.location;

		double ax = ltp.getX(locationA.x);
		double ay = ltp.getY(locationA.y);
		double bx = ltp.getX(locationB.x);
		double by = ltp.getY(locationB.y);

		if (lines.size() == 1) {
			NetworkLine line = lines.get(0);
			drawSingleLineEdgeStraight(g, line, edge, ax, ay, bx, by);
		} else {
			drawMultiLineEdgeStraight(g, lines, edge, ax, ay, bx, by);
		}
	}

	private void drawSingleLineEdgeStraight(Painter g, NetworkLine line,
			Edge edge, double ax, double ay, double bx, double by)
	{
		IPaintInfo paint = lineToPaintForLines[line.line.getId()];

		g.setPaintInfo(paint);

		g.setRef(edge, line);
		g.drawLine(ax, ay, bx, by);
		g.setNoRef();
	}

	private void drawMultiLineEdgeStraight(Painter g,
			Collection<NetworkLine> lines, Edge edge, double ax, double ay,
			double bx, double by)
	{
		SegmentPaintInfo spi = new SegmentPaintInfo(ax, ay, bx, by,
				lineWidth * spreadFactor, lines.size());

		Iterator<NetworkLine> iter = lines.iterator();
		final int nLines = lines.size();
		for (int i = 0; i < nLines; i++) {
			double lax = ax + spi.sx + spi.ndy * i * spi.shift;
			double lay = ay + spi.sy - spi.ndx * i * spi.shift;
			double lbx = bx + spi.sx + spi.ndy * i * spi.shift;
			double lby = by + spi.sy - spi.ndx * i * spi.shift;

			NetworkLine line = iter.next();
			IPaintInfo paint = lineToPaintForLines[line.line.getId()];
			g.setPaintInfo(paint);

			g.setRef(edge, line);
			g.drawLine(lax, lay, lbx, lby);
			g.setNoRef();
		}
	}

}
