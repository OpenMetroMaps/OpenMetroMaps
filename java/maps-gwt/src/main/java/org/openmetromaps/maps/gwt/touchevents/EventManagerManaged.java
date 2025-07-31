package org.openmetromaps.maps.gwt.touchevents;

public interface EventManagerManaged
{

	public void move(Vector2 distance);

	public void zoomIn();

	public void zoomOut();

	public void zoom(float x, float y, float zoomFactor);

	public void zoomIn(float x, float y);

	public void zoomOut(float x, float y);

	public void longClick(float x, float y);

	public boolean canZoomIn();

	public boolean canZoomOut();

}
