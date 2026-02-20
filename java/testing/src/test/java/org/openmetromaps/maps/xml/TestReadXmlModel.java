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

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class TestReadXmlModel
{

	@Test
	public void test() throws ParsingException
	{
		InputStream input = TestReadXmlModel.class.getClassLoader()
				.getResourceAsStream("berlin-schematic.omm");
		XmlModel model = DesktopXmlModelReader.read(input);

		Assert.assertEquals(1, model.getXmlViews().size());
		Assert.assertEquals(25, model.getLines().size());
		Assert.assertEquals(315, model.getStations().size());

		for (XmlLine line : model.getLines()) {
			System.out.println(String.format("line %s, %d stops, color: %s",
					line.getName(), line.getStops().size(), line.getColor()));
		}
	}

}
