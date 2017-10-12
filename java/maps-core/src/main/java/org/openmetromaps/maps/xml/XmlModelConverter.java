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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.ViewConfig;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

public class XmlModelConverter
{

	protected List<Line> linesList = new ArrayList<>();
	protected List<Station> stationsList = new ArrayList<>();
	protected List<Stop> stopsList = new ArrayList<>();

	protected Map<Line, Integer> lineToIndex = new HashMap<>();
	protected Map<Station, Integer> stationToIndex = new HashMap<>();
	protected Map<Stop, Integer> stopToIndex = new HashMap<>();

	public MapModel convert(XmlModel draftModel)
	{
		List<XmlStation> xmlStations = draftModel.getStations();
		List<XmlLine> xmlLines = draftModel.getLines();

		Map<XmlLine, Line> draftToLine = new HashMap<>();
		Map<String, Station> nameToStation = new HashMap<>();

		for (XmlStation xmlStation : xmlStations) {
			Station station = new Station(0, xmlStation.getName(),
					xmlStation.getLocation(), new ArrayList<Stop>());
			stationsList.add(station);
			nameToStation.put(station.getName(), station);
		}

		int id = 0;
		for (XmlLine xmlLine : xmlLines) {
			String name = xmlLine.getName();
			String color = xmlLine.getColor();

			Line line = new Line(id++, name, color, xmlLine.isCircular(), null);
			linesList.add(line);
			draftToLine.put(xmlLine, line);
		}

		for (XmlLine xmlLine : xmlLines) {
			Line line = draftToLine.get(xmlLine);
			List<Stop> stops = new ArrayList<>();
			line.setStops(stops);

			for (XmlStation xmlStop : xmlLine.getStops()) {
				String stopName = xmlStop.getName();

				Station station = nameToStation.get(stopName);

				Stop stop = new Stop(station, line);
				stops.add(stop);
				station.getStops().add(stop);
			}
		}

		Collections.sort(stationsList, new Comparator<Station>() {

			@Override
			public int compare(Station o1, Station o2)
			{
				return o1.getName().compareTo(o2.getName());
			}

		});

		for (int i = 0; i < linesList.size(); i++) {
			Line line = linesList.get(i);
			lineToIndex.put(line, i);
		}

		int k = -1;
		for (int i = 0; i < stationsList.size(); i++) {
			Station station = stationsList.get(i);
			stationToIndex.put(station, i);

			List<Stop> stops = station.getStops();
			for (Stop stop : stops) {
				k++;
				stopToIndex.put(stop, k);
				stopsList.add(stop);
			}
		}

		ModelData data = new ModelData(linesList, stationsList);

		MapModel model = new MapModel(data);

		List<XmlView> xmlViews = draftModel.getXmlViews();
		for (XmlView xmlView : xmlViews) {
			LineNetworkBuilder builder = new LineNetworkBuilder(
					model.getData());
			LineNetwork lineNetwork = builder.getGraph();
			ViewConfig viewConfig = ModelUtil.viewConfig(lineNetwork);
			model.getViews().add(
					new MapView(xmlView.getName(), lineNetwork, viewConfig));

			Map<String, XmlStation> nameToViewStation = new HashMap<>();
			for (XmlStation station : xmlView.getStations()) {
				nameToViewStation.put(station.getName(), station);
			}

			for (Node node : lineNetwork.getNodes()) {
				XmlStation station = nameToViewStation
						.get(node.station.getName());
				if (station != null) {
					node.location = station.getLocation();
				}
			}

			for (Edge edge : lineNetwork.edges) {
				LineNetworkUtil.calculateNeighborLocations(edge);
			}
		}

		return model;
	}

}
