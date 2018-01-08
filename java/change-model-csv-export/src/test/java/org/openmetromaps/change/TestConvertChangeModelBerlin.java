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

package org.openmetromaps.change;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.gtfs4j.csv.GtfsZip;
import org.openmetromaps.gtfs4j.model.Stop;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.TestData;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.misc.NameChanger;
import org.openmetromaps.rawchange.RawChangeModel;
import org.openmetromaps.rawchange.xml.DesktopXmlChangeReader;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class TestConvertChangeModelBerlin
{

	public static void main(String[] args)
			throws ParserConfigurationException, IOException, ParsingException
	{
		XmlModel xmlModel = TestData.berlinXml();
		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel mapModel = modelConverter.convert(xmlModel);

		LineNetworkBuilder builder = new LineNetworkBuilder(mapModel.getData(),
				MapModelUtil.allEdges(mapModel));
		LineNetwork lineNetwork = builder.getGraph();

		InputStream input = TestConvertChangeModelBerlin.class.getClassLoader()
				.getResourceAsStream("berlin-changes.xml");
		RawChangeModel rawModel = DesktopXmlChangeReader.read(input);
		ChangeModel model = ChangeModels.derive(mapModel.getData(), rawModel);

		Path pathGtfs = Paths.get("/tmp/gtfs/filtered.zip");

		List<String> prefixes = new ArrayList<>();
		prefixes.add("S ");
		prefixes.add("U ");
		prefixes.add("S+U ");

		List<String> suffixes = new ArrayList<>();
		suffixes.add(" Bhf (Berlin)");
		suffixes.add(" (Berlin)");
		suffixes.add(" Bhf");
		for (int i = 1; i <= 9; i++) {
			suffixes.add(String.format(" (Berlin) [U%d]", i));
		}

		NameChanger nameChanger = new NameChanger(prefixes, suffixes);

		GtfsZip gtfs = new GtfsZip(pathGtfs);
		List<Stop> stops = gtfs.readStops();
		for (Stop stop : stops) {
			String name = nameChanger.applyNameFixes(stop.getName());
			System.out.println(String.format("%s: %s", stop.getId(), name));
		}
		gtfs.close();

		ChangeModelToCsvExporter exporter = new ChangeModelToCsvExporter(
				mapModel, lineNetwork, model);
		exporter.print();
	}

}
