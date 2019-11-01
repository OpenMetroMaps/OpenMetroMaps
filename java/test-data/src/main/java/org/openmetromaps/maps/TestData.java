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

package org.openmetromaps.maps;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.misc.NameChanger;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class TestData
{

	public static XmlModel berlinXml() throws ParsingException
	{
		InputStream input = TestData.class.getClassLoader()
				.getResourceAsStream("berlin.omm");
		XmlModel model = DesktopXmlModelReader.read(input);
		return model;
	}

	public static NameChanger berlinGtfsNameChanger()
	{
		List<String> prefixes = new ArrayList<>();
		prefixes.add("S ");
		prefixes.add("U ");
		prefixes.add("S+U ");

		List<String> suffixes = new ArrayList<>();
		suffixes.add(" Bhf (Berlin)");
		suffixes.add(" (Berlin)");
		suffixes.add(" Bhf");
		for (int i = 1; i <= 9; i++) {
			suffixes.add(String.format(" (Berlin) [U%d]", i));
		}

		return new NameChanger(prefixes, suffixes);
	}

}
