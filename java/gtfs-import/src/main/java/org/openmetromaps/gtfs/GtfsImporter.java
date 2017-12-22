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
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipException;

import org.openmetromaps.gtfs4j.csv.GtfsZip;
import org.openmetromaps.gtfs4j.model.Agency;

public class GtfsImporter
{

	public GtfsImporter(Path path) throws ZipException, IOException
	{
		GtfsZip zip = new GtfsZip(path);

		List<Agency> agencies = zip.readAgency();
		for (Agency agency : agencies) {
			System.out.println(agency.getName());
		}

		zip.close();
	}

}
