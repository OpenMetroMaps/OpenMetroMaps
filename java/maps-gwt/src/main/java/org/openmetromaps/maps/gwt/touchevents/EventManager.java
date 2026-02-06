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

package org.openmetromaps.maps.gwt.touchevents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchEvent;

import de.topobyte.formatting.Formatting;

public class EventManager<T extends EventManagerManaged>
{

	final static Logger logger = LoggerFactory.getLogger(EventManager.class);

	private static final int DOUBLE_TAP_TIMEOUT = 300;
	private final double DOUBLE_TAP_DISTANCE;
	private final double LONG_PRESS_DISTANCE;

	private final T view;

	public EventManager(T view)
	{
		this.view = view;

		double density = getDevicePixelRatio();

		DOUBLE_TAP_DISTANCE = 50 * density;
		LONG_PRESS_DISTANCE = 30 * density;
	}

	public static final native double getDevicePixelRatio() /*-{
		return window.devicePixelRatio;
	}-*/;

	public boolean onTouchEvent(TouchEvent<?> event)
	{
		ToEvent e = ToEvents.getType(event);
		int count = event.getTouches().length();
		logger.info("count: " + count);

		updateNumDown(event, e);
		printStatus(event, e);

		switch (e) {
		default:
			break;
		case TOUCHSTART:
			if (numDownNow == 1) {
				actionDown(event, e);
			} else if (numDownNow == 2) {
				actionPointerDown(event, e);
			}
			break;
		case TOUCHEND:
			if (numDownNow == 0) {
				actionUp(event, e);
			} else if (numDownNow == 1) {
				actionPointerUp(event, e);
			}
			break;
		case TOUCHCANCEL:
			numDownBefore = numDownNow;
			numDownNow = 0;
			break;
		case TOUCHMOVE:
			actionMove(event, e);
			break;
		}

		return true;
	}

	/*
	 * number of pointers down, before and after the current event respectively
	 * 
	 * we maintain these across all events to be able to classify situations
	 * conveniently.
	 */
	private int numDownBefore = 0;
	private int numDownNow = 0;

	/*
	 * update the number of currently pressed pointers according to the current
	 * event.
	 */
	private void updateNumDown(TouchEvent<?> event, ToEvent e)
	{
		int count = event.getTouches().length();

		switch (e) {
		default:
			break;
		case TOUCHSTART:
			numDownNow = count;
			numDownBefore = count - 1;
			break;
		case TOUCHEND:
			numDownNow = count;
			numDownBefore = count + 1;
			break;
		case TOUCHCANCEL:
			numDownBefore = numDownNow;
			numDownNow = 0;
			break;
		case TOUCHMOVE:
			numDownNow = numDownBefore = count;
			break;
		}
	}

	/*
	 * print some status about the current pointers and their positions
	 */
	private void printStatus(TouchEvent<?> event, ToEvent e)
	{
		String name = event.getAssociatedType().getName();
		logger.info("event: " + name);

		StringBuilder strb = new StringBuilder();
		int count = event.getTouches().length();
		for (int i = 0; i < count; i++) {
			Touch touch = event.getTouches().get(i);
			int id = touch.getIdentifier();
			String pos = Formatting.format("%.0f,%.0f", touch.getClientX(),
					touch.getClientY());
			strb.append(Formatting.format("%d (%s)", id, pos));
			if (i < count - 1) {
				strb.append(", ");
			}
		}
		String ids = strb.toString();

		logger.info(Formatting.format("%d -> %d: %s", numDownBefore, numDownNow,
				ids));
	}

	/*
	 * methods for specific touch event actions
	 * 
	 * when these get called, internal variable are assumed to be set correctly.
	 */

	private void actionDown(TouchEvent<?> event, ToEvent e)
	{
		movementDown(event, e);
		click(event);
		updateDoubleTap(event);
	}

	private void actionPointerDown(TouchEvent<?> event, ToEvent e)
	{
		movementPointerDown(event, e);
		updateDoubleTap(event);
	}

	private void actionUp(TouchEvent<?> event, ToEvent e)
	{
		updateDoubleTap(event);
		if (doubleTapPress && doubleTapRelease) {
			twoFingerTap();
		}
	}

	private void actionPointerUp(TouchEvent<?> event, ToEvent e)
	{
		movementPointerUp(event);
		updateDoubleTap(event);
	}

	private void actionMove(TouchEvent<?> event, ToEvent e)
	{
		handleMovement(event);
	}

	private void actionCancel()
	{

	}

	/*
	 * single pointer clicking
	 */

	private long lastSinglePress = 0;
	private Point lastSinglePressPoint = null;
	private boolean usedLastClickForDoubleClick = false;
	private boolean sentLongPressMessage = false;

	private void click(TouchEvent<?> event)
	{
		Point point = point(event, 0);
		Point lastPoint = lastSinglePressPoint;
		lastSinglePressPoint = point;
		long time = timeStamp(event);
		long lastTime = lastSinglePress;
		lastSinglePress = time;
		if (lastPoint == null) {
			return;
		}
		if (usedLastClickForDoubleClick) {
			usedLastClickForDoubleClick = false;
		} else {
			float distance = new Vector2(point, lastPoint).length();
			long diff = time - lastTime;
			if (distance < DOUBLE_TAP_DISTANCE && diff < DOUBLE_TAP_TIMEOUT) {
				usedLastClickForDoubleClick = true;
				doubleClick(event, lastPoint);
				return;
			}
		}
	}

	private void doubleClick(TouchEvent<?> event, Point firstPoint)
	{
		Point point = point(event, 0);
		logger.info(Formatting.format("double click: %.1f, %.1f", point.getX(),
				point.getY()));
		if (view.canZoomIn()) {
			view.zoomIn(firstPoint.getX(), firstPoint.getY());
		}
	}

	/*
	 * two pointer clicking
	 */

	final static int TWO_FINGER_TAP_MAX_DELTA_TIME = 100;

	long time1down, time2down, time2up, time1up;
	boolean doubleTapPress = false;
	boolean doubleTapRelease = false;
	Point doubleTapPoint1 = null;
	Point doubleTapPoint2 = null;

	private static final int timeStamp(TouchEvent<?> event)
	{
		return timeStamp(event.getNativeEvent());
	}

	private static final native int timeStamp(NativeEvent event) /*-{
		return event.timeStamp;
	}-*/;

	private void updateDoubleTap(TouchEvent<?> event)
	{
		if (numDownBefore == 0 && numDownNow == 1) {
			// first pointer pressed
			time1down = timeStamp(event);
			Touch touch = event.getTouches().get(0);
			doubleTapPoint1 = new Point(touch.getClientX(), touch.getClientY());
		} else if (numDownBefore == 1 && numDownNow == 2) {
			// second pointer pressed
			time2down = timeStamp(event);
			Touch touch = event.getTouches().get(1);
			doubleTapPress = time2down
					- time1down < TWO_FINGER_TAP_MAX_DELTA_TIME;
			doubleTapPoint2 = new Point(touch.getClientX(), touch.getClientY());
		} else if (numDownBefore == 2 && numDownNow == 1) {
			// second pointer up
			time2up = timeStamp(event);
		} else if (numDownBefore == 1 && numDownNow == 0) {
			// first pointer up
			time1up = timeStamp(event);
			doubleTapRelease = time1up
					- time2up < TWO_FINGER_TAP_MAX_DELTA_TIME;
		}
	}

	final static int TWO_FINGER_TAP_MAX_PRESSURE_LENGTH = 250;

	private void twoFingerTap()
	{
		long timePassed = time1up - time1down;
		if (timePassed < TWO_FINGER_TAP_MAX_PRESSURE_LENGTH) {
			float x = (doubleTapPoint1.getX() + doubleTapPoint2.getX()) / 2;
			float y = (doubleTapPoint1.getY() + doubleTapPoint2.getY()) / 2;
			if (view.canZoomOut()) {
				view.zoomOut(x, y);
			}
		}
	}

	/*
	 * movement
	 */

	private Point lastSingle = null;

	private TwoPoints initialDouble = null;
	private TwoPoints lastDouble = null;

	private void movementDown(TouchEvent<?> event, ToEvent e)
	{
		Touch touch = event.getTouches().get(0);
		lastSingle = new Point(touch.getClientX(), touch.getClientY());
	}

	private void movementPointerDown(TouchEvent<?> event, ToEvent e)
	{
		if (numDownNow == 2) {
			Point p1 = point(event, 0);
			Point p2 = point(event, 1);
			initialDouble = new TwoPoints(p1, p2);
			lastDouble = new TwoPoints(p1, p2);
		}
	}

	private void movementPointerUp(TouchEvent<?> event)
	{
		if (numDownNow == 1) {
			// In theory, we should be able to set the lastSingle position to
			// the remaining pointer location like this:
			//
			// Touch touch = event.getTouches().get(0);
			// lastSingle = new Point(touch.getClientX(), touch.getClientY());
			//
			// In practice this seems to produce movement glitches, so instead
			// set it to null and handle that special situation below.
			lastSingle = null;
		}
	}

	private void handleMovement(TouchEvent<?> event)
	{
		if (numDownNow == 1) {
			Point current = point(event, 0);
			if (lastSingle != null) {
				Vector2 distance = new Vector2(lastSingle, current);
				lastSingle = current;
				logger.info("touch-pinch single move: " + distance.length());
				view.move(distance);
			} else {
				lastSingle = current;
			}
		} else if (numDownNow == 2) {
			Point p1 = point(event, 0);
			Point p2 = point(event, 1);
			TwoPoints current = new TwoPoints(p1, p2);

			Vector2 distance = new Vector2(lastDouble.getMidpoint(),
					current.getMidpoint());

			float currentPinch = current.distance();
			float lastPinch = lastDouble.distance();
			float factor = currentPinch / lastPinch;

			logger.info(Formatting.format(
					"touch-pinch initial %.2f, current %.2f, last %.2f",
					initialDouble.distance(), current.distance(),
					lastDouble.distance()));

			logger.info("touch-pinch current: " + currentPinch);
			logger.info("touch-pinch last: " + lastPinch);
			logger.info("touch-pinch factor: " + factor);

			Point midpoint = lastDouble.getMidpoint();
			view.zoom(midpoint.x, midpoint.y, factor);
			view.move(distance);

			lastDouble = current;
		}
	}

	private Point point(TouchEvent<?> event, int i)
	{
		Touch touch = event.getTouches().get(i);
		return new Point(touch.getClientX(), touch.getClientY());
	}

}
