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

package org.openmetromaps.maps.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.TestData;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class TestNodesInBetween
{

	@Test
	public void test() throws ParsingException
	{
		XmlModel xmlModel = TestData.berlinXml();
		MapModel model = new XmlModelConverter().convert(xmlModel);

		Map<String, Station> nameToStation = new HashMap<>();
		List<Station> stations = model.getData().stations;
		for (Station station : stations) {
			nameToStation.put(station.getName(), station);
		}

		Station hermannplatz = nameToStation.get("Hermannplatz");
		Station grenzallee = nameToStation.get("Grenzallee");

		LineNetworkBuilder builder = new LineNetworkBuilder(model.getData(),
				MapModelUtil.allEdges(model));
		LineNetwork network = builder.getGraph();

		Node node1 = network.getStationToNode().get(hermannplatz);
		Node node2 = network.getStationToNode().get(grenzallee);

		NodeConnectionResult connection = LineNetworkUtil.findConnection(node1,
				node2);

		Line line = connection.getCommonLines().iterator().next();

		LineConnectionResult lineConnection = LineNetworkUtil
				.findConnection(line, node1, node2);

		Assert.assertTrue(connection.isConnected());
		int idxNode1 = lineConnection.getIdxNode1();
		int idxNode2 = lineConnection.getIdxNode2();

		NodesInBetweenResult nodesBetween = LineNetworkUtil
				.getNodesBetween(network, line, idxNode1, idxNode2);

		Assert.assertEquals("Hermannplatz",
				nodesBetween.getStart().station.getName());
		Assert.assertEquals("Grenzallee",
				nodesBetween.getEnd().station.getName());

		Assert.assertEquals(3, nodesBetween.getNodes().size());

		Assert.assertEquals("Rathaus Neukölln",
				nodesBetween.getNodes().get(0).station.getName());
		Assert.assertEquals("Karl-Marx-Straße",
				nodesBetween.getNodes().get(1).station.getName());
		Assert.assertEquals("Neukölln",
				nodesBetween.getNodes().get(2).station.getName());
	}

}
