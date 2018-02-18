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

package org.openmetromaps.model.osm.inspector;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.xml.XmlModelWriter;
import org.openmetromaps.model.osm.DraftModel;
import org.openmetromaps.model.osm.DraftModelConverter;
import org.openmetromaps.model.osm.Fix;
import org.openmetromaps.model.osm.ModelBuilder;

import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFile;
import de.topobyte.system.utils.SystemPaths;

public class TestGueretBuildModel
{

	public static void main(String[] args) throws IOException,
			ParserConfigurationException, TransformerException
	{
		Path pathInput = SystemPaths.HOME.resolve("gueret/gueret.osm.pbf");
		OsmFile fileInput = new OsmFile(pathInput, FileFormat.PBF);

		Path pathOutput = SystemPaths.HOME.resolve("gueret/gueret.omm");

		System.out.println("Input: " + pathInput);

		List<String> prefixes = new ArrayList<>();
		List<Fix> fixes = new ArrayList<>();

		ModelBuilder modelBuilder = new ModelBuilder(fileInput, prefixes,
				fixes);
		modelBuilder.run(true);

		DraftModel model = modelBuilder.getModel();

		ModelData data = new DraftModelConverter().convert(model);
		XmlModelWriter writer = new XmlModelWriter();
		OutputStream os = Files.newOutputStream(pathOutput);
		writer.write(os, data, new ArrayList<>());
		os.close();
	}

}
