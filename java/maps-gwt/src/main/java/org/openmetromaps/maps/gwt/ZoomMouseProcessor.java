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

import com.google.gwt.event.dom.client.MouseWheelEvent;

import de.topobyte.viewports.HasSize;
import de.topobyte.viewports.Renderable;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.scrolling.HasMargin;
import de.topobyte.viewports.scrolling.HasScene;
import de.topobyte.viewports.scrolling.Viewport;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class ZoomMouseProcessor<T extends HasSize & Renderable & Viewport & HasScene & HasMargin>
		extends BaseMouseProcessor
{

	protected double zoomStep = 0.1;

	private T view;

	public ZoomMouseProcessor(T view)
	{
		this.view = view;
	}

	@Override
	public void onMouseWheel(MouseWheelEvent e)
	{
		super.onMouseWheel(e);

		boolean in = e.getDeltaY() < 0;

		Coordinate point = new Coordinate(e.getX(), e.getY());
		ViewportUtil.zoomFixed(view, point, in, zoomStep);

		view.render();
	}

}
