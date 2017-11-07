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

import org.openmetromaps.maps.CoordinateConverter;
import org.openmetromaps.maps.Points;
import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelReader;
import org.openmetromaps.maps.xml.XmlStation;
import org.xml.sax.SAXException;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.BBoxHelper;
import de.topobyte.lightgeom.lina.Point;

public class TestConvertCoordinates
{

	public static void main(String[] args)
			throws ParserConfigurationException, SAXException, IOException
	{
		InputStream input = TestConvertCoordinates.class.getClassLoader()
				.getResourceAsStream("berlin.xml");
		XmlModel model = XmlModelReader.read(input);

		convert(model.getStations(), 1000, 50);
	}

	private static void convert(List<XmlStation> stations, double size,
			double margin)
	{
		List<de.topobyte.adt.geo.Coordinate> coordinates = new ArrayList<>();
		for (XmlStation station : stations) {
			Coordinate location = station.getLocation();
			coordinates.add(new de.topobyte.adt.geo.Coordinate(
					location.getLongitude(), location.getLatitude()));
		}

		BBox bbox = BBoxHelper.minimumBoundingBox(coordinates);
		System.out.println("bbox: " + bbox);

		CoordinateConverter converter = new CoordinateConverter(bbox, size,
				margin);
		System.out.println(String.format("size: %f x %f", converter.getWidth(),
				converter.getHeight()));

		List<Point> newCoordinates = new ArrayList<>();

		for (XmlStation station : stations) {
			Point c = converter.convert(station.getLocation());
			newCoordinates.add(c);
			System.out.println(String.format("%f, %f: %s", c.getX(), c.getY(),
					station.getName()));
		}

		Point minimum = Points.minimum(newCoordinates);
		Point maximum = Points.maximum(newCoordinates);
		System.out.println(String.format("%f:%f,%f:%f", minimum.getX(),
				maximum.getX(), minimum.getY(), maximum.getY()));
	}

}
