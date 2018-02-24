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

package org.openmetromaps.cli.osm;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.model.osm.DraftModel;
import org.openmetromaps.model.osm.FileModelBuilder;
import org.openmetromaps.model.osm.Fix;
import org.openmetromaps.model.osm.filter.RouteFilter;
import org.openmetromaps.model.osm.filter.RouteTypeFilter;
import org.openmetromaps.model.osm.inspector.ModelInspector;

import de.topobyte.osm4j.utils.OsmFile;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunModelInspector
{

	private static final String OPTION_INPUT = "input";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			OsmOptions.addInputOptions(options);
			// @formatter:off
			OptionHelper.addL(options, OPTION_INPUT, true, true, "file", "a source OSM data file");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws Exception
	{
		CommandLine line = arguments.getLine();

		OsmOptions.Input input = null;
		try {
			input = OsmOptions.parseInput(line);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		String argInput = line.getOptionValue(OPTION_INPUT);
		Path pathInput = Paths.get(argInput);
		OsmFile fileInput = new OsmFile(pathInput, input.format);

		System.out.println("Input: " + pathInput);

		RouteFilter routeFilter = new RouteTypeFilter("light_rail", "subway");

		List<String> prefixes = new ArrayList<>();
		prefixes.add("S ");
		prefixes.add("U ");
		prefixes.add("S+U ");
		prefixes.add("U-Bhf ");

		List<String> suffixes = new ArrayList<>();

		List<Fix> fixes = new ArrayList<>();

		FileModelBuilder modelBuilder = new FileModelBuilder(fileInput,
				routeFilter, prefixes, suffixes, fixes);
		modelBuilder.run(true, false);

		DraftModel model = modelBuilder.getModel();
		ModelInspector modelInspector = new ModelInspector(model);
		modelInspector.show();
	}

}
