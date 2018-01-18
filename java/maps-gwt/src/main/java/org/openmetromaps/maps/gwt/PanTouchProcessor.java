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

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

import de.topobyte.viewports.HasSize;
import de.topobyte.viewports.Renderable;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.scrolling.DragInfo;
import de.topobyte.viewports.scrolling.HasMargin;
import de.topobyte.viewports.scrolling.HasScene;
import de.topobyte.viewports.scrolling.Viewport;

public class PanTouchProcessor<T extends HasSize & Renderable & Viewport & HasScene & HasMargin>
		extends BaseTouchProcessor
{

	private boolean pressed = false;
	private DragInfo dragInfo = null;

	private T view;

	public PanTouchProcessor(T view)
	{
		this.view = view;
	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		super.onTouchStart(event);
		Touch touch = event.getTouches().get(0);
		pressed = true;
		dragInfo = new DragInfo(touch.getClientX(), touch.getClientY());
	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		super.onTouchEnd(event);
		pressed = false;
		dragInfo = null;
	}

	@Override
	public void onTouchCancel(TouchCancelEvent event)
	{
		super.onTouchCancel(event);
		pressed = false;
		dragInfo = null;
	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		super.onTouchMove(event);
		if (pressed) {
			onDragged(event);
		}
	}

	private void onDragged(TouchMoveEvent e)
	{
		Touch touch = e.getTouches().get(0);
		dragInfo.update(touch.getClientX(), touch.getClientY());
		Coordinate delta = dragInfo.getDeltaToLast();

		double dx = delta.getX() / view.getZoom();
		double dy = delta.getY() / view.getZoom();
		double nx = view.getPositionX() + dx;
		double ny = view.getPositionY() + dy;

		view.setPositionX(nx);
		view.setPositionY(ny);
		view.render();
	}

}
