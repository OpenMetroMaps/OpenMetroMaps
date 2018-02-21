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

package org.openmetromaps.cli.gtfs;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.gtfs.DraftModel;
import org.openmetromaps.gtfs.GtfsImporter;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.xml.XmlModelWriter;
import org.openmetromaps.misc.NameChanger;
import org.openmetromaps.model.gtfs.DraftModelConverter;

import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunGtfsImport
{

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT = "output";
	private static final String OPTION_FIX_BOMS = "fix-boms";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_INPUT, true, true, "file", "a source GTFS zip file");
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "a target model text file");
			OptionHelper.addL(options, OPTION_FIX_BOMS, false, false, "whether to check for BOMs in zipped files");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws Exception
	{
		CommandLine line = arguments.getLine();

		String argInput = line.getOptionValue(OPTION_INPUT);
		String argOutput = line.getOptionValue(OPTION_OUTPUT);
		boolean fixBoms = line.hasOption(OPTION_FIX_BOMS);

		Path pathInput = Paths.get(argInput);
		Path pathOutput = Paths.get(argOutput);

		System.out.println("Input: " + pathInput);
		System.out.println("Output: " + pathOutput);

		List<String> prefixes = new ArrayList<>();
		prefixes.add("S ");
		prefixes.add("U ");
		prefixes.add("S+U ");
		prefixes.add("U-Bhf ");

		List<String> suffixes = new ArrayList<>();

		NameChanger nameChanger = new NameChanger(prefixes, suffixes);

		GtfsImporter importer = new GtfsImporter(pathInput, nameChanger,
				fixBoms);
		importer.execute();

		OutputStream os = Files.newOutputStream(pathOutput);

		DraftModel draft = importer.getModel();
		ModelData data = new DraftModelConverter().convert(draft);

		new XmlModelWriter().write(os, data, new ArrayList<>());
		os.close();
	}

}
