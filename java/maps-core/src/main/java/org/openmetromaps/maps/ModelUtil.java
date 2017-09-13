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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.maps.painting.core.ColorCode;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.BBoxHelper;
import de.topobyte.adt.geo.Coordinate;

public class ModelUtil
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
		List<Stop> stops = stop.getStation().getStops();
		if (stops.size() == 1) {
			return stop.getLocation();
		}
		return ModelUtil.mean(stops);
	}

	public static Coordinate mean(List<Stop> stops)
	{
		double x = 0;
		double y = 0;
		int n = 0;
		final int nStops = stops.size();
		for (int i = 0; i < nStops; i++) {
			Stop stop = stops.get(i);
			x += stop.getLocation().lon;
			y += stop.getLocation().lat;
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
				x += stop.getLocation().lon;
				y += stop.getLocation().lat;
				n++;
			}
		}
		if (n == 1) {
			new Coordinate(x, y);
		}
		return new Coordinate(x / n, y / n);
	}

	public static ColorCode getColor(Line line)
	{
		String sColor = line.getColor();
		return new ColorCode(Integer.decode(sColor));
	}

	public static ViewConfig viewConfig(ModelData model)
	{
		List<Coordinate> coords = new ArrayList<>();
		for (Station station : model.stations) {
			for (Stop stop : station.getStops()) {
				coords.add(stop.getLocation());
			}
		}
		BBox bbox = BBoxHelper.minimumBoundingBox(null, coords);

		coords.sort(new CoordinateComparatorLongitude());
		double medianLon = coords.get(coords.size() / 2).getLongitude();

		coords.sort(new CoordinateComparatorLatitude());
		double medianLat = coords.get(coords.size() / 2).getLatitude();

		Coordinate startPosition = new Coordinate(medianLon, medianLat);

		return new ViewConfig(bbox, startPosition);
	}

}
