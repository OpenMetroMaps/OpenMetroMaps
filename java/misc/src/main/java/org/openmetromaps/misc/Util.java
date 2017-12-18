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

package org.openmetromaps.misc;

import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;

public class Util
{

	final static Logger logger = LoggerFactory.getLogger(Util.class);

	public static List<Line> determineInterestingLines(Context context,
			Line line, Station station)
	{
		logger.debug("station: " + station.getName());

		List<Line> lines = new ArrayList<>(
				context.getStationToLines().get(station));
		lines.remove(line);

		if (logger.isDebugEnabled()) {
			logger.debug(
					"old: " + Collections2.transform(lines, e -> e.getName()));
		}

		LineNetwork network = context.getLineNetwork();
		Node node = network.getStationToNode().get(station);
		List<Edge> edges = node.edges;
		for (Edge edge : edges) {
			boolean useEdge = false;
			for (NetworkLine netLine : edge.lines) {
				if (netLine.line == line) {
					useEdge = true;
				}
			}
			if (!useEdge) {
				continue;
			}
			if (edge.n1 != node) {
				remove(lines, edge.n1);
			}
			if (edge.n2 != node) {
				remove(lines, edge.n2);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(
					"new: " + Collections2.transform(lines, e -> e.getName()));
		}

		return lines;
	}

	private static void remove(List<Line> lines, Node node)
	{
		List<Stop> stops = node.station.getStops();
		for (Stop stop : stops) {
			logger.debug("remove: " + stop.getLine().getName());
			lines.remove(stop.getLine());
		}
	}

	public static void fillStationToLines(
			Multimap<Station, Line> stationToLines, MapModel model)
	{
		for (Line line : model.getData().lines) {
			List<Stop> stops = line.getStops();
			for (Stop stop : stops) {
				stationToLines.put(stop.getStation(), line);
			}
		}
	}

}
