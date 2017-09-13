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

package org.openmetromaps.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.NeighborInfo;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.Colors;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintFactory;
import org.openmetromaps.maps.painting.core.PaintType;
import org.openmetromaps.maps.painting.core.Painter;
import org.openmetromaps.maps.painting.core.geom.LineSegment;
import org.openmetromaps.maps.painting.core.geom.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infomatiq.jsi.Rectangle;
import com.vividsolutions.jts.geom.Envelope;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.BBoxHelper;
import de.topobyte.adt.geo.Coordinate;
import de.topobyte.interactiveview.ZoomChangedListener;
import de.topobyte.jeography.core.mapwindow.SteplessMapWindow;
import de.topobyte.jsi.intersectiontester.RTreeIntersectionTester;
import de.topobyte.jsi.intersectiontester.RectangleIntersectionTester;
import de.topobyte.lightgeom.curves.spline.CubicSpline;
import de.topobyte.lightgeom.curves.spline.CubicSplineB;
import de.topobyte.lightgeom.curves.spline.SplineUtil;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;

public class PlanRenderer implements ZoomChangedListener
{

	static final Logger logger = LoggerFactory.getLogger(PlanRenderer.class);

	public static enum StationMode {
		SIMPLE,
		CONVEX
	}

	public static enum SegmentMode {
		STRAIGHT,
		CURVE
	}

	private static final boolean DEBUG_RANKS = false;
	private static final boolean DEBUG_TANGENTS = false;

	private boolean isRenderLabels = true;

	private SegmentMode segmentMode;

	private float scale;
	private float factor;

	private PaintFactory pf;

	private float baseLineWidth = 3.0f;

	private float lineWidth = 1.0f;
	private float spreadFactor = 1.8f;
	private boolean onlyImportant = false;

	private int overDrawPixels = 100;
	private double f = 0.3;

	private LineNetwork lineNetwork;
	private Map<NetworkLine, ColorCode> colors = new HashMap<>();

	protected SteplessMapWindow mapWindow;
	private LocationToPoint ltp;

	private IPaintInfo[] lineToPaintForLines;

	private StationDrawer stationDrawer;

	public PlanRenderer(ModelData data, StationMode stationMode,
			SegmentMode segmentMode, SteplessMapWindow mapWindow,
			LocationToPoint ltp, float scale, PaintFactory pf)
	{
		this.mapWindow = mapWindow;
		this.ltp = ltp;

		this.segmentMode = segmentMode;
		this.scale = scale;
		this.pf = pf;

		LineNetworkBuilder builder = new LineNetworkBuilder(data);
		lineNetwork = builder.getGraph();

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

		switch (stationMode) {
		case SIMPLE:
			stationDrawer = new StationDrawerSimple(pf, lineNetwork, colors,
					scale, ltp, spreadFactor);
			break;
		case CONVEX:
			stationDrawer = new StationDrawerConvex(pf, lineNetwork, colors,
					scale, ltp, spreadFactor);
			break;
		}

		mapWindow.addZoomListener(this);
		zoomChanged();
	}

	public boolean isRenderLabels()
	{
		return isRenderLabels;
	}

	public void setRenderLabels(boolean isRenderLabels)
	{
		this.isRenderLabels = isRenderLabels;
	}

	public SegmentMode getSegmentMode()
	{
		return segmentMode;
	}

	public void setSegmentMode(SegmentMode segmentMode)
	{
		this.segmentMode = segmentMode;
	}

	@Override
	public void zoomChanged()
	{
		double zoom = mapWindow.getZoom();
		double f = zoom / 12;
		if (factor > 1) {
			factor = (float) Math.pow(f, 3);
		} else {
			factor = (float) Math.pow(f, 4);
		}
		lineWidth = baseLineWidth * factor * scale;

		List<NetworkLine> lines = lineNetwork.getLines();
		final int nLines = lines.size();
		for (int i = 0; i < nLines; i++) {
			NetworkLine line = lines.get(i);
			IPaintInfo paint = lineToPaintForLines[line.line.getId()];
			paint.setWidth(lineWidth);
		}

		onlyImportant = zoom < 12;

		stationDrawer.zoomChanged(factor, lineWidth);
	}

	private static final String LOG_SEGMENTS = "segments";
	private static final String LOG_STATIONS = "stations";
	private static final String LOG_LABELS = "labels";
	private long durationCurves = 0;

	public void paint(Painter g)
	{
		SteplessMapWindow window = new SteplessMapWindow(
				mapWindow.getWidth() + overDrawPixels,
				mapWindow.getHeight() + overDrawPixels, mapWindow.getZoom(),
				mapWindow.getCenterLon(), mapWindow.getCenterLat());
		BBox bbox = window.getBoundingBox();
		Envelope envelope = bbox.toEnvelope();

		TimeMeasuring tm = new TimeMeasuring(logger);

		final int nNodes = lineNetwork.nodes.size();
		final int nEdges = lineNetwork.edges.size();

		/*
		 * Segments
		 */

		BBox edgeBox = new BBox(0, 0, 0, 0);
		Envelope edgeEnvelope = new Envelope();

		tm.start(LOG_SEGMENTS);
		durationCurves = 0;
		for (int i = 0; i < nEdges; i++) {
			Edge edge = lineNetwork.edges.get(i);
			Coordinate locationA = edge.n1.location;
			Coordinate locationB = edge.n2.location;

			BBoxHelper.minimumBoundingBox(edgeBox, locationA, locationB);
			edgeBox.toEnvelope(edgeEnvelope);
			if (!envelope.intersects(edgeEnvelope)) {
				continue;
			}

			double ax = ltp.getX(locationA.lon);
			double ay = ltp.getY(locationA.lat);
			double bx = ltp.getX(locationB.lon);
			double by = ltp.getY(locationB.lat);

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
			Coordinate location = node.location;

			if (!envelope.contains(location.lon, location.lat)) {
				continue;
			}

			stationDrawer.drawStation(g, node, path);
		}
		tm.stop(LOG_STATIONS);

		/*
		 * Labels
		 */

		int fontSize = Math.round(12 * scale);

		IPaintInfo piOutline = pf.create(Colors.WHITE, 2 * scale);
		piOutline.setFontSize(fontSize);
		IPaintInfo piText = pf.create(Colors.BLACK, 1 * scale);
		piText.setFontSize(fontSize);

		if (DEBUG_RANKS) {
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
			renderLabels(g, envelope, nNodes, fontSize, piOutline, piText);
		}
		tm.stop(LOG_LABELS);

		tm.log(LOG_SEGMENTS, "Time for segments: %d");
		tm.log(LOG_STATIONS, "Time for stations: %d");
		tm.log(LOG_LABELS, "Time for labels: %d");
		logger.info(
				String.format("Time for curve drawing: %d", durationCurves));
	}

	private void renderLabels(Painter g, Envelope envelope, int nNodes,
			int fontSize, IPaintInfo piOutline, IPaintInfo piText)
	{
		RectangleIntersectionTester tester = new RTreeIntersectionTester();
		for (int i = 0; i < nNodes; i++) {
			Node node = lineNetwork.nodes.get(i);
			Station station = node.station;
			if (onlyImportant && node.rank < 2) {
				continue;
			}

			Coordinate location = node.location;

			if (!envelope.contains(location.lon, location.lat)) {
				continue;
			}

			String name = station.getName();
			Point p = ltp.getPoint(location);
			p.y -= 6 * scale * factor;

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
			double sp0x = ltp.getX(prev.location.lon);
			double sp0y = ltp.getY(prev.location.lat);
			d02 = v1;
			d02.set(bx, by);
			d02.sub(sp0x, sp0y);
			d02.normalize();
		}
		if (next != null) {
			double sp3x = ltp.getX(next.location.lon);
			double sp3y = ltp.getY(next.location.lat);
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
		Coordinate lp = edge.prev;
		Coordinate ln = edge.next;

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
				double sp0x = ltp.getX(lp.lon);
				double sp0y = ltp.getY(lp.lat);
				d02 = v1.set(bx, by).sub(sp0x, sp0y).normalize();
			}
			if (ln != null) {
				double sp3x = ltp.getX(ln.lon);
				double sp3y = ltp.getY(ln.lat);
				d31 = v2.set(ax, ay).sub(sp3x, sp3y).normalize();
			}

			SplineUtil.spline(spline, lax, lay, lbx, lby, d02, d31, f, true);
			long ta = System.currentTimeMillis();
			g.draw(spline);
			long tb = System.currentTimeMillis();
			durationCurves += tb - ta;

			if (DEBUG_TANGENTS) {
				g.draw(new LineSegment(spline.getP1(), spline.getC1()));
				g.draw(new LineSegment(spline.getC2(), spline.getP2()));
			}
		}
	}

}
