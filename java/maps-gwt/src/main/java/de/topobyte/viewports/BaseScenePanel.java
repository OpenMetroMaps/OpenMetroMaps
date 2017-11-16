// Copyright 2017 Sebastian Kuerten
//
// This file is part of viewports.
//
// viewports is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// viewports is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with viewports. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.viewports;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

import de.topobyte.viewports.geometry.Rectangle;
import de.topobyte.viewports.scrolling.HasMargin;
import de.topobyte.viewports.scrolling.HasScene;
import de.topobyte.viewports.scrolling.ViewportListener;
import de.topobyte.viewports.scrolling.ViewportWithSignals;

public abstract class BaseScenePanel extends SimplePanel implements
		ViewportWithSignals, HasScene, HasMargin, HasSize, RequiresResize
{

	final static Logger logger = LoggerFactory.getLogger(BaseScenePanel.class);

	protected Canvas canvas;

	protected int width = 0;
	protected int height = 0;

	protected Rectangle scene;

	protected int margin = 150;

	protected double positionX = 0;
	protected double positionY = 0;
	protected double zoom = 1;

	public BaseScenePanel()
	{
		canvas = Canvas.createIfSupported();
		add(canvas);

		// This is very important to initialize the size of the canvas after the
		// widget has loaded.
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute()
			{
				checkBounds();
			}

		});
	}

	protected abstract void render();

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
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

	@Override
	public Rectangle getScene()
	{
		return scene;
	}

	public void setScene(Rectangle scene)
	{
		this.scene = scene;
	}

	@Override
	public double getMargin()
	{
		return margin;
	}

	@Override
	public double getPositionX()
	{
		return positionX;
	}

	@Override
	public double getPositionY()
	{
		return positionY;
	}

	@Override
	public double getViewportWidth()
	{
		return getWidth();
	}

	@Override
	public double getViewportHeight()
	{
		return getHeight();
	}

	@Override
	public double getZoom()
	{
		return zoom;
	}

	protected void internalSetZoom(double value)
	{
		zoom = value;
	}

	protected void internalSetPositionX(double value)
	{
		positionX = value;
	}

	protected void internalSetPositionY(double value)
	{
		positionY = value;
	}

	@Override
	public void setPositionX(double value)
	{
		internalSetPositionX(value);
		fireViewportListenersViewportChanged();
	}

	@Override
	public void setPositionY(double value)
	{
		internalSetPositionY(value);
		fireViewportListenersViewportChanged();
	}

	@Override
	public void setZoom(double zoom)
	{
		setZoomCentered(zoom);
	}

	public void setZoomCentered(double zoom)
	{
		double mx = -positionX + getWidth() / this.zoom / 2.0;
		double my = -positionY + getHeight() / this.zoom / 2.0;

		internalSetZoom(zoom);
		internalSetPositionX(getWidth() / zoom / 2.0 - mx);
		internalSetPositionY(getHeight() / zoom / 2.0 - my);

		checkBounds();
		fireViewportListenersZoomChanged();
		render();
	}

	protected void checkBounds()
	{
		boolean update = false;
		if (-positionX + getWidth() / zoom > getScene().getWidth() + margin) {
			logger.debug("Moved out of viewport at right");
			internalSetPositionX(
					getWidth() / zoom - getScene().getWidth() - margin);
			update = true;
		}
		if (positionX > margin) {
			logger.debug("Scrolled too much to the left");
			internalSetPositionX(margin);
			update = true;
		}
		if (-positionY + getHeight() / zoom > getScene().getHeight() + margin) {
			logger.debug("Moved out of viewport at bottom");
			internalSetPositionY(
					getHeight() / zoom - getScene().getHeight() - margin);
			update = true;
		}
		if (positionY > margin) {
			logger.debug("Scrolled too much to the top");
			internalSetPositionY(margin);
			update = true;
		}
		if (update) {
			render();
		}
		fireViewportListenersViewportChanged();
	}

	private List<ViewportListener> viewportListeners = new ArrayList<>();

	@Override
	public void addViewportListener(ViewportListener listener)
	{
		viewportListeners.add(listener);
	}

	@Override
	public void removeViewportListener(ViewportListener listener)
	{
		viewportListeners.remove(listener);
	}

	protected void fireViewportListenersViewportChanged()
	{
		for (ViewportListener listener : viewportListeners) {
			listener.viewportChanged();
		}
	}

	protected void fireViewportListenersZoomChanged()
	{
		for (ViewportListener listener : viewportListeners) {
			listener.zoomChanged();
		}
	}

}
