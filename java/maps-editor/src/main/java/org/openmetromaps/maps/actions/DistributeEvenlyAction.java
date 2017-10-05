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

package org.openmetromaps.maps.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.openmetromaps.maps.MapEditor;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import de.topobyte.adt.geo.Coordinate;
import de.topobyte.swing.util.EmptyIcon;

public class DistributeEvenlyAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory
			.getLogger(DistributeEvenlyAction.class);

	private static final long serialVersionUID = 1L;

	public DistributeEvenlyAction(MapEditor mapEditor)
	{
		super(mapEditor, "Distribute Evenly",
				"Distribute the stations between two selected ones evenly");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Set<Node> nodes = mapEditor.getMapViewStatus().getSelectedNodes();

		if (nodes.size() != 2) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Please select exactly two stations.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Iterator<Node> iterator = nodes.iterator();
		Node node1 = iterator.next();
		Node node2 = iterator.next();
		logger.debug(String.format("Trying to connect: '%s' and '%s'",
				node1.station.getName(), node2.station.getName()));

		Set<Line> node1Lines = lines(node1);
		Set<Line> node2Lines = lines(node2);

		SetView<Line> commonLines = Sets.intersection(node1Lines, node2Lines);
		boolean connected = !commonLines.isEmpty();

		if (!connected) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Please select two stations that are connected with a line.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Line line = commonLines.iterator().next();

		logger.debug("Common line: " + line.getName());

		int idxNode1 = -1;
		int idxNode2 = -1;

		List<Stop> stops = line.getStops();
		for (int i = 0; i < stops.size(); i++) {
			Station station = stops.get(i).getStation();
			if (station == node1.station) {
				idxNode1 = i;
			} else if (station == node2.station) {
				idxNode2 = i;
			}
		}

		if (idxNode1 < 0 || idxNode2 < 0) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Unable to determine connection between stations.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Node start, end;
		int min, max;
		if (idxNode1 < idxNode2) {
			min = idxNode1;
			max = idxNode2;
			start = node1;
			end = node2;
		} else {
			min = idxNode2;
			max = idxNode1;
			start = node2;
			end = node1;
		}

		int num = max - min;

		logger.debug(String.format("Stops min: %d, max: %d, num: %d", min, max,
				num));

		Coordinate c1 = start.location;
		Coordinate c2 = end.location;

		double diffX = c2.getLongitude() - c1.getLongitude();
		double diffY = c2.getLatitude() - c1.getLatitude();

		double dx = diffX / num;
		double dy = diffY / num;

		LineNetwork lineNetwork = mapEditor.getMap().getLineNetwork();

		for (int i = 1; i <= num; i++) {
			Stop stop = stops.get(min + i);
			Node node = getNode(lineNetwork, stop);
			double x = c1.lon + dx * i;
			double y = c1.lat + dy * i;
			node.location = new Coordinate(x, y);
		}
	}

	private Set<Line> lines(Node node)
	{
		Set<Line> lines = new HashSet<>();
		for (Stop stop : node.station.getStops()) {
			lines.add(stop.getLine());
		}
		return lines;
	}

	private Node getNode(LineNetwork lineNetwork, Stop stop)
	{
		// TODO: this is pretty inefficient
		for (Node node : lineNetwork.getNodes()) {
			for (Stop s : node.station.getStops()) {
				if (s == stop) {
					return node;
				}
			}
		}
		return null;
	}

}
