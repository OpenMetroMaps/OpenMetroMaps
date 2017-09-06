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

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFile;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class FilterRegion
{

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT = "output";
	private static final String OPTION_BOUNDARY = "boundary";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_INPUT, true, true, "file", "a source OSM data file");
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "a target OSM data file");
			OptionHelper.addL(options, OPTION_BOUNDARY, true, true, "file", "a boundary geometry file");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws IOException, ParseException
	{
		CommandLine line = arguments.getLine();

		String argInput = line.getOptionValue(OPTION_INPUT);
		String argOutput = line.getOptionValue(OPTION_OUTPUT);
		String argBoundary = line.getOptionValue(OPTION_BOUNDARY);
		Path pathInput = Paths.get(argInput);
		Path pathOutput = Paths.get(argOutput);
		Path pathBoundary = Paths.get(argBoundary);
		OsmFile fileInput = new OsmFile(pathInput, FileFormat.PBF);
		OsmFile fileOutput = new OsmFile(pathOutput, FileFormat.TBO);

		System.out.println("Input: " + pathInput);
		System.out.println("Output: " + pathOutput);
		System.out.println("Boundary: " + pathBoundary);

		Geometry region = new WKTReader()
				.read(new FileReader(pathBoundary.toFile()));

		org.openmetromaps.osm.FilterRegion filter = new org.openmetromaps.osm.FilterRegion(
				fileInput, fileOutput, region, false);
		filter.execute();
	}

}
