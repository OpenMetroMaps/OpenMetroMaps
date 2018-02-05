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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lina.Matrix;
import de.topobyte.viewports.BaseScenePanel;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.CoordinateTransformer;
import de.topobyte.viewports.geometry.Rectangle;
import de.topobyte.viewports.scrolling.TransformHelper;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class BaseMapWindowPanel extends BaseScenePanel
		implements LocationToPoint
{

	private static final long serialVersionUID = 1L;

	private MouseProcessor mouseProcessor = null;

	public BaseMapWindowPanel(Rectangle scene)
	{
		super(scene);

		BaseMouseEventProcessor<BaseMapWindowPanel> mep = new BaseMouseEventProcessor<>(
				this);
		setMouseProcessor(mep);
	}

	public void setMouseProcessor(MouseProcessor mouseProcessor)
	{
		if (this.mouseProcessor != null) {
			removeMouseListener(this.mouseProcessor);
			removeMouseMotionListener(this.mouseProcessor);
			removeMouseWheelListener(this.mouseProcessor);
		}
		this.mouseProcessor = mouseProcessor;
		addMouseListener(mouseProcessor);
		addMouseMotionListener(mouseProcessor);
		addMouseWheelListener(mouseProcessor);
	}

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

	@Override
	public void checkBounds()
	{
		super.checkBounds();
	}

	private CoordinateTransformer transformer;

	@Override
	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Matrix matrix = TransformHelper.createMatrix(scene, this);
		transformer = new CoordinateTransformer(matrix);
	}

	protected void fillRect(Graphics2D g, double x1, double y1, double x2,
			double y2)
	{
		Coordinate start = new Coordinate(x1, y1);
		Coordinate end = new Coordinate(x2, y2);
		Coordinate tStart = transformer.transform(start);
		Coordinate tEnd = transformer.transform(end);

		Rectangle2D rectangle = new Rectangle2D.Double(tStart.getX(),
				tStart.getY(), tEnd.getX() - tStart.getX(),
				tEnd.getY() - tStart.getY());
		g.fill(rectangle);
	}

}
