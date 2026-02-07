// Copyright 2026 Sebastian Kuerten
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

package org.openmetromaps.swing.config;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.swing.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigurationReader
{

	final static Logger logger = LoggerFactory
			.getLogger(ConfigurationReader.class);

	public static Configuration read(InputStream input)
			throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(input);

		Configuration configuration = new Configuration();
		NodeList options = document.getElementsByTagName("option");
		for (int i = 0; i < options.getLength(); i++) {
			Element option = (Element) options.item(i);
			String name = option.getAttribute("name");
			String value = option.getAttribute("value");
			if (name == null || value == null) {
				continue;
			}
			parseOption(configuration, name, value);
		}

		return configuration;
	}

	private static void parseOption(Configuration configuration, String name,
			String value)
	{
		if (name.equals("theme")) {
			try {
				configuration.setTheme(Theme.valueOf(value));
			} catch (IllegalArgumentException e) {
				logger.warn("Unknown theme value: {}", value);
			}
		} else if (name.equals("docking-frames-theme")) {
			configuration.setDockingFramesTheme(value);
		} else {
			logger.warn("unhandled option: {}:{}", name, value);
		}
	}

}
