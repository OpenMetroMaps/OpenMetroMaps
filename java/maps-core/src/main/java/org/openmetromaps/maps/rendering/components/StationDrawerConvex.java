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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmetromaps.maps.LocationToPoint;
import org.openmetromaps.maps.SegmentEndPointPaintInfo;
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
import org.openmetromaps.maps.painting.core.geom.LineSegment;
import org.openmetromaps.maps.painting.core.geom.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.formatting.Formatting;
import de.topobyte.lightgeom.convexhull.PointArray;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;

public class StationDrawerConvex extends AbstractStationDrawer
{

	static final Logger logger = LoggerFactory
			.getLogger(StationDrawerConvex.class);

	private final static boolean DEBUG = false;

	private ColorCode debugBlack = new ColorCode(0xaa000000, true);
	private ColorCode debugWhite = new ColorCode(0xaaffffff, true);
	private ColorCode debugRed = new ColorCode(0xaaff0000, true);
	private ColorCode debugBlue = new ColorCode(0xaa0000ff, true);
	private ColorCode debugGreen = new ColorCode(0xaa00ff00, true);

	protected IPaintInfo paintDebug;

	public StationDrawerConvex(PaintFactory pf, LineNetwork data,
			Map<NetworkLine, ColorCode> colors, float scale,
			LocationToPoint ltp, float spreadFactor)
	{
		super(pf, data, colors, scale, ltp, spreadFactor);

		paintDebug = pf.create(Colors.WHITE);
		paintDebug.setCap(Cap.ROUND);
		paintDebug.setJoin(Join.ROUND);
		paintDebug.setStyle(PaintType.STROKE);
	}

	private Vector2 da = new Vector2(0, 0);
	private Vector2 db = new Vector2(0, 0);
	private Vector2 m = new Vector2(0, 0);

	private Vector2 wa = new Vector2(0, 0);
	private Vector2 wb = new Vector2(0, 0);
	private Vector2 sa1 = new Vector2(0, 0);
	private Vector2 sa2 = new Vector2(0, 0);
	private Vector2 sb1 = new Vector2(0, 0);
	private Vector2 sb2 = new Vector2(0, 0);

	private Point p1 = new Point(0, 0);
	private Point p2 = new Point(0, 0);
	private Point p3 = new Point(0, 0);
	private Point p4 = new Point(0, 0);

	private List<List<NetworkLine>> done = new ArrayList<>();
	private PointArray coords = new PointArray();
	private List<SegmentEndPointPaintInfo> spis = new ArrayList<>();
	private List<Edge> skipped = new ArrayList<>();

	@Override
	public void drawStation(Painter g, Node node, Path path, boolean selected,
			boolean renderCenter)
	{
		g.setRef(node);
		drawStationInternal(g, node, path, selected, renderCenter);
		g.setNoRef();
	}

	private void drawStationInternal(Painter g, Node node, Path path,
			boolean selected, boolean renderCenter)
	{
		Point location = node.location;
		List<Edge> nodeEdges = node.edges;

		Set<NetworkLine> nodeLines = new HashSet<>();
		for (Edge edge : nodeEdges) {
			nodeLines.addAll(edge.lines);
		}

		if (nodeLines.isEmpty()) {
			return;
		}

		if (nodeLines.size() == 1) {
			NetworkLine line = nodeLines.iterator().next();
			int lineId = line.line.getId();
			IPaintInfo paint = lineToPaintForStations[lineId];
			double px = ltp.getX(location.x);
			double py = ltp.getY(location.y);
			drawSinglePuntal(g, px, py, paint, selected);
			return;
		}

		List<Edge> edges = node.edges;

		double px = ltp.getX(location.x);
		double py = ltp.getY(location.y);

		done.clear();
		spis.clear();
		skipped.clear();

		final int nEdges = edges.size();
		for (int i = 0; i < nEdges; i++) {
			Edge edge = edges.get(i);
			List<NetworkLine> lines = edge.lines;

			if (lines.size() == 1) {
				continue;
			}

			if (done.contains(lines)) {
				skipped.add(edge);
				continue;
			}
			done.add(lines);

			SegmentEndPointPaintInfo spi = endpointInfo(edge, node, ltp,
					lineWidth, spreadFactor, lines.size());
			spis.add(spi);
		}

		// Simple stations with only one line
		if (spis.size() == 0) {
			drawMultiPuntal(g, px, py, selected);
			return;
		}

		// Stations with multiple lines but all on the the same one or two edges
		if (spis.size() == 1) {
			SegmentEndPointPaintInfo spi = spis.get(0);
			drawLineal(g, path, px, py, spi, selected, renderCenter);
			spiPool.give(spi);
			return;
		}

		// Stations with different lines on two edges, but quasi co-linear
		if (spis.size() == 2) {
			SegmentEndPointPaintInfo spi1 = spis.get(0);
			SegmentEndPointPaintInfo spi2 = spis.get(1);
			da.set(spi1.ndx, spi1.ndy);
			db.set(spi2.ndx, spi2.ndy);

			double angle = Math.abs(da.dotProduct(db));
			if (angle > 0.99) {
				// > ~172 degrees
				SegmentEndPointPaintInfo spi = spi1.nShift > spi2.nShift ? spi1
						: spi2;
				drawLineal(g, path, px, py, spi, selected, renderCenter);
				spiPool.give(spi1);
				spiPool.give(spi2);
				return;
			}
		}

		// Stations with multiple lines on edges with different configurations

		// First compute the endpoint info for all previously skipped edges
		final int nSkipped = skipped.size();
		for (int k = 0; k < nSkipped; k++) {
			Edge edge = skipped.get(k);
			List<NetworkLine> lines = edge.lines;

			if (lines.size() == 1) {
				continue;
			}

			SegmentEndPointPaintInfo spi = endpointInfo(edge, node, ltp,
					lineWidth, spreadFactor, lines.size());
			spis.add(spi);
		}

		m.set(px, py);

		// Add corners of the edge to the convex hull
		coords.clear();

		final int nSpis = spis.size();
		for (int i = 0; i < nSpis; i++) {
			SegmentEndPointPaintInfo spi = spis.get(i);

			coords.add((float) (px + spi.sx), (float) (py + spi.sy));
			coords.add((float) (px + spi.ex), (float) (py + spi.ey));
		}

		// Add intersections of rays
		for (int i = 0; i < nSpis - 1; i++) {
			for (int j = i + 1; j < nSpis; j++) {
				SegmentEndPointPaintInfo spi1 = spis.get(i);
				SegmentEndPointPaintInfo spi2 = spis.get(j);

				da.set(spi1.ndx, spi1.ndy);
				db.set(spi2.ndx, spi2.ndy);

				double angle = da.dotProduct(db);
				if (angle > 0.7071) {
					// less than ~90 degrees
					continue;
				}

				wa.set(spi1.sx, spi1.sy);
				wb.set(spi2.sx, spi2.sy);
				sa1.set(m).add(wa);
				sa2.set(m).sub(wa);
				sb1.set(m).add(wb);
				sb2.set(m).sub(wb);
				Point po1 = rayIntersection(p1, sa1, da, sb1, db);
				Point po2 = rayIntersection(p2, sa1, da, sb2, db);
				Point po3 = rayIntersection(p3, sa2, da, sb1, db);
				Point po4 = rayIntersection(p4, sa2, da, sb2, db);
				addIfNonNull(coords, po1);
				addIfNonNull(coords, po2);
				addIfNonNull(coords, po3);
				addIfNonNull(coords, po4);
			}
		}

		logger.info(Formatting.format("Station (%d edges): %s", spis.size(),
				node.station.getName()));

		logger.info("number of points: " + coords.numPoints());
		hull(path, coords);

		if (selected) {
			g.setPaintInfo(paintSelectedStationsStrokeOutline);
		} else {
			g.setPaintInfo(paintStationsStrokeOutline);
		}
		g.draw(path);
		g.setPaintInfo(paintStationsFill);
		g.draw(path);
		g.setPaintInfo(paintStationsStroke);
		g.draw(path);

		if (renderCenter) {
			renderCenter(g, px, py);
		}

		if (DEBUG) {
			paintDebug.setWidth(2);

			paintDebug.setColor(debugBlue);
			g.setPaintInfo(paintDebug);
			final int nPoints = coords.numPoints();
			for (int i = 0; i < nPoints; i++) {
				g.draw(new LineSegment(m.x, m.y, coords.getX(i),
						coords.getY(i)));
			}

			paintDebug.setColor(debugBlack);
			g.setPaintInfo(paintDebug);
			for (int i = 0; i < nSpis; i++) {
				SegmentEndPointPaintInfo spi = spis.get(i);

				Point p1 = new Point(px + spi.sx, py + spi.sy);
				Point p2 = new Point(px + spi.ex, py + spi.ey);

				g.draw(new LineSegment(p1, p2));
			}
		}

		for (int i = 0; i < nSpis; i++) {
			spiPool.give(spis.get(i));
		}
	}

}
