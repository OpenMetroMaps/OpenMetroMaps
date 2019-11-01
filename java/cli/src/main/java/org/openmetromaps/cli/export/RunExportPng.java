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

package org.openmetromaps.cli.export;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.cli.common.CommonOptions;
import org.openmetromaps.cli.common.RenderingConfig;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.image.ImageUtil;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;
import de.topobyte.utilities.apache.commons.cli.parsing.ArgumentHelper;
import de.topobyte.utilities.apache.commons.cli.parsing.ArgumentParseException;
import de.topobyte.utilities.apache.commons.cli.parsing.DoubleOption;
import de.topobyte.viewports.geometry.Rectangle;

public class RunExportPng
{

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT = "output";
	private static final String OPTION_ZOOM = "zoom";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_INPUT, true, true, "file", "an OpenMetroMaps model file");
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "an output image file");
			OptionHelper.addL(options, OPTION_ZOOM, true, false, "double", "zoom level to use");
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

		DoubleOption argZoom = ArgumentHelper.getDouble(line, OPTION_ZOOM);
		double zoom = 1;
		if (argZoom.hasValue()) {
			zoom = argZoom.getValue();
		}

		System.out.println("Input: " + pathInput);
		System.out.println("Output: " + pathOutput);
		System.out.println("Zoom: " + zoom);

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

		execute(model, renderingConfig, pathOutput, zoom);
	}

	private static void execute(MapModel model, RenderingConfig renderingConfig,
			Path pathOutput, double zoom) throws IOException
	{
		MapView view = model.getViews().get(0);
		Rectangle scene = view.getConfig().getScene();

		double width = scene.getWidth();
		double height = scene.getHeight();

		double x = 0;
		double y = 0;

		int imageWidth = (int) Math.ceil(width * zoom);
		int imageHeight = (int) Math.ceil(height * zoom);

		ImageUtil.createPng(model, pathOutput, imageWidth, imageHeight, x, y,
				zoom, renderingConfig.getStationMode(),
				renderingConfig.getSegmentMode());
	}

}
