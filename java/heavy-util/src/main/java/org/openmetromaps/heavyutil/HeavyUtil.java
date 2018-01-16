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

package org.openmetromaps.heavyutil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class HeavyUtil
{

	final static Logger logger = LoggerFactory.getLogger(HeavyUtil.class);

	public static List<Line> determineInterestingLines(
			Multimap<Station, Line> stationToLines, NetworkLine line, Node node)
	{
		Station station = node.station;
		logger.debug("station: " + station.getName());

		List<Line> lines = new ArrayList<>(stationToLines.get(station));
		lines.remove(line.line);

		if (logger.isDebugEnabled()) {
			logger.debug(
					"old: " + Collections2.transform(lines, e -> e.getName()));
		}

		List<Edge> edges = node.edges;
		List<Edge> relevant = new ArrayList<>();
		for (Edge edge : edges) {
			boolean useEdge = false;
			for (NetworkLine netLine : edge.lines) {
				if (netLine.line == line.line) {
					useEdge = true;
				}
			}
			if (useEdge) {
				relevant.add(edge);
			}
		}

		logger.debug("relevant edges: " + relevant.size());
		if (relevant.size() == 2) {
			List<List<Line>> results = new ArrayList<>();
			for (Edge edge : relevant) {
				if (edge.n1 != node) {
					results.add(collect(edge.n1));
				}
				if (edge.n2 != node) {
					results.add(collect(edge.n2));
				}
			}
			Set<Line> lines1 = new HashSet<>(results.get(0));
			Set<Line> lines2 = new HashSet<>(results.get(1));
			SetView<Line> common = Sets.intersection(lines1, lines2);
			lines.removeAll(common);
		}

		if (logger.isDebugEnabled()) {
			logger.debug(
					"new: " + Collections2.transform(lines, e -> e.getName()));
		}

		return lines;
	}

	private static List<Line> collect(Node node)
	{
		List<Line> result = new ArrayList<>();
		List<Stop> stops = node.station.getStops();
		for (Stop stop : stops) {
			logger.debug("remove: " + stop.getLine().getName());
			result.add(stop.getLine());
		}
		return result;
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
