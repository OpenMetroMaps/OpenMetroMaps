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
import java.util.logging.Logger;

import org.openmetromaps.maps.DataConfig;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.BBox;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.logging.client.SystemLogHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

import de.topobyte.formatting.Formatting;
import de.topobyte.lightgeom.lina.Point;

public class SimplePlanPanel extends SimplePanel implements RequiresResize
{

	private static Logger logger = Logger.getLogger("");
	static {
		// Is this necessary? Probably not. TODO: get logging to work
		logger.addHandler(new SystemLogHandler());
	}

	private Canvas canvas;

	private int width = 0;
	private int height = 0;

	private MapModel mapModel;
	private MapView mapView;

	private BBox box;

	private double mx;
	private double my;
	private double w;
	private double h;

	private boolean debugSize = false;

	public SimplePlanPanel()
	{
		canvas = Canvas.createIfSupported();
		add(canvas);

		BaseMouseProcessor mouseHandler = new BaseMouseProcessor();
		Util.addHandler(canvas, mouseHandler);

		// This is very important to initialize the size of the canvas after the
		// widget has loaded.
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute()
			{
				onResize();
			}

		});
	}

	public void setModel(MapModel mapModel)
	{
		this.mapModel = mapModel;
		mapView = mapModel.getViews().get(0);

		DataConfig viewConfig = ModelUtil.dataConfig(mapModel.getData());
		box = viewConfig.getBbox();

		initInternalValues();
	}

	public void setViewport(BBox box)
	{
		this.box = box;
		initInternalValues();
	}

	public boolean isDebugSize()
	{
		return debugSize;
	}

	public void setDebugSize(boolean debugSize)
	{
		this.debugSize = debugSize;
	}

	private void initInternalValues()
	{
		mx = box.getLon1();
		my = box.getLat2();
		w = box.getLon2() - box.getLon1();
		h = box.getLat1() - box.getLat2();
	}

	@Override
	public void onResize()
	{
		int width = getElement().getClientWidth();
		int height = getElement().getClientHeight();
		setSize(width, height);
		render();
	}

	private void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;

		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
	}

	public void render()
	{
		Context2d c = canvas.getContext2d();
		c.clearRect(0, 0, width, height);
		fillBackground(c);

		renderFrame(c);
		if (mapView == null) {
			renderTestContent(c);
		} else {
			renderView(c);
		}

		if (debugSize) {
			c.setFont("16px Arial");
			c.setTextAlign(TextAlign.LEFT);
			c.setFillStyle("#000000");
			c.fillText(Formatting.format("%dx%d", width, height), 15, 31);
		}
	}

	private void fillBackground(Context2d c)
	{
		c.setFillStyle(CssColor.make("#eeeeee"));
		c.fillRect(0, 0, width, height);
	}

	private void renderFrame(Context2d c)
	{
		// draw a frame around the whole canvas
		c.setStrokeStyle("#000000");
		c.setLineWidth(1);
		c.beginPath();
		c.moveTo(10, 10);
		c.lineTo(width - 10, 10);
		c.lineTo(width - 10, height - 10);
		c.lineTo(10, height - 10);
		c.closePath();
		c.stroke();
	}

	private void renderTestContent(Context2d c)
	{
		// draw a simple line
		c.setFillStyle("#000000");
		int size = 40;
		c.setFont(size + "pt Times");
		c.setTextAlign(TextAlign.CENTER);
		c.fillText("Loading data...", width / 2, height / 2 + size / 2);
	}

	private void renderView(Context2d c)
	{
		float s = 5;
		float w = 3;

		ModelData data = mapModel.getData();

		Map<Station, Node> stationToNode = mapView.getLineNetwork()
				.getStationToNode();

		c.setLineWidth(w);
		for (Line line : data.lines) {
			c.setStrokeStyle(CssColor.make(line.getColor()));
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
				c.setFillStyle(CssColor.make("#ffffff"));
				Point p = getPoint(stationToNode.get(station).location);
				c.beginPath();
				c.arc(p.x, p.y, s, 0, 360);
				c.fill();
			} else {
				Stop stop = stops.iterator().next();
				c.setFillStyle(CssColor.make(stop.getLine().getColor()));
				Point p = getPoint(stationToNode.get(station).location);
				c.beginPath();
				c.arc(p.x, p.y, s, 0, 360);
				c.fill();
			}
		}
	}

	private Point getPoint(Point location)
	{
		double x = (location.x - mx) / w * width;
		double y = (location.y - my) / h * height;
		return new Point(x, y);
	}

}
