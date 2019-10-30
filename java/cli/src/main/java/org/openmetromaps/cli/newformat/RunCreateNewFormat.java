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

package org.openmetromaps.cli.newformat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.cli.common.CommonOptions;
import org.openmetromaps.cli.common.RenderingConfig;
import org.openmetromaps.maps.CoordinateConversionType;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.newformat.NewFormatWriter;

import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;
import de.topobyte.utilities.apache.commons.cli.parsing.ArgumentParseException;

public class RunCreateNewFormat
{

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT = "output";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_INPUT, true, true, "file", "an OpenMetroMaps model file");
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "an output file in the new format");
			// @formatter:on
			CommonOptions.addRenderingOptions(options);
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

		RenderingConfig renderingConfig = null;
		try {
			renderingConfig = CommonOptions.parseRenderingOptions(line);
		} catch (ArgumentParseException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		InputStream input = Files.newInputStream(pathInput);

		XmlModel xmlModel = DesktopXmlModelReader.read(input);

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);
		ModelUtil.ensureView(model, CoordinateConversionType.WGS84);

		execute(model, renderingConfig, pathOutput);
	}

	private static void execute(MapModel model, RenderingConfig renderingConfig,
			Path pathOutput) throws IOException, ParserConfigurationException
	{
		NewFormatWriter writer = new NewFormatWriter();
		writer.setStationMode(renderingConfig.getStationMode());
		writer.setSegmentMode(renderingConfig.getSegmentMode());

		OutputStream os = Files.newOutputStream(pathOutput);
		writer.write(os, model.getData(), model.getViews());
		os.close();
	}

}
