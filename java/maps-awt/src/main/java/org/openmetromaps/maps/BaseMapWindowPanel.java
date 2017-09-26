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

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import de.topobyte.adt.geo.Coordinate;
import de.topobyte.jeography.core.mapwindow.SteplessMapWindow;
import de.topobyte.jeography.core.viewbounds.ViewBounds;
import de.topobyte.lightgeom.lina.Point;

public class BaseMapWindowPanel extends JPanel
		implements ComponentListener, LocationToPoint
{

	private static final long serialVersionUID = 1L;

	protected SteplessMapWindow mapWindow;

	public BaseMapWindowPanel(Coordinate position, int minZoom, int maxZoom,
			ViewBounds bounds)
	{
		mapWindow = new SteplessMapWindow(1, 1, 12, position.getLongitude(),
				position.getLatitude());
		mapWindow.setMinZoom(minZoom);
		mapWindow.setMaxZoom(maxZoom);
		if (bounds != null) {
			mapWindow.setViewBounds(bounds);
		}

		BaseMouseEventProcessor mep = new BaseMouseEventProcessor(this, mapWindow);

		addComponentListener(this);
		addMouseListener(mep);
		addMouseMotionListener(mep);
		addMouseWheelListener(mep);
	}

	@Override
	// from ComponentListener
	public void componentResized(ComponentEvent e)
	{
		int width = getWidth();
		int height = getHeight();
		mapWindow.resize(width, height);
		repaint();
	}

	@Override
	// from ComponentListener
	public void componentHidden(ComponentEvent e)
	{
		// do nothing here
	}

	@Override
	// from ComponentListener
	public void componentMoved(ComponentEvent e)
	{
		// do nothing here
	}

	@Override
	// from ComponentListener
	public void componentShown(ComponentEvent e)
	{
		// do nothing here
	}

	@Override
	public Point getPoint(Coordinate location)
	{
		double x = mapWindow.getX(location.lon);
		double y = mapWindow.getY(location.lat);
		return new Point(x, y);
	}

	@Override
	public Point getPoint(Coordinate location, Point point)
	{
		double x = mapWindow.getX(location.lon);
		double y = mapWindow.getY(location.lat);
		return point.set(x, y);
	}

	@Override
	public double getX(double lon)
	{
		return mapWindow.getX(lon);
	}

	@Override
	public double getY(double lat)
	{
		return mapWindow.getY(lat);
	}

	public SteplessMapWindow getMapWindow()
	{
		return mapWindow;
	}

}
