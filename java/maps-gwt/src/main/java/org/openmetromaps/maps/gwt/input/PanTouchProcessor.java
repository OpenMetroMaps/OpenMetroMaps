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

package org.openmetromaps.maps.gwt.input;

import org.openmetromaps.maps.gwt.touchevents.EventManager;
import org.openmetromaps.maps.gwt.touchevents.EventManagerManaged;

import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

import de.topobyte.viewports.HasSize;
import de.topobyte.viewports.Renderable;
import de.topobyte.viewports.scrolling.HasMargin;
import de.topobyte.viewports.scrolling.HasScene;
import de.topobyte.viewports.scrolling.Viewport;

public class PanTouchProcessor<T extends HasSize & Renderable & Viewport & HasScene & HasMargin & EventManagerManaged>
		extends BaseTouchProcessor
{

	private EventManager<EventManagerManaged> eventManager;

	public PanTouchProcessor(T view)
	{
		eventManager = new EventManager<EventManagerManaged>(view);
	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		super.onTouchStart(event);
		eventManager.onTouchEvent(event);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		super.onTouchEnd(event);
		eventManager.onTouchEvent(event);
	}

	@Override
	public void onTouchCancel(TouchCancelEvent event)
	{
		super.onTouchCancel(event);
	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		super.onTouchMove(event);
		eventManager.onTouchEvent(event);
	}

}
