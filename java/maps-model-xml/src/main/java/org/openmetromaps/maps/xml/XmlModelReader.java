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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.model.Coordinate;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.xml.domabstraction.iface.IDocument;
import de.topobyte.xml.domabstraction.iface.IDocumentFactory;
import de.topobyte.xml.domabstraction.iface.IElement;
import de.topobyte.xml.domabstraction.iface.INodeList;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class XmlModelReader
{

	public static XmlModel read(IDocumentFactory factory, InputStream is)
			throws ParsingException
	{
		XmlModelReader reader = new XmlModelReader();
		return reader.readModel(factory, is);
	}

	public static XmlModel read(IDocument document) throws ParsingException
	{
		XmlModelReader reader = new XmlModelReader();
		return reader.readModel(document);
	}

	private String version;
	private List<XmlStation> xmlStations = new ArrayList<>();
	private List<XmlLine> xmlLines = new ArrayList<>();
	private List<XmlView> xmlViews = new ArrayList<>();

	private Map<String, XmlStation> nameToStation = new HashMap<>();

	private XmlModelReader()
	{
		// private constructor
	}

	private XmlModel readModel(IDocumentFactory factory, InputStream is)
			throws ParsingException
	{
		IDocument doc = factory.parse(is);
		return readModel(doc);
	}

	private XmlModel readModel(IDocument doc) throws ParsingException
	{
		parseStations(doc);
		parseLines(doc);
		parseViews(doc);

		return new XmlModel(version, xmlStations, xmlLines, xmlViews);
	}

	private void parseStations(IDocument doc)
	{
		INodeList allOmmFiles = doc.getElementsByTagName("omm-file");
		IElement firstOmmFile = allOmmFiles.element(0);

		version = firstOmmFile.getAttribute("version");

		INodeList allStations = firstOmmFile.getElementsByTagName("stations");
		IElement firstStations = allStations.element(0);
		INodeList stationList = firstStations.getElementsByTagName("station");

		for (int i = 0; i < stationList.getLength(); i++) {
			IElement eStation = stationList.element(i);

			String stationName = eStation.getAttribute("name");
			String valLon = eStation.getAttribute("lon");
			String valLat = eStation.getAttribute("lat");
			double lon = Double.parseDouble(valLon);
			double lat = Double.parseDouble(valLat);

			XmlStation station = new XmlStation(stationName,
					new Coordinate(lon, lat));
			xmlStations.add(station);
		}

		for (XmlStation station : xmlStations) {
			nameToStation.put(station.getName(), station);
		}
	}

	private void parseLines(IDocument doc)
	{
		INodeList allLines = doc.getElementsByTagName("lines");
		IElement firstLines = allLines.element(0);
		INodeList lineList = firstLines.getElementsByTagName("line");

		for (int i = 0; i < lineList.getLength(); i++) {
			IElement eLine = lineList.element(i);

			String lineName = eLine.getAttribute("name");
			String color = eLine.getAttribute("color");
			String circular = eLine.getAttribute("circular");

			boolean isCircular = circular.equals("true");

			List<XmlStation> stops = new ArrayList<>();

			INodeList stopList = eLine.getElementsByTagName("stop");
			for (int k = 0; k < stopList.getLength(); k++) {
				IElement station = stopList.element(k);
				String stationName = station.getAttribute("station");
				XmlStation xmlStation = nameToStation.get(stationName);
				stops.add(xmlStation);
			}

			xmlLines.add(new XmlLine(lineName, color, isCircular, stops));
		}
	}

	private void parseViews(IDocument doc)
	{
		INodeList allViews = doc.getElementsByTagName("view");
		for (int i = 0; i < allViews.getLength(); i++) {
			IElement eView = allViews.element(i);
			xmlViews.add(parseView(eView));
		}
	}

	private XmlView parseView(IElement eView)
	{
		String viewName = eView.getAttribute("name");

		String valSceneWidth = eView.getAttribute("scene-width");
		String valSceneHeight = eView.getAttribute("scene-height");

		double sceneWidth = Double.parseDouble(valSceneWidth);
		double sceneHeight = Double.parseDouble(valSceneHeight);

		String valStartX = eView.getAttribute("start-x");
		String valStartY = eView.getAttribute("start-y");

		double startX = Double.parseDouble(valStartX);
		double startY = Double.parseDouble(valStartY);

		XmlView view = new XmlView(viewName, sceneWidth, sceneHeight, startX,
				startY);

		parseViewStations(view, eView);

		parseViewEdges(view, eView);

		return view;
	}

	private void parseViewStations(XmlView view, IElement eView)
	{
		INodeList stationList = eView.getElementsByTagName("station");

		for (int i = 0; i < stationList.getLength(); i++) {
			IElement eStation = stationList.element(i);
			XmlViewStation station = parseViewStation(eStation);
			view.getStations().add(station);
		}
	}

	private XmlViewStation parseViewStation(IElement eStation)
	{
		String stationName = eStation.getAttribute("name");
		String valx = eStation.getAttribute("x");
		String valY = eStation.getAttribute("y");
		double x = Double.parseDouble(valx);
		double y = Double.parseDouble(valY);

		return new XmlViewStation(stationName, new Point(x, y));
	}

	private void parseViewEdges(XmlView view, IElement eView)
	{
		INodeList edgesList = eView.getElementsByTagName("edges");

		for (int i = 0; i < edgesList.getLength(); i++) {
			IElement eEdges = edgesList.element(i);
			XmlEdges edges = parseEdges(eEdges);
			view.getEdges().add(edges);
		}
	}

	private XmlEdges parseEdges(IElement eEdges)
	{
		String line = eEdges.getAttribute("line");

		XmlEdges edges = new XmlEdges(line);

		INodeList intervalList = eEdges.getElementsByTagName("interval");
		for (int i = 0; i < intervalList.getLength(); i++) {
			IElement eInterval = intervalList.element(i);
			parseInterval(edges, eInterval);
		}

		return edges;
	}

	private void parseInterval(XmlEdges edges, IElement eInterval)
	{
		String from = eInterval.getAttribute("from");
		String to = eInterval.getAttribute("to");
		edges.addInterval(new XmlInterval(from, to));
	}

}
