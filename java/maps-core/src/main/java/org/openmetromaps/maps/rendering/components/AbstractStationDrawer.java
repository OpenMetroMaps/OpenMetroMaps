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

package org.openmetromaps.maps.rendering.components;

import java.util.Map;

import org.openmetromaps.maps.EdgeUtil;
import org.openmetromaps.maps.LocationToPoint;
import org.openmetromaps.maps.SegmentEndPointPaintInfo;
import org.openmetromaps.maps.SegmentEndPointPool;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.painting.core.Cap;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.Colors;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.Join;
import org.openmetromaps.maps.painting.core.PaintFactory;
import org.openmetromaps.maps.painting.core.PaintType;
import org.openmetromaps.maps.painting.core.Painter;
import org.openmetromaps.maps.painting.core.geom.Path;

import de.topobyte.lightgeom.convexhull.ConvexHull;
import de.topobyte.lightgeom.convexhull.PointArray;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;

public abstract class AbstractStationDrawer implements StationDrawer
{

	protected final static boolean DEBUG = false;

	public static final float STATION_OUTLINE_INCREASE = 1.4f;

	protected LocationToPoint ltp;

	protected LineNetwork data;
	private float scale;

	protected IPaintInfo paintStationsStroke;
	protected IPaintInfo paintStationsStrokeOutline;
	protected IPaintInfo paintStationsFill;
	protected IPaintInfo paintStationsFillOutline;

	protected IPaintInfo paintSelectedStationsStrokeOutline;
	protected IPaintInfo paintSelectedStationsFillOutline;

	protected IPaintInfo paintStationCenters;

	protected float spreadFactor;
	private float baseStationsSize = 8;
	protected float stationsSize;
	protected float circleRadius;
	protected float circleRadiusOutline;
	protected float lineWidth;

	protected IPaintInfo[] lineToPaintForStations;

	protected SegmentEndPointPool spiPool = new SegmentEndPointPool();

	public AbstractStationDrawer(PaintFactory pf, LineNetwork data,
			Map<NetworkLine, ColorCode> colors, float scale,
			LocationToPoint ltp, float spreadFactor)
	{
		this.data = data;
		this.scale = scale;
		this.ltp = ltp;
		this.spreadFactor = spreadFactor;

		paintStationsFill = pf.create(Colors.WHITE);
		paintStationsFill.setStyle(PaintType.FILL);
		paintStationsFillOutline = pf.create(Colors.BLACK);
		paintStationsFillOutline.setStyle(PaintType.FILL);
		paintStationsStroke = pf.create(Colors.WHITE);
		paintStationsStroke.setCap(Cap.ROUND);
		paintStationsStroke.setJoin(Join.ROUND);
		paintStationsStroke.setStyle(PaintType.STROKE);
		paintStationsStrokeOutline = pf.create(Colors.BLACK);
		paintStationsStrokeOutline.setCap(Cap.ROUND);
		paintStationsStrokeOutline.setJoin(Join.ROUND);
		paintStationsStrokeOutline.setStyle(PaintType.STROKE);

		paintSelectedStationsFillOutline = pf.create(Colors.RED);
		paintSelectedStationsFillOutline.setStyle(PaintType.FILL);
		paintSelectedStationsStrokeOutline = pf.create(Colors.RED);
		paintSelectedStationsStrokeOutline.setCap(Cap.ROUND);
		paintSelectedStationsStrokeOutline.setJoin(Join.ROUND);
		paintSelectedStationsStrokeOutline.setStyle(PaintType.STROKE);

		paintStationCenters = pf.create(Colors.BLACK);
		paintStationCenters.setCap(Cap.ROUND);
		paintStationCenters.setJoin(Join.ROUND);
		paintStationCenters.setStyle(PaintType.FILL);

		lineToPaintForStations = new IPaintInfo[data.getLines().size()];

		final int nLines = data.getLines().size();
		for (int i = 0; i < nLines; i++) {
			NetworkLine line = data.getLines().get(i);
			IPaintInfo paint = pf.create(colors.get(line));
			paint.setStyle(PaintType.FILL);
			lineToPaintForStations[line.line.getId()] = paint;
		}
	}

	@Override
	public float getScale()
	{
		return scale;
	}

	@Override
	public void setScale(float scale)
	{
		this.scale = scale;
	}

	@Override
	public void zoomChanged(float factor, float lineWidth)
	{
		this.lineWidth = lineWidth;

		stationsSize = baseStationsSize * factor * scale;
		circleRadius = stationsSize / 2 / STATION_OUTLINE_INCREASE;
		circleRadiusOutline = stationsSize / 2;

		paintStationsStroke.setWidth(stationsSize / STATION_OUTLINE_INCREASE);
		paintStationsStrokeOutline.setWidth(stationsSize);
		paintSelectedStationsStrokeOutline.setWidth(stationsSize);

		int nLines = data.getLines().size();
		for (int i = 0; i < nLines; i++) {
			NetworkLine line = data.getLines().get(i);
			IPaintInfo paintForStations = lineToPaintForStations[line.line
					.getId()];
			paintForStations.setWidth(stationsSize);
		}
	}

	protected void drawSinglePuntal(Painter g, double px, double py,
			IPaintInfo paint, boolean selected)
	{
		if (selected) {
			g.setPaintInfo(paintSelectedStationsFillOutline);
			g.drawCircle(px, py, circleRadiusOutline);
		}
		g.setPaintInfo(paint);
		g.drawCircle(px, py, circleRadius);
	}

	protected void drawMultiPuntal(Painter g, double px, double py,
			boolean selected)
	{
		if (selected) {
			g.setPaintInfo(paintSelectedStationsFillOutline);
		} else {
			g.setPaintInfo(paintStationsFillOutline);
		}
		g.drawCircle(px, py, circleRadiusOutline);
		g.setPaintInfo(paintStationsFill);
		g.drawCircle(px, py, circleRadius);
	}

	protected void drawLineal(Painter g, Path path, double px, double py,
			SegmentEndPointPaintInfo spi, boolean selected,
			boolean renderCenter)
	{
		path.reset();
		path.moveTo(px + spi.sx, py + spi.sy);
		path.lineTo(px + spi.ex, py + spi.ey);

		if (selected) {
			g.setPaintInfo(paintSelectedStationsStrokeOutline);
		} else {
			g.setPaintInfo(paintStationsStrokeOutline);
		}
		g.draw(path);

		g.setPaintInfo(paintStationsStroke);
		g.draw(path);

		if (renderCenter) {
			renderCenter(g, px, py);
		}
	}

	protected void renderCenter(Painter g, double px, double py)
	{
		g.setPaintInfo(paintStationCenters);
		g.drawCircle(px, py, 1);
	}

	protected void addIfNonNull(PointArray points, Point p)
	{
		if (p != null) {
			points.add((float) p.x, (float) p.y);
		}
	}

	protected Point rayIntersection(Point p, Vector2 as, Vector2 ad, Vector2 bs,
			Vector2 bd)
	{
		double u = (as.y * bd.x + bd.y * bs.x - bs.y * bd.x - bd.y * as.x)
				/ (ad.x * bd.y - ad.y * bd.x);
		double v = (as.x + ad.x * u - bs.x) / bd.x;
		if (u > 0 && v > 0) {
			p.setX(as.x + ad.x * u);
			p.setY(as.y + ad.y * u);
			return p;
		}
		return null;
	}

	private ConvexHull convexHull = new ConvexHull();

	protected void hull(Path path, PointArray coords)
	{
		PointArray hull = convexHull.computePolygon(coords, false);

		path.reset();
		int nPoints = hull.size / 2;
		path.moveTo(hull.getX(0), hull.getY(0));
		for (int i = 1; i < nPoints; i++) {
			path.lineTo(hull.getX(i), hull.getY(i));
		}
	}

	protected SegmentEndPointPaintInfo endpointInfo(Edge edge, Node node,
			LocationToPoint ltp, float lineWidth, float spreadFactor,
			int nLines)
	{
		Point locationA = edge.n1.location;
		Point locationB = edge.n2.location;

		double ax = ltp.getX(locationA.x);
		double ay = ltp.getY(locationA.y);
		double bx = ltp.getX(locationB.x);
		double by = ltp.getY(locationB.y);

		boolean reverse = edge.n2 == node;

		SegmentEndPointPaintInfo spi = spiPool.get();
		if (!reverse) {
			Point lp = edge.prev;
			spi = EdgeUtil.endpointInfo(spi, ax, ay, bx, by, lp, ltp, lineWidth,
					spreadFactor, nLines);
		} else {
			Point ln = edge.next;
			spi = EdgeUtil.endpointInfo(spi, bx, by, ax, ay, ln, ltp, lineWidth,
					spreadFactor, nLines);
		}
		return spi;
	}

}
