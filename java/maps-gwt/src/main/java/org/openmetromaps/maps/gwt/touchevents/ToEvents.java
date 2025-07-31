package org.openmetromaps.maps.gwt.touchevents;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.TouchEvent;

public class ToEvents
{

	static Map<String, ToEvent> nameToType = new HashMap<>();

	static {
		for (ToEvent event : ToEvent.values()) {
			nameToType.put(event.getEventName(), event);
		}
	}

	public static ToEvent getType(String name)
	{
		return nameToType.get(name);
	}

	public static ToEvent getType(TouchEvent<?> event)
	{
		return nameToType.get(event.getAssociatedType().getName());
	}

}
