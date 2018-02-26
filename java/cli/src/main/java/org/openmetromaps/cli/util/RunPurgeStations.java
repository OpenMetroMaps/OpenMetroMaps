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

package org.openmetromaps.cli.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.maps.xml.XmlModelWriter;

import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class RunPurgeStations
{

	private static final String OPTION_FILE = "file";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_FILE, true, true, "file", "an OpenMetroMaps model file");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws Exception
	{
		CommandLine line = arguments.getLine();

		String argFile = line.getOptionValue(OPTION_FILE);
		Path path = Paths.get(argFile);

		System.out.println("Input: " + path);

		execute(path);
	}

	private static void execute(Path path) throws IOException, ParsingException,
			ParserConfigurationException, TransformerException
	{
		InputStream input = Files.newInputStream(path);

		XmlModel xmlModel = DesktopXmlModelReader.read(input);

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		ModelData data = model.getData();
		List<Line> lines = data.lines;
		List<Station> stations = data.stations;
		System.out.println(
				String.format("This file contains %d lines and %d stations",
						lines.size(), stations.size()));

		Set<String> usedStationNames = new HashSet<>();
		for (Line line : lines) {
			for (Stop stop : line.getStops()) {
				Station station = stop.getStation();
				usedStationNames.add(station.getName());
			}
		}

		data.stations = new ArrayList<>();
		for (Station station : stations) {
			if (usedStationNames.contains(station.getName())) {
				data.stations.add(station);
			}
		}

		List<MapView> views = model.getViews();
		for (MapView view : views) {
			LineNetwork network = view.getLineNetwork();
			List<Node> nodes = view.getLineNetwork().nodes;
			network.nodes = new ArrayList<>();
			for (Node node : nodes) {
				if (usedStationNames.contains(node.station.getName())) {
					network.nodes.add(node);
				}
			}
		}

		OutputStream output = Files.newOutputStream(path);
		new XmlModelWriter().write(output, data, views);
		output.close();
	}

}
