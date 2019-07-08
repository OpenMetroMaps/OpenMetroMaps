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

package org.openmetromaps.newformat;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.TestData;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class TestPrintNewFormat
{

	public static void main(String[] args)
			throws IOException, ParsingException, ParserConfigurationException
	{
		XmlModel xmlModel = TestData.berlinXml();

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		NewFormatWriter writer = new NewFormatWriter();
		writer.write(System.out, model.getData(), model.getViews());
	}

}
