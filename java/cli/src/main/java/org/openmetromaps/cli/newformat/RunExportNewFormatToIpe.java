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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;
import de.topobyte.xml4jah.dom.DocumentWriter;

public class RunExportNewFormatToIpe
{

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT = "output";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_INPUT, true, true, "file", "an input file in the new format");
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "an IPE ile");
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

		InputStream input = Files.newInputStream(pathInput);

		execute(input, pathOutput);
	}

	private static void execute(InputStream input, Path pathOutput)
			throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document docSource = builder.parse(input);

		// TODO: get size from document
		int width = 1000;
		int height = 905;

		Document doc = builder.newDocument();
		Element eIpe = doc.createElement("ipe");
		eIpe.setAttribute("version", "70005");
		eIpe.setAttribute("creator", "OpenMetroMaps");
		doc.appendChild(eIpe);

		Element eIpestyle = doc.createElement("ipestyle");

		Element layout = doc.createElement("layout");
		eIpestyle.appendChild(layout);
		layout.setAttribute("paper", width + " " + height);
		layout.setAttribute("origin", "0 0");
		layout.setAttribute("frame", width + " " + height);

		for (int i = 0; i <= 100; i += 10) {
			Element opacity = doc.createElement("opacity");
			eIpestyle.appendChild(opacity);
			opacity.setAttribute("name", i + "%");
			opacity.setAttribute("value",
					String.format(Locale.US, "%.2f", i / (double) 100));
		}

		Element ePage = doc.createElement("page");
		eIpe.appendChild(eIpestyle);
		eIpe.appendChild(ePage);

		Element eGroup = doc.createElement("group");
		eGroup.setAttribute("matrix", String.format("1 0 0 -1 0 %d", height));
		ePage.appendChild(eGroup);

		NodeList paths = docSource.getElementsByTagName("path");
		for (int i = 0; i < paths.getLength(); i++) {
			Node element = paths.item(i);
			Node copy = doc.importNode(element, true);
			eGroup.appendChild(copy);
		}

		DocumentWriter writer = new DocumentWriter();
		writer.write(doc, pathOutput);
	}

}
