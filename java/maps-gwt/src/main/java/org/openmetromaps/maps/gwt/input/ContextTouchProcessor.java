// Copyright 2026 Sebastian Kuerten
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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.Widget;

public class ContextTouchProcessor
		extends PanTouchProcessor<ScrollableAdvancedPlanPanel>
{

	private static final int TAP_DISTANCE = 12;

	private ScrollableAdvancedPlanPanel panel;

	private boolean tapCandidate = false;
	private int tapIdentifier = -1;
	private int tapStartX = 0;
	private int tapStartY = 0;
	private int tapX = 0;
	private int tapY = 0;
	private int popupX = 0;
	private int popupY = 0;

	public ContextTouchProcessor(ScrollableAdvancedPlanPanel panel)
	{
		super(panel);
		this.panel = panel;
	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		handleTapStart(event);
		super.onTouchStart(event);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		super.onTouchEnd(event);
		handleTapEnd(event);
	}

	@Override
	public void onTouchCancel(TouchCancelEvent event)
	{
		super.onTouchCancel(event);
		cancelTap();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		handleTapMove(event);
		super.onTouchMove(event);
	}

	private void handleTapStart(TouchStartEvent event)
	{
		if (event.getTouches().length() != 1) {
			cancelTap();
			return;
		}

		Touch touch = event.getTouches().get(0);
		Widget source = (Widget) event.getSource();

		tapCandidate = true;
		tapIdentifier = touch.getIdentifier();
		tapStartX = touch.getRelativeX(source.getElement());
		tapStartY = touch.getRelativeY(source.getElement());
		updateTapPosition(source, touch);
	}

	private void handleTapMove(TouchMoveEvent event)
	{
		if (!tapCandidate) {
			return;
		}

		if (event.getTouches().length() != 1) {
			cancelTap();
			return;
		}

		Touch touch = event.getTouches().get(0);
		if (touch.getIdentifier() != tapIdentifier) {
			cancelTap();
			return;
		}

		Widget source = (Widget) event.getSource();
		updateTapPosition(source, touch);

		int dx = tapX - tapStartX;
		int dy = tapY - tapStartY;
		if (dx * dx + dy * dy > TAP_DISTANCE * TAP_DISTANCE) {
			cancelTap();
		}
	}

	private void handleTapEnd(TouchEndEvent event)
	{
		if (!tapCandidate || event.getTouches().length() != 0) {
			cancelTap();
			return;
		}

		JsArray<Touch> changedTouches = event.getChangedTouches();
		if (changedTouches.length() > 0) {
			Touch touch = changedTouches.get(0);
			if (touch.getIdentifier() != tapIdentifier) {
				cancelTap();
				return;
			}

			Widget source = (Widget) event.getSource();
			updateTapPosition(source, touch);
		}

		panel.showContextMenu(tapX, tapY, 16, popupX, popupY);
		cancelTap();
	}

	private void updateTapPosition(Widget source, Touch touch)
	{
		tapX = touch.getRelativeX(source.getElement());
		tapY = touch.getRelativeY(source.getElement());
		popupX = source.getAbsoluteLeft() + tapX;
		popupY = source.getAbsoluteTop() + tapY;
	}

	private void cancelTap()
	{
		tapCandidate = false;
		tapIdentifier = -1;
	}

}
