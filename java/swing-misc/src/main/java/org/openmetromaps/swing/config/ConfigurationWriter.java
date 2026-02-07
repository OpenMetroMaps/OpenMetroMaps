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
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openmetromaps.swing.Theme;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConfigurationWriter
{

	public static void write(Configuration configuration, OutputStream out)
			throws IOException
	{
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();

			buildDocument(document, configuration);

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");

			transformer.transform(new DOMSource(document),
					new StreamResult(out));
		} catch (ParserConfigurationException | TransformerException e) {
			throw new IOException("Unable to write configuration", e);
		}
	}

	private static void buildDocument(Document document,
			Configuration configuration)
	{
		Element eConfiguration = document.createElement("configuration");
		document.appendChild(eConfiguration);

		Theme theme = configuration.getTheme();
		if (theme != null) {
			addOption(document, eConfiguration, "theme", theme.name());
		}
		if (configuration.getDockingFramesTheme() != null) {
			addOption(document, eConfiguration, "docking-frames-theme",
					configuration.getDockingFramesTheme());
		}
	}

	private static void addOption(Document document, Element eConfiguration,
			String name, String value)
	{
		Element eOption = document.createElement("option");
		eOption.setAttribute("name", name);
		eOption.setAttribute("value", value == null ? "" : value);
		eConfiguration.appendChild(eOption);
	}

}
