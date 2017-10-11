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

package org.openmetromap.maps.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelReader;
import org.openmetromaps.maps.xml.XmlStation;
import org.xml.sax.SAXException;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.BBoxHelper;
import de.topobyte.adt.geo.Coordinate;
import de.topobyte.geomath.WGS84;

public class TestConvertCoordinates
{

	public static void main(String[] args)
			throws ParserConfigurationException, SAXException, IOException
	{
		InputStream input = TestConvertCoordinates.class.getClassLoader()
				.getResourceAsStream("berlin.xml");
		XmlModel model = XmlModelReader.read(input);

		List<Coordinate> coordinates = new ArrayList<>();
		for (XmlStation station : model.getStations()) {
			Coordinate location = station.getLocation();
			coordinates.add(location);
		}

		convert(coordinates, 1000);
	}

	private static void convert(List<Coordinate> coordinates, double size)
	{
		BBox bbox = BBoxHelper.minimumBoundingBox(coordinates);
		System.out.println("bbox: " + bbox);

		double worldsize = 1;

		double x1 = WGS84.lon2merc(bbox.getLon1(), worldsize);
		double x2 = WGS84.lon2merc(bbox.getLon2(), worldsize);

		double y1 = WGS84.lat2merc(bbox.getLat1(), worldsize);
		double y2 = WGS84.lat2merc(bbox.getLat2(), worldsize);

		double spanX = Math.abs(x1 - x2);
		double spanY = Math.abs(y1 - y2);

		double biggerSpan = Math.max(spanX, spanY);
		double factor = size / biggerSpan;

		double minX = Math.min(x1, x2);
		double minY = Math.min(y1, y2);

		System.out.println(
				String.format("coordinates: %f,%f:%f,%f", x1, x2, y1, y2));
		System.out.println(String.format("spanX: %f, spanY: %f", spanX, spanY));

		List<Coordinate> newCoordinates = new ArrayList<>();

		for (Coordinate coordinate : coordinates) {
			double x = WGS84.lon2merc(coordinate.getLongitude(), worldsize);
			double y = WGS84.lat2merc(coordinate.getLatitude(), worldsize);
			double dx = x - minX;
			double dy = y - minY;
			double sx = dx * factor;
			double sy = dy * factor;
			newCoordinates.add(new Coordinate(sx, sy));
			System.out.println(String.format("%f, %f", sx, sy));
		}

		Coordinate minimum = Coordinate.minimum(newCoordinates);
		Coordinate maximum = Coordinate.maximum(newCoordinates);
		System.out.println(String.format("%f:%f,%f:%f", minimum.getLongitude(),
				maximum.getLongitude(), minimum.getLatitude(),
				maximum.getLatitude()));
	}

}
