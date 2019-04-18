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
import java.util.Collections;
import java.util.List;

import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.maps.painting.core.ColorCode;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.BBoxHelper;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.viewports.geometry.Rectangle;

public class ModelUtil
{

	public static ColorCode getColor(Line line)
	{
		String sColor = line.getColor();
		if (sColor.isEmpty()) {
			return new ColorCode(0xffbf3f);
		}
		return new ColorCode(Integer.decode(sColor));
	}

	public static DataConfig dataConfig(ModelData model)
	{
		List<de.topobyte.adt.geo.Coordinate> coords = new ArrayList<>();
		for (Station station : model.stations) {
			coords.add(coord(station.getLocation()));
			for (Stop stop : station.getStops()) {
				if (stop.getLocation() != null) {
					coords.add(coord(stop.getLocation()));
				}
			}
		}
		BBox bbox = BBoxHelper.minimumBoundingBox(coords);

		Collections.sort(coords, new CoordinateComparatorLongitude());
		double medianLon = coords.get(coords.size() / 2).getLongitude();

		Collections.sort(coords, new CoordinateComparatorLatitude());
		double medianLat = coords.get(coords.size() / 2).getLatitude();

		Coordinate startPosition = new Coordinate(medianLon, medianLat);

		return new DataConfig(bbox(bbox), startPosition);
	}

	private static org.openmetromaps.maps.model.BBox bbox(BBox bbox)
	{
		return new org.openmetromaps.maps.model.BBox(bbox.getLon1(),
				bbox.getLat1(), bbox.getLon2(), bbox.getLat2());
	}

	private static de.topobyte.adt.geo.Coordinate coord(Coordinate location)
	{
		return new de.topobyte.adt.geo.Coordinate(location.getLongitude(),
				location.getLatitude());
	}

	public static ViewConfig viewConfig(LineNetwork lineNetwork)
	{
		List<Point> points = new ArrayList<>();
		for (Node node : lineNetwork.getNodes()) {
			points.add(node.location);
		}
		Point min = Points.minimum(points);
		Point max = Points.maximum(points);

		Collections.sort(points, new PointComparatorX());
		double medianX = points.get(points.size() / 2).getX();

		Collections.sort(points, new PointComparatorY());
		double medianY = points.get(points.size() / 2).getY();

		Rectangle scene = new Rectangle(min.x, min.y, max.x, max.y);

		return new ViewConfig(scene,
				new de.topobyte.viewports.geometry.Coordinate(medianX,
						medianY));
	}

	public static ViewConfig viewConfig(LineNetwork lineNetwork, double width,
			double height)
	{
		List<Point> points = new ArrayList<>();
		for (Node node : lineNetwork.getNodes()) {
			points.add(node.location);
		}

		Collections.sort(points, new PointComparatorX());
		double medianX = points.get(points.size() / 2).getX();

		Collections.sort(points, new PointComparatorY());
		double medianY = points.get(points.size() / 2).getY();

		Rectangle scene = new Rectangle(0, 0, width, height);

		return new ViewConfig(scene,
				new de.topobyte.viewports.geometry.Coordinate(medianX,
						medianY));
	}

	public static void ensureView(MapModel model,
			CoordinateConversionType conversionType)
	{
		if (!model.getViews().isEmpty()) {
			return;
		}

		List<Edges> edges = MapModelUtil.allEdges(model);

		LineNetworkBuilder builder = new LineNetworkBuilder(model.getData(),
				edges);
		LineNetwork lineNetwork = builder.getGraph();
		List<Node> nodes = lineNetwork.getNodes();

		for (Node node : nodes) {
			Coordinate coord = node.station.getLocation();
			node.location = new Point(coord.getLongitude(),
					coord.getLatitude());
		}
		LineNetworkUtil.calculateAllNeighborLocations(lineNetwork);

		ViewConfig viewConfig = ModelUtil.viewConfig(lineNetwork);
		MapView view = new MapView("Test", edges, lineNetwork, viewConfig);
		CoordinateConversion.convertView(view, conversionType);
		model.getViews().add(view);
	}

	public static MapView cloneMapView(MapView view)
	{
		return Cloning.cloneMapView(view);
	}

	public static MapView getScaledInstance(MapView view, double scale)
	{
		MapView copy = cloneMapView(view);
		scale(copy, scale);
		return copy;
	}

	public static void scale(MapView view, double factor)
	{
		LineNetwork network = view.getLineNetwork();
		for (Node node : network.getNodes()) {
			Point location = node.location;
			scale(location, factor);
		}

		for (Edge edge : network.getEdges()) {
			scale(edge.prev, factor);
			scale(edge.next, factor);
		}

		scale(view.getConfig().getStartPosition(), factor);
		scale(view.getConfig().getScene(), factor);
	}

	private static void scale(Point location, double factor)
	{
		if (location == null) {
			return;
		}
		location.x = location.x * factor;
		location.y = location.y * factor;
	}

	private static void scale(
			de.topobyte.viewports.geometry.Coordinate location, double factor)
	{
		if (location == null) {
			return;
		}
		location.setX(location.getX() * factor);
		location.setY(location.getY() * factor);
	}

	private static void scale(Rectangle scene, double factor)
	{
		scene.setX1(scene.getX1() * factor);
		scene.setX2(scene.getX2() * factor);
		scene.setY1(scene.getY1() * factor);
		scene.setY2(scene.getY2() * factor);
	}

}
