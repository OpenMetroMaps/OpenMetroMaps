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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openmetromaps.maps.Edges;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.TestData;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class TestShowChange
{

	public static void main(String[] args) throws IOException, ParsingException
	{
		XmlModel xmlModel = TestData.berlinXml();

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		List<Edges> edges = new ArrayList<>();
		for (Line line : model.getData().lines) {
			edges.add(new Edges(line.getName()));
		}

		LineNetworkBuilder builder = new LineNetworkBuilder(model.getData(),
				edges);
		LineNetwork network = builder.getGraph();

		Multimap<Station, Line> stationToLines = HashMultimap.create();
		Util.fillStationToLines(stationToLines, model);

		Context context = new Context(stationToLines, network);

		test(context, network, "U8", "Hermannplatz");
		test(context, network, "S41", "Schöneberg");
		test(context, network, "S41", "Südkreuz");
		test(context, network, "S41", "Hermannstraße");
		test(context, network, "S41", "Neukölln");
		test(context, network, "U7", "Rudow");
		test(context, network, "S8", "Eichwalde");
		test(context, network, "S8", "Zeuthen");
		test(context, network, "U1", "Nollendorfplatz");
		test(context, network, "U1", "Wittenbergplatz");
	}

	private static void test(Context context, LineNetwork network,
			String nameLine, String nameStation)
	{
		System.out.println(String.format("%s %s", nameLine, nameStation));

		NetworkLine line = findLine(network, nameLine);
		Node node = findStation(network, nameStation);

		List<Line> lines = Util.determineInterestingLines(context, line, node);
		Collections.sort(lines, new Comparator<Line>() {

			@Override
			public int compare(Line o1, Line o2)
			{
				return o1.getName().compareTo(o2.getName());
			}

		});
		System.out.println(
				"found: " + Collections2.transform(lines, e -> e.getName()));
	}

	private static NetworkLine findLine(LineNetwork network, String name)
	{
		for (NetworkLine line : network.lines) {
			if (line.line.getName().equals(name)) {
				return line;
			}
		}
		return null;
	}

	private static Node findStation(LineNetwork network, String name)
	{
		for (Node node : network.nodes) {
			if (node.station.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

}
