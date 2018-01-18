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

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

import de.topobyte.viewports.HasSize;
import de.topobyte.viewports.Renderable;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.scrolling.DragInfo;
import de.topobyte.viewports.scrolling.HasMargin;
import de.topobyte.viewports.scrolling.HasScene;
import de.topobyte.viewports.scrolling.Viewport;

public class PanMouseProcessor<T extends HasSize & Renderable & Viewport & HasScene & HasMargin>
		extends BaseMouseProcessor
{

	private boolean pressed = false;
	private DragInfo dragInfo = null;

	private T view;

	public PanMouseProcessor(T view)
	{
		this.view = view;
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

		double dx = delta.getX() / view.getZoom();
		double dy = delta.getY() / view.getZoom();
		double nx = view.getPositionX() + dx;
		double ny = view.getPositionY() + dy;

		view.setPositionX(nx);
		view.setPositionY(ny);
		view.render();
	}

}
