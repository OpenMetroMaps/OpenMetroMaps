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

package org.openmetromaps.mobidig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.mobidig.demo.AufzugViewer;

import de.topobyte.melon.resources.Resources;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class Util
{

	public static MapModel stuttgartSchematic() throws ParsingException
	{
		return load("sbahn-schematic.omm");
	}

	public static MapModel stuttgartGeographic() throws ParsingException
	{
		return load("sbahn-geographic.omm");
	}

	public static MapModel load(String resource) throws ParsingException
	{
		InputStream input = AufzugViewer.class.getClassLoader()
				.getResourceAsStream(resource);
		XmlModel xmlModel = DesktopXmlModelReader.read(input);

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);
		return model;
	}

	public static List<String> lines(String resourceName) throws IOException
	{
		InputStream is = Resources.stream(resourceName);
		String text = IOUtils.toString(is, StandardCharsets.UTF_8);
		String[] lines = text.split("\\r?\\n");
		return Arrays.asList(lines);
	}

}
