// Copyright 2025 Sebastian Kuerten
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

package org.openmetromaps.cli.gtfs;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.gtfs.DraftModel;
import org.openmetromaps.gtfs.GtfsImporter;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.xml.XmlModelWriter;
import org.openmetromaps.misc.NameChanger;
import org.openmetromaps.model.gtfs.DraftModelConverter;

import de.topobyte.system.utils.SystemPaths;

public class RunGtfsImport19
{

	public static void main(String[] args) throws Exception
	{
		boolean fixBoms = false;

		Path pathInput = SystemPaths.HOME.resolve("in/omm/issue-19/gtfs.zip");
		Path pathOutput = SystemPaths.HOME.resolve("in/omm/issue-19/gtfs.omm");

		System.out.println("Input: " + pathInput);
		System.out.println("Output: " + pathOutput);

		List<String> prefixes = new ArrayList<>();
		prefixes.add("S ");
		prefixes.add("U ");
		prefixes.add("S+U ");
		prefixes.add("U-Bhf ");

		List<String> suffixes = new ArrayList<>();

		NameChanger nameChanger = new NameChanger(prefixes, suffixes);

		GtfsImporter importer = new GtfsImporter(pathInput, nameChanger,
				fixBoms);
		importer.execute();

		OutputStream os = Files.newOutputStream(pathOutput);

		DraftModel draft = importer.getModel();
		ModelData data = new DraftModelConverter().convert(draft);

		new XmlModelWriter().write(os, data, new ArrayList<>());
		os.close();
	}

}
