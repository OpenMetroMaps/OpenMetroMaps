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

import java.io.IOException;
import java.io.OutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class PermanentConfigWriter
{

	public static void write(PermanentConfiguration configuration, OutputStream out)
			throws IOException
	{
		DocumentFactory documentFactory = DocumentFactory.getInstance();
		Document document = documentFactory.createDocument();

		buildDocument(documentFactory, document, configuration);

		OutputFormat pretty = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(pretty);
		xmlWriter.setOutputStream(out);
		xmlWriter.write(document);
	}

	private static void buildDocument(DocumentFactory documentFactory,
			Document document, PermanentConfiguration configuration)
	{
		Element eConfiguration = documentFactory.createElement("configuration");
		document.add(eConfiguration);

		if (configuration.getLookAndFeel() != null) {
			addOption(documentFactory, eConfiguration, "look-and-feel",
					configuration.getLookAndFeel());
		}
		if (configuration.getDockingFramesTheme() != null) {
			addOption(documentFactory, eConfiguration, "docking-frames-theme",
					configuration.getDockingFramesTheme());
		}
	}

	private static void addOption(DocumentFactory documentFactory,
			Element eConfiguration, String name, String value)
	{
		Element eOption = documentFactory.createElement("option");
		eOption.add(documentFactory.createAttribute(eOption, "name", name));
		// convert null to ""
		String v = value == null ? "" : value;
		eOption.add(documentFactory.createAttribute(eOption, "value", v));
		eConfiguration.add(eOption);
	}

}
