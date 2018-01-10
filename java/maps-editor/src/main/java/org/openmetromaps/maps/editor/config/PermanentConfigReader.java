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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PermanentConfigReader
{

	final static Logger logger = LoggerFactory
			.getLogger(PermanentConfigReader.class);

	public static PermanentConfiguration read(InputStream input)
			throws ParserConfigurationException, SAXException,
			FileNotFoundException, IOException
	{
		SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
		PermanentConfigHandler configHandler = new PermanentConfigHandler();
		sax.parse(input, configHandler);

		return configHandler.configuration;
	}

}

class PermanentConfigHandler extends DefaultHandler
{

	final static Logger logger = LoggerFactory
			.getLogger(PermanentConfigHandler.class);

	PermanentConfiguration configuration = new PermanentConfiguration();

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
		if (name.equals("look-and-feel")) {
			configuration.setLookAndFeel(value);
		} else if (name.equals("docking-frames-theme")) {
			configuration.setDockingFramesTheme(value);
		} else {
			logger.debug(String.format("unhandled option: %s:%s", name, value));
		}
	}

}
