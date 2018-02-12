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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.heavyutil.HeavyUtil;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunListChangeStations
{

	private static final String OPTION_INPUT = "input";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_INPUT, true, true, "file", "an OpenMetroMaps model file");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws Exception
	{
		CommandLine line = arguments.getLine();

		String argInput = line.getOptionValue(OPTION_INPUT);
		Path pathInput = Paths.get(argInput);

		System.out.println("Input: " + pathInput);

		InputStream input = Files.newInputStream(pathInput);

		XmlModel xmlModel = DesktopXmlModelReader.read(input);

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		execute(model);
	}

	private static void execute(MapModel model)
	{
		LineNetworkBuilder builder = new LineNetworkBuilder(model.getData(),
				MapModelUtil.allEdges(model));
		LineNetwork lineNetwork = builder.getGraph();

		Multimap<Station, Line> stationToLines = HashMultimap.create();
		HeavyUtil.fillStationToLines(stationToLines, model);

		Set<Node> changeNodes = new HashSet<>();

		List<NetworkLine> lines = lineNetwork.getLines();
		for (NetworkLine line : lines) {
			List<Node> nodes = LineNetworkUtil.getNodes(lineNetwork, line.line);
			for (Node node : nodes) {
				List<Line> changeLines = HeavyUtil
						.determineInterestingLines(stationToLines, line, node);
				if (changeLines.isEmpty()) {
					continue;
				}

				changeNodes.add(node);
			}
		}

		List<Station> changeStations = new ArrayList<>();

		for (Node node : changeNodes) {
			changeStations.add(node.station);
		}

		MapModelUtil.sortStationsByName(changeStations);

		for (Station station : changeStations) {
			System.out.println(station.getName());
		}
	}

}
