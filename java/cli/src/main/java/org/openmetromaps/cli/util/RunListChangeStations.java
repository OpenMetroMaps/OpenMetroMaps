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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.maps.Edges;
import org.openmetromaps.maps.MapModel;
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
import org.openmetromaps.misc.Context;
import org.openmetromaps.misc.Util;

import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
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
		List<Edges> edges = new ArrayList<>();
		for (Line line : model.getData().lines) {
			edges.add(new Edges(line.getName()));
		}

		LineNetworkBuilder builder = new LineNetworkBuilder(model.getData(),
				edges);
		LineNetwork lineNetwork = builder.getGraph();

		Multimap<Station, Line> stationToLines = HashMultimap.create();
		Util.fillStationToLines(stationToLines, model);

		Context context = new Context(stationToLines, lineNetwork);

		List<NetworkLine> lines = lineNetwork.getLines();
		for (NetworkLine line : lines) {
			List<Node> nodes = LineNetworkUtil.getNodes(lineNetwork, line.line);
			for (Node node : nodes) {
				List<Line> changeLines = Util.determineInterestingLines(context,
						line, node);
				if (changeLines.isEmpty()) {
					continue;
				}

				Collections.sort(changeLines, new Comparator<Line>() {

					@Override
					public int compare(Line o1, Line o2)
					{
						return o1.getName().compareTo(o2.getName());
					}

				});

				Collection<String> names = Collections2.transform(changeLines,
						e -> e.getName());
				String otherNames = Joiner.on(", ").join(names);

				System.out.println(
						String.format("%s, %s: %s", line.line.getName(),
								node.station.getName(), otherNames));
			}
		}

	}

}
