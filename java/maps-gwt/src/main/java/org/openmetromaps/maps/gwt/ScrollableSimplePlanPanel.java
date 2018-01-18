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

package org.openmetromaps.maps.gwt;

import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.CssColor;

import de.topobyte.formatting.Formatting;
import de.topobyte.lightgeom.lina.Point;

public class ScrollableSimplePlanPanel extends BaseMapWindowPanel
{

	private MapModel mapModel;
	private MapView mapView;

	private boolean debugSize = false;

	public ScrollableSimplePlanPanel()
	{
		// TODO: use start position

		MouseProcessor panMouseHandler = new PanMouseProcessor<>(this);
		MouseProcessor zoomMouseHandler = new ZoomMouseProcessor<>(this);
		Util.addHandler(canvas, panMouseHandler);
		Util.addHandler(canvas, zoomMouseHandler);

		TouchProcessor panTouchHandler = new PanTouchProcessor<>(this);
		Util.addHandler(canvas, panTouchHandler);
	}

	public void setModel(MapModel mapModel)
	{
		this.mapModel = mapModel;
		mapView = mapModel.getViews().get(0);

		setScene(mapView.getConfig().getScene());
	}

	public boolean isDebugSize()
	{
		return debugSize;
	}

	public void setDebugSize(boolean debugSize)
	{
		this.debugSize = debugSize;
	}

	@Override
	public void render()
	{
		// g.setColor(Color.WHITE);
		// fillRect(g, scene.getX1(), scene.getY1(), scene.getX2(),
		// scene.getY2());

		Context2d c = canvas.getContext2d();
		fillBackground(c);
		renderContent(c);

		if (debugSize) {
			c.setFont("16px Arial");
			c.setTextAlign(TextAlign.LEFT);
			c.setFillStyle("#000000");
			c.fillText(Formatting.format("%dx%d %.2f", getWidth(), getHeight(),
					getDevicePixelRatio()), 5, 21);
		}
	}

	private void fillBackground(Context2d c)
	{
		c.setFillStyle(CssColor.make("#eeeeee"));
		c.fillRect(0, 0, width, height);
	}

	private void renderContent(Context2d c)
	{
		if (mapView == null) {
			return;
		}

		float s = 5;
		float w = 3;

		Map<Station, Node> stationToNode = mapView.getLineNetwork()
				.getStationToNode();

		ModelData data = mapModel.getData();

		c.setLineWidth(w);
		for (Line line : data.lines) {
			c.setStrokeStyle(line.getColor());
			List<Stop> stops = line.getStops();
			Point prev = stationToNode.get(stops.get(0).getStation()).location;
			for (int i = 1; i < stops.size(); i++) {
				Point next = stationToNode
						.get(stops.get(i).getStation()).location;
				Point a = getPoint(prev);
				Point b = getPoint(next);
				c.beginPath();
				c.moveTo(a.x, a.y);
				c.lineTo(b.x, b.y);
				c.stroke();
				prev = next;
			}
		}

		for (Station station : data.stations) {
			List<Stop> stops = station.getStops();
			if (stops.isEmpty()) {
				continue;
			} else if (stops.size() > 1) {
				c.setFillStyle("#ffffff");
				Point p = getPoint(stationToNode.get(station).location);
				c.beginPath();
				c.arc(p.x, p.y, s, 0, 360);
				c.fill();
			} else {
				Stop stop = stops.iterator().next();
				c.setFillStyle(stop.getLine().getColor());
				Point p = getPoint(stationToNode.get(station).location);
				c.beginPath();
				c.arc(p.x, p.y, s, 0, 360);
				c.fill();
			}
		}
	}

}
