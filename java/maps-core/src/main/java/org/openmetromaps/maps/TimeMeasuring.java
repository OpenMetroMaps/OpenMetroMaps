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

package org.openmetromaps.maps;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import de.topobyte.formatting.Formatting;

public class TimeMeasuring
{

	private Logger logger;

	public TimeMeasuring(Logger logger)
	{
		this.logger = logger;
	}

	private Map<String, Long> starts = new HashMap<>();
	private Map<String, Long> stops = new HashMap<>();

	public void start(String key)
	{
		starts.put(key, System.currentTimeMillis());
	}

	public void stop(String key)
	{
		stops.put(key, System.currentTimeMillis());
	}

	public void log(String key, String message)
	{
		long start = starts.get(key);
		long stop = stops.get(key);
		long interval = stop - start;
		logger.info(Formatting.format(message, interval));
	}

}
