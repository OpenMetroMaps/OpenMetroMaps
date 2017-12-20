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

package org.openmetromaps.gtfs;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class RoutesReader
{

	private CSVReader csvReader;

	private int idxRouteId;
	private int idxShortName;
	private int idxLongName;

	public RoutesReader(Reader reader) throws IOException
	{
		csvReader = new CSVReader(reader, ',', '"');

		String[] head = csvReader.readNext();

		idxRouteId = Util.getIndex(head, "route_id");
		idxShortName = Util.getIndex(head, "route_short_name");
		idxLongName = Util.getIndex(head, "route_long_name");
	}

	public List<Route> readAll() throws IOException
	{
		List<Route> routes = new ArrayList<>();

		while (true) {
			String[] parts = csvReader.readNext();
			if (parts == null) {
				break;
			}
			String id = parts[idxRouteId];
			String shortName = parts[idxShortName];
			String longName = parts[idxLongName];
			routes.add(new Route(id, shortName, longName));
		}

		csvReader.close();

		return routes;
	}

}
