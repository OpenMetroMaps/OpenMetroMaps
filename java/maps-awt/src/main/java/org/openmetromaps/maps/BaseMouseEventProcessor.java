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

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JComponent;

import org.openmetromaps.swing.Util;

import de.topobyte.jeography.core.mapwindow.SteplessMapWindow;

public class BaseMouseEventProcessor implements MouseProcessor
{

	private JComponent c;
	private SteplessMapWindow mapWindow;
	protected double zoomStep = 0.1;

	public BaseMouseEventProcessor(JComponent c, SteplessMapWindow mapWindow)
	{
		this.c = c;
		this.mapWindow = mapWindow;
	}

	private java.awt.Point pointPress;
	private boolean mousePressed = false;

	@Override
	public void mouseClicked(MouseEvent e)
	{
		c.grabFocus();

		boolean control = Util.isControlPressed(e);

		if (e.getClickCount() == 2) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (!control) {
					zoomFixed(e.getPoint(), true, zoomStep);
				} else {
					mapWindow.zoomInToPosition(e.getX(), e.getY(), zoomStep);
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				if (!control) {
					zoomFixed(e.getPoint(), false, zoomStep);
				} else {
					mapWindow.zoomOutToPosition(e.getX(), e.getY(), zoomStep);
				}
			}
			c.repaint();
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
			c.repaint();
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
			zoomFixed(e.getPoint(), true, zoomStep);
		} else {
			zoomFixed(e.getPoint(), false, zoomStep);
		}
		c.repaint();
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
