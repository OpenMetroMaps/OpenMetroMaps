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

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lina.Matrix;
import de.topobyte.viewports.BaseScenePanel;
import de.topobyte.viewports.Renderable;
import de.topobyte.viewports.geometry.CoordinateTransformer;
import de.topobyte.viewports.scrolling.TransformHelper;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class BaseMapWindowPanel extends BaseScenePanel
		implements LocationToPoint, Renderable
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

}
