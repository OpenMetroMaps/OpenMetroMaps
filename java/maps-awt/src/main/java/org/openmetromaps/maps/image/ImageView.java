// Copyright 2018 Sebastian Kuerten
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

package org.openmetromaps.maps.image;

import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.LocationToPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.viewports.geometry.Rectangle;
import de.topobyte.viewports.scrolling.ViewportListener;
import de.topobyte.viewports.scrolling.ViewportUtil;
import de.topobyte.viewports.scrolling.ViewportWithSignals;

public class ImageView implements ViewportWithSignals, LocationToPoint
{

	final static Logger logger = LoggerFactory.getLogger(ImageView.class);

	protected Rectangle scene;

	protected int width = 0;
	protected int height = 0;

	protected int margin = 0;

	protected double positionX = 0;
	protected double positionY = 0;
	protected double zoom = 1;

	public ImageView(Rectangle scene, int width, int height)
	{
		this.scene = scene;
		this.width = width;
		this.height = height;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public Rectangle getScene()
	{
		return scene;
	}

	// ViewportWithSignals

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
		return width;
	}

	@Override
	public double getViewportHeight()
	{
		return height;
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
	}

	protected void checkBounds()
	{
		if (-positionX + getWidth() / zoom > getScene().getWidth() + margin) {
			logger.debug("Moved out of viewport at right");
			internalSetPositionX(
					getWidth() / zoom - getScene().getWidth() - margin);
		}
		if (positionX > margin) {
			logger.debug("Scrolled too much to the left");
			internalSetPositionX(margin);
		}
		if (-positionY + getHeight() / zoom > getScene().getHeight() + margin) {
			logger.debug("Moved out of viewport at bottom");
			internalSetPositionY(
					getHeight() / zoom - getScene().getHeight() - margin);
		}
		if (positionY > margin) {
			logger.debug("Scrolled too much to the top");
			internalSetPositionY(margin);
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

	// LocationToPoint

	@Override
	public Point getPoint(Point location)
	{
		double x = ViewportUtil.getViewX(this, location.x);
		double y = ViewportUtil.getViewY(this, location.y);
		return new Point(x, y);
	}

	@Override
	public Point getPoint(Point location, Point point)
	{
		double x = ViewportUtil.getViewX(this, location.x);
		double y = ViewportUtil.getViewY(this, location.y);
		return point.set(x, y);
	}

	@Override
	public double getX(double x)
	{
		return ViewportUtil.getViewX(this, x);
	}

	@Override
	public double getY(double y)
	{
		return ViewportUtil.getViewY(this, y);
	}

}