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
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import de.topobyte.adt.geo.Coordinate;
import de.topobyte.jeography.core.mapwindow.SteplessMapWindow;
import de.topobyte.jeography.core.viewbounds.ViewBounds;
import de.topobyte.lightgeom.lina.Point;

public class BaseMapWindowPanel extends JPanel implements ComponentListener,
		MouseListener, MouseMotionListener, MouseWheelListener, LocationToPoint
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

		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
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

	private java.awt.Point pointPress;
	private boolean mousePressed = false;

	@Override
	public void mouseClicked(MouseEvent e)
	{
		this.grabFocus();

		boolean control = false;
		int modifiers = e.getModifiersEx();
		if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
			control = true;
		}

		if (e.getClickCount() == 2) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (!control) {
					zoomFixed(e.getPoint(), true, 0.1);
				} else {
					mapWindow.zoomInToPosition(e.getX(), e.getY(), 0.1);
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				if (!control) {
					zoomFixed(e.getPoint(), false, 0.1);
				} else {
					mapWindow.zoomOutToPosition(e.getX(), e.getY(), 0.1);
				}
			}
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// do nothing here
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// do nothing here
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1) {
			pointPress = e.getPoint();
			mousePressed = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1) {
			mousePressed = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (mousePressed) {
			java.awt.Point currentPoint = e.getPoint();
			int dx = pointPress.x - currentPoint.x;
			int dy = pointPress.y - currentPoint.y;
			pointPress = currentPoint;
			// down right movement is negative for both
			mapWindow.move(dx, dy);
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// do nothing here
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int rotation = e.getWheelRotation();
		if (rotation < 0) {
			zoomFixed(e.getPoint(), true, 0.1);
		} else {
			zoomFixed(e.getPoint(), false, 0.1);
		}
		repaint();
	}

	private void zoomFixed(java.awt.Point point, boolean in, double zoomStep)
	{
		// (lon, lat) that we want to keep fixed at the screen point (x, y)
		double flon = mapWindow.getPositionLon(point.x);
		double flat = mapWindow.getPositionLat(point.y);

		if (in) {
			mapWindow.zoomIn(zoomStep);
		} else {
			mapWindow.zoomOut(zoomStep);
		}

		// (x, y) of the (lon, lat) after applying the zoom change
		double fx = mapWindow.getX(flon);
		double fy = mapWindow.getY(flat);
		// shift the map to keep the (lon, lat) fixed
		mapWindow.move((int) Math.round(fx - point.x),
				(int) Math.round(fy - point.y));
	}

}
