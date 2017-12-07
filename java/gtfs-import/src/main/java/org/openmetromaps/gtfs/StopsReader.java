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

public class StopsReader
{

	private CSVReader csvReader;

	private int idxStopId;
	private int idxStopName;
	private int idxStopLon;
	private int idxStopLat;

	public StopsReader(Reader reader) throws IOException
	{
		csvReader = new CSVReader(reader, ',', '"');

		String[] head = csvReader.readNext();

		idxStopId = Util.getIndex(head, "stop_id");
		idxStopName = Util.getIndex(head, "stop_name");
		idxStopLon = Util.getIndex(head, "stop_lon");
		idxStopLat = Util.getIndex(head, "stop_lat");
	}

	public List<Stop> readAll() throws IOException
	{
		List<Stop> stops = new ArrayList<>();

		while (true) {
			String[] parts = csvReader.readNext();
			if (parts == null) {
				break;
			}
			String id = parts[idxStopId];
			String name = parts[idxStopName];
			String lon = parts[idxStopLon];
			String lat = parts[idxStopLat];
			stops.add(new Stop(id, name, lat, lon));
		}

		csvReader.close();

		return stops;
	}

}
