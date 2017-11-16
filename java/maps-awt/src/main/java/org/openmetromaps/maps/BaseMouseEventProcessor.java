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

import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.scrolling.ViewportMouseListener;
import de.topobyte.viewports.scrolling.ViewportUtil;
import de.topobyte.viewports.scrolling.ViewportWithSignals;

public class BaseMouseEventProcessor<T extends JComponent & ViewportWithSignals>
		extends ViewportMouseListener implements MouseProcessor
{

	protected T c;
	protected double zoomStep = 0.1;

	public BaseMouseEventProcessor(T c)
	{
		super(c);
		this.c = c;
	}

	private java.awt.Point pointPress;
	private boolean mousePressed = false;

	@Override
	public void mouseClicked(MouseEvent e)
	{
		c.grabFocus();

		Coordinate point = new Coordinate(e.getX(), e.getY());
		boolean control = Util.isControlPressed(e);

		if (e.getClickCount() == 2) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (!control) {
					ViewportUtil.zoomFixed(c, point, true, zoomStep);
				} else {
					// TODO: re-enable zoom-and-center
					// mapWindow.zoomInToPosition(e.getX(), e.getY(), zoomStep);
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				if (!control) {
					ViewportUtil.zoomFixed(c, point, false, zoomStep);
				} else {
					// TODO: re-enable zoom-and-center
					// mapWindow.zoomOutToPosition(e.getX(), e.getY(),
					// zoomStep);
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
			c.setPositionX(c.getPositionX() - dx / c.getZoom());
			c.setPositionY(c.getPositionY() - dy / c.getZoom());
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
		Coordinate point = new Coordinate(e.getX(), e.getY());
		if (rotation < 0) {
			ViewportUtil.zoomFixed(c, point, true, zoomStep);
		} else {
			ViewportUtil.zoomFixed(c, point, false, zoomStep);
		}
		c.repaint();
	}

}
