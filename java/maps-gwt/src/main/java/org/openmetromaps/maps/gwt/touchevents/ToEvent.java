package org.openmetromaps.maps.gwt.touchevents;

public enum ToEvent {

	TOUCHCANCEL("touchcancel"),
	TOUCHEND("touchend"),
	TOUCHMOVE("touchmove"),
	TOUCHSTART("touchstart");

	private String eventName;

	private ToEvent(String name)
	{
		eventName = name;
	}

	public String getEventName()
	{
		return eventName;
	}

}
