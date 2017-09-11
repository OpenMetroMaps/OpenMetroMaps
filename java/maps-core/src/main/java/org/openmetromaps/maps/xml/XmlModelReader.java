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

package org.openmetromaps.maps.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.topobyte.adt.geo.Coordinate;

public class XmlModelReader
{

	protected List<Line> linesList = new ArrayList<>();
	protected List<Station> stationsList = new ArrayList<>();
	protected List<Stop> stopsList = new ArrayList<>();

	protected Map<Line, Integer> lineToIndex = new HashMap<>();
	protected Map<Station, Integer> stationToIndex = new HashMap<>();
	protected Map<Stop, Integer> stopToIndex = new HashMap<>();

	public XmlModel read(InputStream is)
			throws ParserConfigurationException, SAXException, IOException
	{
		List<XmlLine> xmlLines = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);

		NodeList lineNodes = doc.getElementsByTagName("line");
		for (int i = 0; i < lineNodes.getLength(); i++) {
			Element eLine = (Element) lineNodes.item(i);

			NamedNodeMap attributes = eLine.getAttributes();
			String lineName = attributes.getNamedItem("name").getNodeValue();
			String color = attributes.getNamedItem("color").getNodeValue();
			String circular = attributes.getNamedItem("circular")
					.getNodeValue();

			boolean isCircular = circular.equals("true");

			List<XmlStop> stops = new ArrayList<>();

			NodeList stations = eLine.getElementsByTagName("station");
			for (int k = 0; k < stations.getLength(); k++) {
				Node station = stations.item(k);
				attributes = station.getAttributes();
				String stationName = attributes.getNamedItem("name")
						.getNodeValue();
				String valLon = attributes.getNamedItem("lon").getNodeValue();
				String valLat = attributes.getNamedItem("lat").getNodeValue();
				double lon = Double.parseDouble(valLon);
				double lat = Double.parseDouble(valLat);
				stops.add(new XmlStop(stationName, new Coordinate(lon, lat)));
			}

			xmlLines.add(new XmlLine(lineName, color, isCircular, stops));
		}

		return new XmlModel(xmlLines);
	}

}
