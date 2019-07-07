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

import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.EdgeUtil;
import org.openmetromaps.maps.LocationToPoint;
import org.openmetromaps.maps.SegmentEndPointPaintInfo;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.NeighborInfo;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintFactory;
import org.openmetromaps.maps.painting.core.Painter;
import org.openmetromaps.maps.painting.core.geom.LineSegment;

import de.topobyte.lightgeom.curves.spline.CubicSpline;
import de.topobyte.lightgeom.curves.spline.CubicSplineB;
import de.topobyte.lightgeom.curves.spline.SplineUtil;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;

public class SegmentDrawerCurved extends AbstractSegmentDrawer
{

	private double f = 0.3;
	private long durationCurves = 0;
	private boolean debugTangents = false;

	public SegmentDrawerCurved(PaintFactory pf, LineNetwork lineNetwork,
			Map<NetworkLine, ColorCode> colors, float scale,
			LocationToPoint ltp, float spreadFactor, float lineWidth)
	{
		super(pf, lineNetwork, colors, scale, ltp, spreadFactor, lineWidth);
	}

	@Override
	public void startSegments()
	{
		durationCurves = 0;
	}

	public long getDurationCurves()
	{
		return durationCurves;
	}

	public boolean isDebugTangents()
	{
		return debugTangents;
	}

	public void setDebugTangents(boolean debugTangents)
	{
		this.debugTangents = debugTangents;
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
			drawSingleLineEdgeCurved(g, line, edge, ax, ay, bx, by);
		} else {
			drawMultiLineEdgeCurved(g, lines, edge, ax, ay, bx, by);
		}
	}

	private CubicSpline spline = new CubicSplineB(0, 0, 0, 0, 0, 0, 0, 0);

	private Vector2 v1 = new Vector2(0, 0);
	private Vector2 v2 = new Vector2(0, 0);

	private void drawSingleLineEdgeCurved(Painter g, NetworkLine line,
			Edge edge, double ax, double ay, double bx, double by)
	{
		IPaintInfo paint = lineToPaintForLines[line.line.getId()];
		g.setPaintInfo(paint);

		NeighborInfo neighbors = line.getNeighbors(edge);

		Node prev = neighbors.prev;
		Node next = neighbors.next;

		Vector2 d02 = null, d31 = null;

		if (prev != null) {
			double sp0x = ltp.getX(prev.location.x);
			double sp0y = ltp.getY(prev.location.y);
			d02 = v1;
			d02.set(bx, by);
			d02.sub(sp0x, sp0y);
			d02.normalize();
		}
		if (next != null) {
			double sp3x = ltp.getX(next.location.x);
			double sp3y = ltp.getY(next.location.y);
			d31 = v2;
			d31.set(ax, ay);
			d31.sub(sp3x, sp3y);
			d31.normalize();
		}

		SplineUtil.spline(spline, ax, ay, bx, by, d02, d31, f, true);

		g.setRef(edge, line);
		g.draw(spline);
		g.setNoRef();
	}

	private SegmentEndPointPaintInfo spiA = new SegmentEndPointPaintInfo();
	private SegmentEndPointPaintInfo spiB = new SegmentEndPointPaintInfo();

	private void drawMultiLineEdgeCurved(Painter g, List<NetworkLine> lines,
			Edge edge, double ax, double ay, double bx, double by)
	{
		Point lp = edge.prev;
		Point ln = edge.next;

		EdgeUtil.segmentInfo(spiA, spiB, ax, ay, bx, by, lp, ln, ltp, lineWidth,
				spreadFactor, lines.size());

		for (int i = 0; i < lines.size(); i++) {
			double lax = ax + spiA.sx + spiA.ndy * i * spiA.shift;
			double lay = ay + spiA.sy - spiA.ndx * i * spiA.shift;
			double lbx = bx + spiB.sx + spiB.ndy * i * spiB.shift;
			double lby = by + spiB.sy - spiB.ndx * i * spiB.shift;

			NetworkLine line = lines.get(i);
			IPaintInfo paint = lineToPaintForLines[line.line.getId()];
			g.setPaintInfo(paint);

			Vector2 d02 = null, d31 = null;

			if (lp != null) {
				double sp0x = ltp.getX(lp.x);
				double sp0y = ltp.getY(lp.y);
				d02 = v1.set(bx, by).sub(sp0x, sp0y).normalize();
			}
			if (ln != null) {
				double sp3x = ltp.getX(ln.x);
				double sp3y = ltp.getY(ln.y);
				d31 = v2.set(ax, ay).sub(sp3x, sp3y).normalize();
			}

			g.setRef(edge, line);

			SplineUtil.spline(spline, lax, lay, lbx, lby, d02, d31, f, true);
			long ta = System.currentTimeMillis();
			g.draw(spline);
			long tb = System.currentTimeMillis();
			durationCurves += tb - ta;

			if (debugTangents) {
				g.draw(new LineSegment(spline.getP1(), spline.getC1()));
				g.draw(new LineSegment(spline.getC2(), spline.getP2()));
			}

			g.setNoRef();
		}
	}
}
