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

package org.openmetromaps.maps.gwt.client;

import java.util.logging.Logger;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.logging.client.SystemLogHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

public class TestPanel extends SimplePanel implements RequiresResize {

	private static Logger logger = Logger.getLogger("");
	static {
		// Is this necessary? Proably not. TODO: get logging to work
		logger.addHandler(new SystemLogHandler());
	}

	private Canvas canvas;

	private int width = 0;
	private int height = 0;

	private MapModel mapModel;
	private MapView mapView;

	public TestPanel() {
		canvas = Canvas.createIfSupported();
		add(canvas);

		// This is very important to initialize the size of the canvas after the
		// widget has loaded.
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				onResize();
			}

		});
	}

	public void setModel(MapModel mapModel) {
		this.mapModel = mapModel;
		mapView = mapModel.getViews().get(0);
	}

	@Override
	public void onResize() {
		int width = getElement().getClientWidth();
		int height = getElement().getClientHeight();
		setSize(width, height);
		render();
	}

	private void setSize(int width, int height) {
		this.width = width;
		this.height = height;

		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
	}

	public void render() {
		Context2d c = canvas.getContext2d();

		c.clearRect(0, 0, width, height);
		renderFrame(c);
		if (mapView == null) {
			renderTestContent(c);
		} else {
			renderView(c);
		}
	}

	private void renderFrame(Context2d c) {
		// draw a frame around the whole canvas
		c.beginPath();
		c.moveTo(10, 10);
		c.lineTo(width - 10, 10);
		c.lineTo(width - 10, height - 10);
		c.lineTo(10, height - 10);
		c.closePath();
		c.stroke();
	}

	private void renderTestContent(Context2d c) {
		// draw a simple line
		c.setStrokeStyle("#000000");
		c.beginPath();
		c.moveTo(20, 20);
		c.lineTo(100, 30);
		c.stroke();
	}

	private void renderView(Context2d c) {
		// TODO: implement this
	}

}
