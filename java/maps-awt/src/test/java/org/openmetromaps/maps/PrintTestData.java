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

import org.openmetromaps.maps.xml.XmlLine;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlStation;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class PrintTestData
{

	public static void main(String[] args) throws ParsingException
	{
		XmlModel model = TestData.berlinXml();
		for (XmlStation station : model.getStations()) {
			System.out.println(String.format("%s: %.6f,%.6f", station.getName(),
					station.getLocation().getLongitude(),
					station.getLocation().getLatitude()));
		}
		for (XmlLine line : model.getLines()) {
			System.out.println(String.format("line %s, %d stops, color: %s",
					line.getName(), line.getStops().size(), line.getColor()));
		}
	}

}
