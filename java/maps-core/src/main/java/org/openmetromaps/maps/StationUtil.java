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

import java.util.Collection;
import java.util.List;

import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

public class StationUtil
{

	public static boolean isLastStopOfALine(Station station)
	{
		List<Stop> stops = station.getStops();
		final int nStops = stops.size();
		for (int i = 0; i < nStops; i++) {
			Stop stop = stops.get(i);
			Line line = stop.getLine();
			if (line.isCircular()) {
				continue;
			}
			List<Stop> lineStops = line.getStops();
			if (stop == lineStops.get(0)
					|| stop == lineStops.get(lineStops.size() - 1)) {
				return true;
			}
		}
		return false;
	}

	public static Coordinate location(Stop stop)
	{
		if (stop.getLocation() != null) {
			return stop.getLocation();
		}
		return stop.getStation().getLocation();
	}

	public static Coordinate mean(List<Stop> stops)
	{
		double x = 0;
		double y = 0;
		int n = 0;
		final int nStops = stops.size();
		for (int i = 0; i < nStops; i++) {
			Stop stop = stops.get(i);
			x += stop.getLocation().getLongitude();
			y += stop.getLocation().getLatitude();
			n++;
		}
		if (n == 1) {
			new Coordinate(x, y);
		}
		return new Coordinate(x / n, y / n);
	}

	public static Coordinate meanOfStations(Collection<Station> stations)
	{
		double x = 0;
		double y = 0;
		int n = 0;
		for (Station station : stations) {
			List<Stop> stops = station.getStops();
			final int nStops = stops.size();
			for (int i = 0; i < nStops; i++) {
				Stop stop = stops.get(i);
				x += stop.getLocation().getLongitude();
				y += stop.getLocation().getLatitude();
				n++;
			}
		}
		if (n == 1) {
			new Coordinate(x, y);
		}
		return new Coordinate(x / n, y / n);
	}

}
