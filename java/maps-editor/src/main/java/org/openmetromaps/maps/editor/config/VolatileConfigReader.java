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

package org.openmetromaps.maps.editor.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class VolatileConfigReader
{

	final static Logger logger = LoggerFactory
			.getLogger(VolatileConfigReader.class);

	public static VolatileConfiguration read(InputStream input)
			throws ParserConfigurationException, SAXException,
			FileNotFoundException, IOException
	{
		SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
		VolatileConfigHandler configHandler = new VolatileConfigHandler();
		sax.parse(input, configHandler);

		return configHandler.configuration;
	}

}

class VolatileConfigHandler extends DefaultHandler
{

	final static Logger logger = LoggerFactory
			.getLogger(VolatileConfigHandler.class);

	VolatileConfiguration configuration = new VolatileConfiguration();

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes)
	{
		if (qName.equals("option")) {
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			if (name == null || value == null) {
				return;
			}
			parseOption(name, value);
		}
	}

	private void parseOption(String name, String value)
	{
		if (name.equals("last-used-directory")) {
			Path path = Paths.get(value);
			configuration.setLastUsedDirectory(path);
		} else {
			logger.debug(String.format("unhandled option: %s:%s", name, value));
		}
	}

}
