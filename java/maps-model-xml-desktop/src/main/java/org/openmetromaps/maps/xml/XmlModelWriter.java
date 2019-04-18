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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.maps.Edges;
import org.openmetromaps.maps.Interval;
import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ViewConfig;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.topobyte.formatting.DoubleFormatter;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.xml4jah.dom.DocumentWriter;

public class XmlModelWriter
{

	public void write(OutputStream os, ModelData data, List<MapView> views)
			throws ParserConfigurationException, IOException
	{
		// Create document

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.newDocument();

		// Add data to document

		Element eMain = doc.createElement("omm-file");
		eMain.setAttribute("version", "1.0.0");

		Element eStations = doc.createElement("stations");
		Element eLines = doc.createElement("lines");

		doc.appendChild(eMain);
		eMain.appendChild(eStations);
		eMain.appendChild(eLines);

		MapModelUtil.sortStationsByName(data.stations);
		MapModelUtil.sortLinesByName(data.lines);

		DoubleFormatter df = new DoubleFormatter();
		df.setFractionDigits(6);

		for (Station station : data.stations) {
			Element eStation = doc.createElement("station");
			eStations.appendChild(eStation);

			Coordinate location = station.getLocation();

			eStation.setAttribute("name", station.getName());
			eStation.setAttribute("lon", df.format(location.getLongitude()));
			eStation.setAttribute("lat", df.format(location.getLatitude()));
		}

		for (Line line : data.lines) {
			Element eLine = doc.createElement("line");
			eLines.appendChild(eLine);

			eLine.setAttribute("name", line.getName());
			eLine.setAttribute("color", line.getColor());
			eLine.setAttribute("circular", Boolean.toString(line.isCircular()));

			for (Stop stop : line.getStops()) {
				Element eStop = doc.createElement("stop");
				eLine.appendChild(eStop);

				eStop.setAttribute("station", stop.getStation().getName());
			}
		}

		for (MapView view : views) {
			Element eView = doc.createElement("view");
			eMain.appendChild(eView);

			ViewConfig config = view.getConfig();
			eView.setAttribute("name", view.getName());
			eView.setAttribute("scene-width",
					df.format(config.getScene().getWidth()));
			eView.setAttribute("scene-height",
					df.format(config.getScene().getHeight()));
			eView.setAttribute("start-x",
					df.format(config.getStartPosition().getX()));
			eView.setAttribute("start-y",
					df.format(config.getStartPosition().getY()));

			LineNetwork lineNetwork = view.getLineNetwork();
			List<Node> nodes = new ArrayList<>(lineNetwork.getNodes());
			Collections.sort(nodes, new Comparator<Node>() {

				@Override
				public int compare(Node o1, Node o2)
				{
					return o1.station.getName().compareTo(o2.station.getName());
				}

			});

			List<Edges> edgesDefs = view.getEdges();
			for (Edges edgesDef : edgesDefs) {
				Element eEdges = doc.createElement("edges");
				eView.appendChild(eEdges);
				eEdges.setAttribute("line", edgesDef.getLine());

				for (Interval interval : edgesDef.getIntervals()) {
					Element eInterval = doc.createElement("interval");
					eEdges.appendChild(eInterval);
					eInterval.setAttribute("from", interval.getFrom());
					eInterval.setAttribute("to", interval.getTo());
				}
			}

			for (Node node : nodes) {
				Station station = node.station;

				Element eStation = doc.createElement("station");
				eView.appendChild(eStation);

				Point location = node.location;

				eStation.setAttribute("name", station.getName());
				eStation.setAttribute("x", df.format(location.getX()));
				eStation.setAttribute("y", df.format(location.getY()));
			}
		}

		// Write document

		DocumentWriter writer = new DocumentWriter();
		writer.write(doc, os);
	}

}
