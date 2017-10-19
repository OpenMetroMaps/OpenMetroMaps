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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import de.topobyte.lightgeom.lina.Point;

public class ScrollableSimplePanel extends BaseMapWindowPanel
{

	private static final long serialVersionUID = 1L;

	private ModelData data;
	private MapView view;

	public ScrollableSimplePanel(ModelData data, MapView view, int minZoom,
			int maxZoom)
	{
		super(view.getConfig().getScene());
		// TODO: use start position

		this.data = data;
		this.view = view;

		ViewActions.setupMovementActions(getInputMap(), getActionMap(), this);
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.WHITE);
		fillRect(g, scene.getX1(), scene.getY1(), scene.getX2(), scene.getY2());

		float s = 10;
		float w = 3;

		Map<Station, Node> stationToNode = view.getLineNetwork()
				.getStationToNode();

		g.setStroke(new BasicStroke(w));
		for (Line line : data.lines) {
			g.setColor(AwtUtil.getAwtColor(line));
			List<Stop> stops = line.getStops();
			Point prev = stationToNode.get(stops.get(0).getStation()).location;
			for (int i = 1; i < stops.size(); i++) {
				Point next = stationToNode
						.get(stops.get(i).getStation()).location;
				Point a = getPoint(prev);
				Point b = getPoint(next);
				Line2D l = new Line2D.Double(a.x, a.y, b.x, b.y);
				g.draw(l);
				prev = next;
			}
		}

		for (Station station : data.stations) {
			List<Stop> stops = station.getStops();
			if (stops.isEmpty()) {
				continue;
			} else if (stops.size() > 1) {
				g.setColor(Color.WHITE);
				Point p = getPoint(stationToNode.get(station).location);
				Arc2D arc = new Arc2D.Double(p.x - s / 2, p.y - s / 2, s, s, 0,
						360, Arc2D.CHORD);
				g.fill(arc);
			} else {
				Stop stop = stops.iterator().next();
				g.setColor(AwtUtil.getAwtColor(stop.getLine()));
				Point p = getPoint(stationToNode.get(station).location);
				Arc2D arc = new Arc2D.Double(p.x - s / 2, p.y - s / 2, s, s, 0,
						360, Arc2D.CHORD);
				g.fill(arc);
			}
		}
	}

}
