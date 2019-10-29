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

import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.MapViewStatus;
import org.openmetromaps.maps.PlanRenderer;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.image.ImageView;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.painting.core.GenericPaintFactory;
import org.openmetromaps.maps.painting.core.Painter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Joiner;

import de.topobyte.viewports.geometry.Rectangle;
import de.topobyte.xml4jah.dom.DocumentWriter;

public class NewFormatWriter
{

	private StationMode stationMode = StationMode.CONVEX;
	private SegmentMode segmentMode = SegmentMode.CURVE;

	public StationMode getStationMode()
	{
		return stationMode;
	}

	public void setStationMode(StationMode stationMode)
	{
		this.stationMode = stationMode;
	}

	public SegmentMode getSegmentMode()
	{
		return segmentMode;
	}

	public void setSegmentMode(SegmentMode segmentMode)
	{
		this.segmentMode = segmentMode;
	}

	public void write(OutputStream os, ModelData data, List<MapView> views)
			throws ParserConfigurationException, IOException
	{
		Ids ids = new Ids();

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

			String nodeId = nodeId(station);
			ids.setNodeId(station, nodeId);
			eNode.setAttribute("id", nodeId);
			eNode.setTextContent(station.getName());
		}

		for (Line line : data.lines) {
			Element eLine = doc.createElement("metroline");
			eGraph.appendChild(eLine);

			String lineId = lineId(line);
			ids.setLineId(line, lineId);
			eLine.setAttribute("id", lineId);
			eLine.setAttribute("color", line.getColor());
			eLine.setTextContent(line.getName());
		}

		LineNetwork network = views.get(0).getLineNetwork();
		for (Edge edge : network.getEdges()) {
			String id1 = ids.getNodeId(edge.n1.station);
			String id2 = ids.getNodeId(edge.n2.station);
			List<NetworkLine> lines = edge.lines;
			List<String> lineIds = new ArrayList<>();
			for (NetworkLine line : lines) {
				lineIds.add(ids.getLineId(line.line));
			}

			Element eEdge = doc.createElement("edge");
			eGraph.appendChild(eEdge);

			String edgeId = edgeId();
			ids.setEdgeId(edge, edgeId);
			eEdge.setAttribute("id", edgeId);
			eEdge.setAttribute("source", id1);
			eEdge.setAttribute("target", id2);
			eEdge.setTextContent(Joiner.on(", ").join(lineIds));
		}

		for (int i = 0; i < views.size(); i++) {
			MapView view = views.get(i);
			network = view.getLineNetwork();
			Rectangle scene = view.getConfig().getScene();

			double width = scene.getWidth();
			double height = scene.getHeight();
			double zoom = 1;

			int imageWidth = (int) Math.ceil(width * zoom);
			int imageHeight = (int) Math.ceil(height * zoom);

			ImageView imageView = new ImageView(scene, imageWidth, imageHeight);
			imageView.setZoom(zoom);

			MapViewStatus mapViewStatus = new MapViewStatus();
			PlanRenderer renderer = new PlanRenderer(network, mapViewStatus,
					stationMode, segmentMode, imageView, imageView, 1,
					new GenericPaintFactory());

			Element eGeometricEmbedding = doc
					.createElement("geometricembedding");
			eGeometricEmbedding.setAttribute("id", "ge" + (i + 1));
			eGeometricEmbedding.setAttribute("width", "" + imageWidth);
			eGeometricEmbedding.setAttribute("height", "" + imageHeight);
			eMain.appendChild(eGeometricEmbedding);

			Painter painter = new NewFormatPainter(doc, eGeometricEmbedding,
					ids);
			renderer.paint(painter);
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
