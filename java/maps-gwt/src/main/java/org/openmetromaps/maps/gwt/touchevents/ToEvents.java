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
