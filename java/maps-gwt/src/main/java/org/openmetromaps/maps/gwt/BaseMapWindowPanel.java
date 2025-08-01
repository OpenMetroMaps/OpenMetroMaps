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

import org.openmetromaps.maps.LocationToPoint;
import org.openmetromaps.maps.gwt.touchevents.EventManagerManaged;
import org.openmetromaps.maps.gwt.touchevents.Vector2;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lina.Matrix;
import de.topobyte.viewports.BaseScenePanel;
import de.topobyte.viewports.Renderable;
import de.topobyte.viewports.geometry.CoordinateTransformer;
import de.topobyte.viewports.scrolling.TransformHelper;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class BaseMapWindowPanel extends BaseScenePanel
		implements LocationToPoint, Renderable, EventManagerManaged
{

	// TODO: implement mouse processing
	// private MouseProcessor mouseProcessor = null;

	public BaseMapWindowPanel()
	{
		// BaseMouseEventProcessor<BaseMapWindowPanel> mep = new
		// BaseMouseEventProcessor<>(
		// this);
		// setMouseProcessor(mep);
	}

	// public void setMouseProcessor(MouseProcessor mouseProcessor)
	// {
	// if (this.mouseProcessor != null) {
	// removeMouseListener(this.mouseProcessor);
	// removeMouseMotionListener(this.mouseProcessor);
	// removeMouseWheelListener(this.mouseProcessor);
	// }
	// this.mouseProcessor = mouseProcessor;
	// addMouseListener(mouseProcessor);
	// addMouseMotionListener(mouseProcessor);
	// addMouseWheelListener(mouseProcessor);
	// }

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
	public double getX(double lon)
	{
		return ViewportUtil.getViewX(this, lon);
	}

	@Override
	public double getY(double lat)
	{
		return ViewportUtil.getViewY(this, lat);
	}

	@Override
	public void checkBounds()
	{
		super.checkBounds();
	}

	private CoordinateTransformer transformer;

	@Override
	public void render()
	{
		Matrix matrix = TransformHelper.createMatrix(scene, this);
		transformer = new CoordinateTransformer(matrix);
	}

	@Override
	public void move(Vector2 distance)
	{
		double x = getPositionX();
		double y = getPositionY();
		double nx = x
				+ distance.getX() * Util.getDevicePixelRatio() / getZoom();
		double ny = y
				+ distance.getY() * Util.getDevicePixelRatio() / getZoom();
		setPositionX(nx);
		setPositionY(ny);
		render();
	}

	private static final double ZOOM_FACTOR = 1.1;

	@Override
	public void zoomIn()
	{
		zoom(ZOOM_FACTOR);
	}

	@Override
	public void zoomOut()
	{
		zoom(1 / ZOOM_FACTOR);
	}

	private void zoom(double zoomFactor)
	{
		double lowestZoom = 0.001;
		double highestZoom = 100;
		double targetZoom = Math.max(Math.min(zoom * zoomFactor, highestZoom),
				lowestZoom);
		setZoom(targetZoom);
	}

	@Override
	public void zoom(float x, float y, float zoomFactor)
	{
		double currentZoom = getZoom();
		setZoom(currentZoom * zoomFactor);
		checkBounds();
		render();
	}

	@Override
	public void zoomIn(float x, float y)
	{
		zoom(ZOOM_FACTOR);
	}

	@Override
	public void zoomOut(float x, float y)
	{
		zoom(1 / ZOOM_FACTOR);
	}

	@Override
	public void longClick(float x, float y)
	{
		// nothing happens here
	}

	@Override
	public boolean canZoomIn()
	{
		return true;
	}

	@Override
	public boolean canZoomOut()
	{
		return true;
	}

}
