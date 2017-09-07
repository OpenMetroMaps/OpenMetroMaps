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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.osm.FilterRelevantData;

import de.topobyte.osm4j.utils.OsmFile;
import de.topobyte.osm4j.utils.OsmOutputConfig;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunFilterRelevantData
{

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT = "output";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			OsmOptions.addInputOutputOptions(options);
			// @formatter:off
			OptionHelper.addL(options, OPTION_INPUT, true, true, "file", "a source OSM data file");
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "a target OSM data file");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws IOException
	{
		CommandLine line = arguments.getLine();

		OsmOptions.Input input = null;
		OsmOptions.Output output = null;
		try {
			input = OsmOptions.parseInput(line);
			output = OsmOptions.parseOutput(line);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		boolean useMetadata = false;

		String argInput = line.getOptionValue(OPTION_INPUT);
		String argOutput = line.getOptionValue(OPTION_OUTPUT);
		Path pathInput = Paths.get(argInput);
		Path pathOutput = Paths.get(argOutput);
		OsmFile fileInput = new OsmFile(pathInput, input.format);
		OsmFile fileOutput = new OsmFile(pathOutput, output.format);

		OsmOutputConfig outputConfig = new OsmOutputConfig(output.format,
				output.pbfConfig, output.tboConfig, useMetadata);

		System.out.println("Input: " + pathInput);
		System.out.println("Output: " + pathOutput);

		FilterRelevantData filter = new FilterRelevantData(fileInput,
				fileOutput, outputConfig);
		filter.execute();
	}

}
