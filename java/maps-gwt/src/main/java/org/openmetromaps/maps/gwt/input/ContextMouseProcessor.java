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

package org.openmetromaps.maps.gwt.input;

import org.openmetromaps.maps.gwt.ScrollableAdvancedPlanPanel;
import org.openmetromaps.maps.gwt.touchevents.Vector2;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.ui.Widget;

import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.scrolling.DragInfo;

public class ContextMouseProcessor extends BaseMouseProcessor
{

	private boolean pressed = false;
	private DragInfo dragInfo = null;

	private ScrollableAdvancedPlanPanel panel;

	public ContextMouseProcessor(ScrollableAdvancedPlanPanel panel)
	{
		this.panel = panel;
	}

	@Override
	public void onMouseDown(MouseDownEvent e)
	{
		super.onMouseDown(e);
		pressed = true;
		dragInfo = new DragInfo(e.getX(), e.getY());
	}

	@Override
	public void onMouseUp(MouseUpEvent e)
	{
		super.onMouseUp(e);
		pressed = false;
		dragInfo = null;

		Widget source = (Widget) e.getSource();
		int clickX = source.getAbsoluteLeft()
				+ e.getRelativeX(source.getElement());
		int clickY = source.getAbsoluteTop()
				+ e.getRelativeY(source.getElement());

		panel.showContextMenu(e.getX(), e.getY(), 8, clickX, clickY);
	}

	@Override
	public void onMouseMove(MouseMoveEvent e)
	{
		super.onMouseMove(e);
		if (pressed) {
			onMouseDragged(e);
		}
	}

	private void onMouseDragged(MouseMoveEvent e)
	{
		dragInfo.update(e.getX(), e.getY());
		Coordinate delta = dragInfo.getDeltaToLast();

		double dx = delta.getX();
		double dy = delta.getY();

		panel.move(new Vector2((float) dx, (float) dy));
		panel.render();
	}

}
