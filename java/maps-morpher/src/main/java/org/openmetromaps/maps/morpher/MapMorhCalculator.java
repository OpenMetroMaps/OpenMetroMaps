// Copyright 2018 Sebastian Kuerten
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

package org.openmetromaps.maps.morpher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.Edges;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ViewConfig;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

public class MapMorhCalculator
{

	private MapModel model1;
	private MapModel model2;

	private MapView view1;
	private MapView view2;
	private LineNetwork network1;
	private LineNetwork network2;

	private Map<String, Station> nameToStation2;

	public MapMorhCalculator(MapModel model1, MapModel model2)
	{
		this.model1 = model1;
		this.model2 = model2;

		view1 = model1.getViews().get(0);
		view2 = model2.getViews().get(0);
		network1 = view1.getLineNetwork();
		network2 = view2.getLineNetwork();

		nameToStation2 = new HashMap<>();
		for (Station station : network2.getStationToNode().keySet()) {
			nameToStation2.put(station.getName(), station);
		}
	}

	public MapModel deriveModel(double relative)
	{
		ModelData data = model1.getData();

		Rectangle scene1 = view1.getConfig().getScene();
		Rectangle scene2 = view2.getConfig().getScene();
		System.out.println(String.format("%.1f x %.1f vs. %.1f x %.1f",
				scene1.getWidth(), scene1.getHeight(), scene2.getWidth(),
				scene2.getHeight()));

		double width = Math.max(scene1.getWidth(), scene2.getWidth());
		double height = Math.max(scene1.getHeight(), scene2.getHeight());

		double offX1 = (width - scene1.getWidth()) / 2;
		double offY1 = (height - scene1.getHeight()) / 2;

		double offX2 = (width - scene2.getWidth()) / 2;
		double offY2 = (height - scene2.getHeight()) / 2;

		System.out.println(String.format("offsets: %.1f,%.1f and %.1f,%.1f",
				offX1, offY1, offX2, offY2));

		ViewConfig config = new ViewConfig(new Rectangle(0, 0, width, height),
				new Coordinate(width / 2, height / 2));
		List<Edges> edges = view1.getEdges();

		MapModel model = new MapModel(data);

		LineNetworkBuilder builder = new LineNetworkBuilder(data, edges);
		LineNetwork network = builder.getGraph();

		double f1 = 1 - relative;
		double f2 = relative;

		for (Node node : network.getNodes()) {
			String stationName = node.station.getName();
			Station station2 = nameToStation2.get(stationName);

			Node node1 = network1.getStationToNode().get(node.station);
			Node node2 = network2.getStationToNode().get(station2);

			Point loc1 = node1.location;
			Point loc2 = node2.location;

			double x = f1 * (offX1 + loc1.x) + f2 * (offX2 + loc2.x);
			double y = f1 * (offY1 + loc1.y) + f2 * (offY2 + loc2.y);
			node.location = new Point(x, y);
		}

		LineNetworkUtil.calculateAllNeighborLocations(network);

		MapView view = new MapView("morphed", edges, network, config);

		List<MapView> views = new ArrayList<>();
		views.add(view);
		model.setViews(views);

		return model;
	}

	public MapModel getModel1()
	{
		return model1;
	}

	public MapModel getModel2()
	{
		return model2;
	}

}
