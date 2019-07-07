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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.Colors;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintFactory;
import org.openmetromaps.maps.painting.core.Painter;
import org.openmetromaps.maps.painting.core.geom.Path;
import org.openmetromaps.maps.rendering.components.SegmentDrawer;
import org.openmetromaps.maps.rendering.components.SegmentDrawerCurved;
import org.openmetromaps.maps.rendering.components.SegmentDrawerStraight;
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
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.viewports.geometry.Envelope;
import de.topobyte.viewports.scrolling.ViewportListener;
import de.topobyte.viewports.scrolling.ViewportUtil;
import de.topobyte.viewports.scrolling.ViewportWithSignals;

public class PlanRenderer implements ViewportListener
{

	static final Logger logger = LoggerFactory.getLogger(PlanRenderer.class);

	public static enum StationMode {
		DOT,
		SIMPLE,
		CONVEX
	}

	public static enum SegmentMode {
		STRAIGHT,
		CURVE
	}

	private boolean debugRanks = false;
	private boolean debugTangents = false;

	private boolean isRenderLabels = true;
	private boolean renderStationCenters = false;

	private StationMode stationMode;
	private SegmentMode segmentMode;

	private float scale;
	private float factor;

	private PaintFactory pf;

	private float baseLineWidth = 3.0f;

	private float lineWidth = 1.0f;
	private float spreadFactor = 1.8f;
	private boolean onlyImportant = false;

	private int overDrawPixels = 100;

	private LineNetwork lineNetwork;
	private MapViewStatus mapViewStatus;
	private Map<NetworkLine, ColorCode> colors = new HashMap<>();

	protected ViewportWithSignals viewport;
	private LocationToPoint ltp;

	private SegmentDrawer segmentDrawer;
	private StationDrawer stationDrawer;

	public PlanRenderer(LineNetwork lineNetwork, MapViewStatus mapViewStatus,
			StationMode stationMode, SegmentMode segmentMode,
			ViewportWithSignals viewport, LocationToPoint ltp, float scale,
			PaintFactory pf)
	{
		this.lineNetwork = lineNetwork;
		this.mapViewStatus = mapViewStatus;
		this.viewport = viewport;
		this.ltp = ltp;

		this.segmentMode = segmentMode;
		this.stationMode = stationMode;
		this.scale = scale;
		this.pf = pf;

		for (NetworkLine line : lineNetwork.getLines()) {
			colors.put(line, ModelUtil.getColor(line.line));
		}

		setupSegmentDrawer();
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
		if (segmentMode == SegmentMode.CURVE) {
			SegmentDrawerCurved curvedDrawer = (SegmentDrawerCurved) segmentDrawer;
			curvedDrawer.setDebugTangents(debugTangents);
		}
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

	public SegmentMode getSegmentMode()
	{
		return segmentMode;
	}

	public void setSegmentMode(SegmentMode segmentMode)
	{
		this.segmentMode = segmentMode;
		setupSegmentDrawer();
		zoomChanged();
	}

	public float getScale()
	{
		return scale;
	}

	public void setScale(float scale)
	{
		this.scale = scale;
		segmentDrawer.setScale(scale);
		stationDrawer.setScale(scale);
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

	private void setupSegmentDrawer()
	{
		switch (segmentMode) {
		case STRAIGHT:
			segmentDrawer = new SegmentDrawerStraight(pf, lineNetwork, colors,
					scale, ltp, spreadFactor, lineWidth);
			break;
		case CURVE:
			segmentDrawer = new SegmentDrawerCurved(pf, lineNetwork, colors,
					scale, ltp, spreadFactor, lineWidth);
			SegmentDrawerCurved curvedDrawer = (SegmentDrawerCurved) segmentDrawer;
			curvedDrawer.setDebugTangents(debugTangents);
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

		onlyImportant = zoom < 2.2;

		segmentDrawer.zoomChanged(factor, lineWidth);
		stationDrawer.zoomChanged(factor, lineWidth);
	}

	private static final String LOG_SEGMENTS = "segments";
	private static final String LOG_STATIONS = "stations";
	private static final String LOG_LABELS = "labels";

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
		segmentDrawer.startSegments();
		for (int i = 0; i < nEdges; i++) {
			Edge edge = lineNetwork.edges.get(i);
			Point locationA = edge.n1.location;
			Point locationB = edge.n2.location;

			edgeEnvelope.expandToInclude(locationA.x, locationA.y);
			edgeEnvelope.expandToInclude(locationB.x, locationB.y);

			if (!envelope.intersects(edgeEnvelope)) {
				continue;
			}

			List<NetworkLine> lines = edge.lines;
			segmentDrawer.drawSegment(g, lines, edge);
		}
		segmentDrawer.finishSegments();
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

		int fontSize = Math.round(12 * scale);

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
			renderLabels(g, envelope, nNodes, fontSize, piOutline, piText);
		}
		tm.stop(LOG_LABELS);

		tm.log(LOG_SEGMENTS, "Time for segments: %d");
		tm.log(LOG_STATIONS, "Time for stations: %d");
		tm.log(LOG_LABELS, "Time for labels: %d");
		if (segmentMode == SegmentMode.CURVE) {
			SegmentDrawerCurved curvedDrawer = (SegmentDrawerCurved) segmentDrawer;
			logger.info(Formatting.format("Time for curve drawing: %d",
					curvedDrawer.getDurationCurves()));
		}
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

			Point location = node.location;

			if (!envelope.contains(location.x, location.y)) {
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

}
