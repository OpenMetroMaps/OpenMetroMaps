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

package org.openmetromaps.mobidig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.EdgeUtil;
import org.openmetromaps.maps.LocationToPoint;
import org.openmetromaps.maps.MapViewStatus;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.SegmentEndPointPaintInfo;
import org.openmetromaps.maps.SegmentPaintInfo;
import org.openmetromaps.maps.TimeMeasuring;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.NeighborInfo;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.Colors;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintFactory;
import org.openmetromaps.maps.painting.core.PaintType;
import org.openmetromaps.maps.painting.core.Painter;
import org.openmetromaps.maps.painting.core.geom.LineSegment;
import org.openmetromaps.maps.painting.core.geom.Path;
import org.openmetromaps.maps.rendering.components.StationDrawer;
import org.openmetromaps.maps.rendering.components.StationDrawerConvex;
import org.openmetromaps.maps.rendering.components.StationDrawerDot;
import org.openmetromaps.maps.rendering.components.StationDrawerSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infomatiq.jsi.Rectangle;

import de.topobyte.formatting.Formatting;
import de.topobyte.jsi.intersectiontester.RTreeIntersectionTester;
import de.topobyte.jsi.intersectiontester.RectangleIntersectionTester;
import de.topobyte.lightgeom.curves.spline.CubicSpline;
import de.topobyte.lightgeom.curves.spline.CubicSplineB;
import de.topobyte.lightgeom.curves.spline.SplineUtil;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;
import de.topobyte.viewports.geometry.Envelope;
import de.topobyte.viewports.scrolling.ViewportListener;
import de.topobyte.viewports.scrolling.ViewportUtil;
import de.topobyte.viewports.scrolling.ViewportWithSignals;

public class CustomPlanRenderer implements ViewportListener
{

	static final Logger logger = LoggerFactory
			.getLogger(CustomPlanRenderer.class);

	private boolean debugRanks = false;
	private boolean debugTangents = false;

	private boolean isRenderLabels = true;
	private boolean renderStationCenters = false;

	private StationMode stationMode;
	private SegmentMode segmentMode;

	private float scale;
	private float factor;

	private PaintFactory pf;
	private Map<String, ColorCode> colorMap;

	private float baseLineWidth = 3.0f;

	private float lineWidth = 1.0f;
	private float spreadFactor = 1.8f;
	private boolean onlyImportant = false;

	private int overDrawPixels = 100;
	private double f = 0.3;

	private LineNetwork lineNetwork;
	private MapViewStatus mapViewStatus;
	private Map<NetworkLine, ColorCode> colors = new HashMap<>();

	protected ViewportWithSignals viewport;
	private LocationToPoint ltp;

	private IPaintInfo[] lineToPaintForLines;

	private StationDrawer stationDrawer;

	public CustomPlanRenderer(LineNetwork lineNetwork,
			MapViewStatus mapViewStatus, StationMode stationMode,
			SegmentMode segmentMode, ViewportWithSignals viewport,
			LocationToPoint ltp, float scale, PaintFactory pf,
			Map<String, ColorCode> colorMap)
	{
		this.lineNetwork = lineNetwork;
		this.mapViewStatus = mapViewStatus;
		this.viewport = viewport;
		this.ltp = ltp;

		this.segmentMode = segmentMode;
		this.stationMode = stationMode;
		this.scale = scale;
		this.pf = pf;
		this.colorMap = colorMap;

		for (NetworkLine line : lineNetwork.getLines()) {
			colors.put(line, ModelUtil.getColor(line.line));
		}

		List<NetworkLine> lines = lineNetwork.getLines();
		lineToPaintForLines = new IPaintInfo[lines.size()];
		for (NetworkLine line : lines) {
			IPaintInfo paint = pf.create(colors.get(line));
			paint.setStyle(PaintType.STROKE);
			lineToPaintForLines[line.line.getId()] = paint;
		}

		setupStationDrawer();

		viewport.addViewportListener(this);
		zoomChanged();
	}

	public LineNetwork getLineNetwork()
	{
		return lineNetwork;
	}

	public boolean isRenderLabels()
	{
		return isRenderLabels;
	}

	public void setRenderLabels(boolean isRenderLabels)
	{
		this.isRenderLabels = isRenderLabels;
	}

	public boolean isRenderStationCenters()
	{
		return renderStationCenters;
	}

	public void setRenderStationCenters(boolean renderStationCenters)
	{
		this.renderStationCenters = renderStationCenters;
	}

	public boolean isDebugRanks()
	{
		return debugRanks;
	}

	public void setDebugRanks(boolean debugRanks)
	{
		this.debugRanks = debugRanks;
	}

	public boolean isDebugTangents()
	{
		return debugTangents;
	}

	public void setDebugTangents(boolean debugTangents)
	{
		this.debugTangents = debugTangents;
	}

	public StationMode getStationMode()
	{
		return stationMode;
	}

	public void setStationMode(StationMode stationMode)
	{
		this.stationMode = stationMode;
		setupStationDrawer();
		zoomChanged();
	}

	public float getScale()
	{
		return scale;
	}

	public void setStationDrawer(StationDrawer stationDrawer)
	{
		this.stationDrawer = stationDrawer;
	}

	public SegmentMode getSegmentMode()
	{
		return segmentMode;
	}

	public void setSegmentMode(SegmentMode segmentMode)
	{
		this.segmentMode = segmentMode;
	}

	private void setupStationDrawer()
	{
		switch (stationMode) {
		case DOT:
			stationDrawer = new StationDrawerDot(pf, lineNetwork, colors, scale,
					ltp, spreadFactor);
			break;
		case SIMPLE:
			stationDrawer = new StationDrawerSimple(pf, lineNetwork, colors,
					scale, ltp, spreadFactor);
			break;
		case CONVEX:
			stationDrawer = new StationDrawerConvex(pf, lineNetwork, colors,
					scale, ltp, spreadFactor);
			break;
		}
	}

	@Override
	public void viewportChanged()
	{

	}

	@Override
	public void complexChange()
	{

	}

	@Override
	public void zoomChanged()
	{
		double zoom = viewport.getZoom();
		factor = (float) (zoom / 3);
		lineWidth = baseLineWidth * factor * scale;

		List<NetworkLine> lines = lineNetwork.getLines();
		final int nLines = lines.size();
		for (int i = 0; i < nLines; i++) {
			NetworkLine line = lines.get(i);
			IPaintInfo paint = lineToPaintForLines[line.line.getId()];
			paint.setWidth(lineWidth);
		}

		onlyImportant = zoom < 2.2;

		stationDrawer.zoomChanged(factor, lineWidth);
	}

	private static final String LOG_SEGMENTS = "segments";
	private static final String LOG_STATIONS = "stations";
	private static final String LOG_LABELS = "labels";
	private long durationCurves = 0;

	public void paint(Painter g)
	{
		double x1 = ViewportUtil.getRealX(viewport, 0);
		double y1 = ViewportUtil.getRealY(viewport, 0);
		double x2 = ViewportUtil.getRealX(viewport,
				viewport.getViewportWidth());
		double y2 = ViewportUtil.getRealY(viewport,
				viewport.getViewportHeight());

		Envelope envelope = new Envelope(x1, x2, y1, y2);

		TimeMeasuring tm = new TimeMeasuring(logger);

		final int nNodes = lineNetwork.nodes.size();
		final int nEdges = lineNetwork.edges.size();

		/*
		 * Segments
		 */

		Envelope edgeEnvelope = new Envelope();

		tm.start(LOG_SEGMENTS);
		durationCurves = 0;
		for (int i = 0; i < nEdges; i++) {
			Edge edge = lineNetwork.edges.get(i);
			Point locationA = edge.n1.location;
			Point locationB = edge.n2.location;

			edgeEnvelope.expandToInclude(locationA.x, locationA.y);
			edgeEnvelope.expandToInclude(locationB.x, locationB.y);

			if (!envelope.intersects(edgeEnvelope)) {
				continue;
			}

			double ax = ltp.getX(locationA.x);
			double ay = ltp.getY(locationA.y);
			double bx = ltp.getX(locationB.x);
			double by = ltp.getY(locationB.y);

			List<NetworkLine> lines = edge.lines;

			if (lines.size() == 1) {
				NetworkLine line = lines.get(0);
				drawSingleLineEdge(g, line, edge, ax, ay, bx, by);
			} else {
				drawMultiLineEdge(g, lines, edge, ax, ay, bx, by);
			}
		}
		tm.stop(LOG_SEGMENTS);

		/*
		 * Stations
		 */

		tm.start(LOG_STATIONS);
		logger.debug("*** Stations ***");

		Path path = g.createPath();
		for (int i = 0; i < nNodes; i++) {
			Node node = lineNetwork.nodes.get(i);
			Point location = node.location;

			if (!envelope.contains(location.x, location.y)) {
				continue;
			}

			boolean selected = mapViewStatus.isNodeSelected(node);

			stationDrawer.drawStation(g, node, path, selected,
					renderStationCenters);
		}
		tm.stop(LOG_STATIONS);

		/*
		 * Labels
		 */

		int fontSize = Math.round(baseFontSize * scale);

		IPaintInfo piOutline = pf.create(Colors.WHITE, 2 * scale);
		piOutline.setFontSize(fontSize);
		IPaintInfo piText = pf.create(Colors.BLACK, 1 * scale);
		piText.setFontSize(fontSize);

		if (debugRanks) {
			for (int i = 0; i < nNodes; i++) {
				Node node = lineNetwork.nodes.get(i);
				Point p = ltp.getPoint(node.location);
				float x = (float) p.x;
				float y = (float) p.y + 5;
				String text = "" + node.rank;

				g.setPaintInfo(piOutline);
				g.outlineString(text, x, y);
				g.setPaintInfo(piText);
				g.drawString(text, x, y);
			}
		}

		tm.start(LOG_LABELS);
		if (isRenderLabels) {
			renderLabels(g, envelope, nNodes, fontSize);
		}
		tm.stop(LOG_LABELS);

		tm.log(LOG_SEGMENTS, "Time for segments: %d");
		tm.log(LOG_STATIONS, "Time for stations: %d");
		tm.log(LOG_LABELS, "Time for labels: %d");
		logger.info(Formatting.format("Time for curve drawing: %d",
				durationCurves));
	}

	private float baseFontSize = 12;

	private IPaintInfo getPiOutline(String name)
	{
		int fontSize = Math.round(baseFontSize * scale);
		IPaintInfo piOutline = pf.create(Colors.WHITE, 4 * scale);
		piOutline.setFontSize(fontSize);
		return piOutline;
	}

	private IPaintInfo getPiText(String name)
	{
		ColorCode color = colorMap.get(name);
		if (color == null) {
			color = Colors.BLACK;
		}
		int fontSize = Math.round(baseFontSize * scale);
		IPaintInfo piText = pf.create(color, 1 * scale);
		piText.setFontSize(fontSize);
		return piText;
	}

	private void renderLabels(Painter g, Envelope envelope, int nNodes,
			int fontSize)
	{
		RectangleIntersectionTester tester = new RTreeIntersectionTester();
		for (int i = 0; i < nNodes; i++) {
			Node node = lineNetwork.nodes.get(i);
			Station station = node.station;
			if (onlyImportant && node.rank < 2) {
				continue;
			}

			Point location = node.location;

			if (!envelope.contains(location.x, location.y)) {
				continue;
			}

			String name = station.getName();
			Point p = ltp.getPoint(location);
			p.y -= 6 * scale * factor;

			IPaintInfo piOutline = getPiOutline(name);
			IPaintInfo piText = getPiText(name);

			g.setPaintInfo(piText);
			int sw = g.getStringWidth(name);

			Rectangle r = new Rectangle((float) (p.x - sw / 2),
					(float) (p.y - fontSize / 2), (float) (p.x + sw / 2),
					(float) (p.y + fontSize / 2));
			if (tester.isFree(r)) {
				float x = (float) (p.x - sw / 2);
				float y = (float) p.y;

				g.setPaintInfo(piOutline);
				g.outlineString(name, x, y);

				g.setPaintInfo(piText);
				g.drawString(name, x, y);

				tester.add(r, false);
			}
		}
	}

	private void drawSingleLineEdge(Painter g, NetworkLine line, Edge edge,
			double ax, double ay, double bx, double by)
	{
		if (segmentMode == SegmentMode.STRAIGHT) {
			drawSingleLineEdgeStraight(g, line, edge, ax, ay, bx, by);
		} else if (segmentMode == SegmentMode.CURVE) {
			drawSingleLineEdgeCurved(g, line, edge, ax, ay, bx, by);
		}
	}

	private void drawMultiLineEdge(Painter g, List<NetworkLine> lines,
			Edge edge, double ax, double ay, double bx, double by)
	{
		if (segmentMode == SegmentMode.STRAIGHT) {
			drawMultiLineEdgeStraight(g, lines, edge, ax, ay, bx, by);
		} else if (segmentMode == SegmentMode.CURVE) {
			drawMultiLineEdgeCurved(g, lines, edge, ax, ay, bx, by);
		}
	}

	private void drawSingleLineEdgeStraight(Painter g, NetworkLine line,
			Edge edge, double ax, double ay, double bx, double by)
	{
		IPaintInfo paint = lineToPaintForLines[line.line.getId()];

		g.setPaintInfo(paint);

		g.drawLine(ax, ay, bx, by);
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

			g.drawLine(lax, lay, lbx, lby);
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
		g.draw(spline);
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

			SplineUtil.spline(spline, lax, lay, lbx, lby, d02, d31, f, true);
			long ta = System.currentTimeMillis();
			g.draw(spline);
			long tb = System.currentTimeMillis();
			durationCurves += tb - ta;

			if (debugTangents) {
				g.draw(new LineSegment(spline.getP1(), spline.getC1()));
				g.draw(new LineSegment(spline.getC2(), spline.getP2()));
			}
		}
	}

}
