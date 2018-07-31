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

package org.openmetromaps.maps;

import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkCloner;

import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

public class Cloning
{

	public static MapView cloneMapView(MapView view)
	{
		ViewConfig config = cloneConfig(view.getConfig());
		List<Edges> edges = cloneEdges(view.getEdges());
		LineNetwork lineNetwork = cloneLineNetwork(view.getLineNetwork());

		return new MapView(view.getName(), edges, lineNetwork, config);
	}

	public static LineNetwork cloneLineNetwork(LineNetwork lineNetwork)
	{
		LineNetworkCloner networkCloner = new LineNetworkCloner(lineNetwork);
		return networkCloner.cloneLineNetwork();
	}

	public static List<Edges> cloneEdges(List<Edges> allEdges)
	{
		List<Edges> copy = new ArrayList<>();
		for (Edges edges : allEdges) {
			copy.add(cloneEdges(edges));
		}
		return copy;
	}

	public static Edges cloneEdges(Edges edges)
	{
		if (edges == null) {
			return null;
		}
		Edges copy = new Edges(edges.getLine());
		for (Interval interval : edges.getIntervals()) {
			copy.addInterval(cloneInterval(interval));
		}
		return copy;
	}

	public static Interval cloneInterval(Interval interval)
	{
		if (interval == null) {
			return null;
		}
		return new Interval(interval.getFrom(), interval.getTo());
	}

	public static ViewConfig cloneConfig(ViewConfig config)
	{
		return new ViewConfig(cloneRect(config.getScene()),
				cloneCoordinate(config.getStartPosition()));
	}

	public static Rectangle cloneRect(Rectangle r)
	{
		if (r == null) {
			return null;
		}
		return new Rectangle(r.getX1(), r.getY1(), r.getX2(), r.getY2());
	}

	public static Coordinate cloneCoordinate(Coordinate c)
	{
		if (c == null) {
			return null;
		}
		return new Coordinate(c);
	}

}
