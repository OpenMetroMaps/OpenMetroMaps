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

package org.openmetromaps.maps.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.openmetromaps.maps.CoordinateConversion;
import org.openmetromaps.maps.CoordinateConversionType;
import org.openmetromaps.maps.MapModel;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class TestConvert
{

	@Test
	public void test()
			throws ParserConfigurationException, ParsingException, IOException
	{
		InputStream input = TestConvert.class.getClassLoader()
				.getResourceAsStream("berlin-schematic.omm");

		XmlModel xmlModel = DesktopXmlModelReader.read(input);
		input.close();

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		CoordinateConversion.convertViews(model,
				CoordinateConversionType.IDENTITY);

		XmlModelWriter writer = new XmlModelWriter();

		writer.write(System.out, model.getData(), model.getViews());
	}

}
