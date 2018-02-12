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

import java.io.IOException;
import java.util.List;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapModelUtil;
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

		LineNetworkBuilder builder = new LineNetworkBuilder(model.getData(),
				MapModelUtil.allEdges(model));
		LineNetwork network = builder.getGraph();

		Multimap<Station, Line> stationToLines = HashMultimap.create();
		HeavyUtil.fillStationToLines(stationToLines, model);

		test(stationToLines, network, "U8", "Hermannplatz");
		test(stationToLines, network, "S41", "Schöneberg");
		test(stationToLines, network, "S41", "Südkreuz");
		test(stationToLines, network, "S41", "Hermannstraße");
		test(stationToLines, network, "S41", "Neukölln");
		test(stationToLines, network, "U7", "Rudow");
		test(stationToLines, network, "S8", "Eichwalde");
		test(stationToLines, network, "S8", "Zeuthen");
		test(stationToLines, network, "U1", "Nollendorfplatz");
		test(stationToLines, network, "U1", "Wittenbergplatz");
	}

	private static void test(Multimap<Station, Line> stationToLines,
			LineNetwork network, String nameLine, String nameStation)
	{
		System.out.println(String.format("%s %s", nameLine, nameStation));

		NetworkLine line = findLine(network, nameLine);
		Node node = findStation(network, nameStation);

		List<Line> lines = HeavyUtil.determineInterestingLines(stationToLines,
				line, node);
		MapModelUtil.sortLinesByName(lines);

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
