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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.config.PbfConfig;
import de.topobyte.osm4j.utils.config.PbfOptions;
import de.topobyte.osm4j.utils.config.TboConfig;
import de.topobyte.osm4j.utils.config.TboOptions;
import de.topobyte.system.utils.SystemProperties;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class OsmOptions
{

	public static class Input
	{
		public FileFormat format;
	}

	public static class Output
	{
		public FileFormat format;
		public TboConfig tboConfig;
		public PbfConfig pbfConfig;
	}

	private static final String OPTION_INPUT_FORMAT = "input-format";
	private static final String OPTION_OUTPUT_FORMAT = "output-format";

	public static void addInputOutputOptions(Options options)
	{
		addInputOptions(options);
		addOutputOptions(options);
	}

	public static void addInputOptions(Options options)
	{
		OptionHelper.addL(options, OPTION_INPUT_FORMAT, true, true,
				"the file format of the input");
	}

	public static void addOutputOptions(Options options)
	{
		OptionHelper.addL(options, OPTION_OUTPUT_FORMAT, true, true,
				"the file format of the output");
		PbfOptions.add(options);
		TboOptions.add(options);
	}

	public static Input parseInput(CommandLine line)
	{
		Input input = new Input();
		String inputFormatName = line.getOptionValue(OPTION_INPUT_FORMAT);
		input.format = FileFormat.parseFileFormat(inputFormatName);
		if (input.format == null) {
			StringBuilder message = new StringBuilder();
			message.append(
					"invalid argument for option " + OPTION_INPUT_FORMAT);
			message.append(System.getProperty(SystemProperties.LINE_SEPARATOR));
			message.append("please specify one of: "
					+ FileFormat.getHumanReadableListOfSupportedFormats());
			throw new RuntimeException(message.toString());
		}
		return input;
	}

	public static Output parseOutput(CommandLine line)
	{
		Output output = new Output();
		String inputFormatName = line.getOptionValue(OPTION_INPUT_FORMAT);
		output.format = FileFormat.parseFileFormat(inputFormatName);
		if (output.format == null) {
			StringBuilder message = new StringBuilder();
			message.append(
					"invalid argument for option " + OPTION_OUTPUT_FORMAT);
			message.append(System.getProperty(SystemProperties.LINE_SEPARATOR));
			message.append("please specify one of: "
					+ FileFormat.getHumanReadableListOfSupportedFormats());
			throw new RuntimeException(message.toString());
		}
		output.tboConfig = TboOptions.parse(line);
		output.pbfConfig = PbfOptions.parse(line);
		return output;
	}

}
