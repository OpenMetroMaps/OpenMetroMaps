// Copyright 2019 Sebastian Kuerten
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

package org.openmetromaps.cli.graphml;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.graphml.GraphConverter;
import org.openmetromaps.graphml.GraphMLReader;
import org.openmetromaps.graphml.GraphWithData;
import org.openmetromaps.maps.CoordinateConversionType;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.xml.XmlModelWriter;

import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunGraphMLImport
{

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT = "output";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_INPUT, true, true, "file", "a source GraphML file");
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "a target model text file");
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

		Path pathInput = Paths.get(argInput);
		Path pathOutput = Paths.get(argOutput);

		System.out.println("Input: " + pathInput);
		System.out.println("Output: " + pathOutput);

		GraphMLReader graphMLReader = new GraphMLReader();
		GraphWithData graphWithData = graphMLReader.read(pathInput);

		GraphConverter converter = new GraphConverter();
		ModelData data = converter.convert(graphWithData);

		MapModel model = new MapModel(data);
		ModelUtil.ensureView(model, CoordinateConversionType.IDENTITY);

		OutputStream os = Files.newOutputStream(pathOutput);

		new XmlModelWriter().write(os, model.getData(), model.getViews());
		os.close();
	}

}
