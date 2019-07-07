// Copyright 2019 Sebastian Kuerten
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

package org.openmetromaps.newformat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Joiner;

import de.topobyte.xml4jah.dom.DocumentWriter;

public class NewFormatWriter
{

	public void write(OutputStream os, ModelData data, List<MapView> views)
			throws ParserConfigurationException, IOException
	{
		// Create document

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.newDocument();

		// Add data to document

		Element eMain = doc.createElement("metromap");
		eMain.setAttribute("version", "0.0.1");

		Element eDescription = doc.createElement("description");
		eDescription.setTextContent("a metro map in the new format");

		Element eGraph = doc.createElement("graph");
		eGraph.setAttribute("id", "g1");

		doc.appendChild(eMain);
		eMain.appendChild(eDescription);
		eMain.appendChild(eGraph);

		for (Station station : data.stations) {
			Element eNode = doc.createElement("node");
			eGraph.appendChild(eNode);

			eNode.setAttribute("id", nodeId(station));
			eNode.setTextContent(station.getName());
		}

		for (Line line : data.lines) {
			Element eLine = doc.createElement("metroline");
			eGraph.appendChild(eLine);

			eLine.setAttribute("id", lineId(line));
			eLine.setAttribute("color", line.getColor());
			eLine.setTextContent(line.getName());
		}

		LineNetworkBuilder graphBuilder = new LineNetworkBuilder(data,
				MapModelUtil.allEdges(data));
		LineNetwork network = graphBuilder.getGraph();

		for (Edge edge : network.getEdges()) {
			String id1 = nodeId(edge.n1.station);
			String id2 = nodeId(edge.n2.station);
			List<NetworkLine> lines = edge.lines;
			List<String> lineIds = new ArrayList<>();
			for (NetworkLine line : lines) {
				lineIds.add(lineId(line.line));
			}

			Element eEdge = doc.createElement("edge");
			eGraph.appendChild(eEdge);

			eEdge.setAttribute("id", edgeId());
			eEdge.setAttribute("source", id1);
			eEdge.setAttribute("target", id2);
			eEdge.setTextContent(Joiner.on(", ").join(lineIds));
		}

		// Write document

		DocumentWriter writer = new DocumentWriter();
		writer.write(doc, os);
	}

	private String nodeId(Station station)
	{
		String id = station.getName().toLowerCase();
		return id;
	}

	private String lineId(Line line)
	{
		String id = line.getName().toLowerCase();
		return id;
	}

	private int edgeCount = 0;

	private String edgeId()
	{
		return "e" + ++edgeCount;
	}

}
