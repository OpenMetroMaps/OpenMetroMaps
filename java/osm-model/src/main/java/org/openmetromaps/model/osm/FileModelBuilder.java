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

package org.openmetromaps.model.osm;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.osm4j.core.access.OsmIteratorInput;
import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;
import de.topobyte.osm4j.core.dataset.MapDataSetLoader;
import de.topobyte.osm4j.utils.OsmFile;
import de.topobyte.osm4j.utils.OsmFileInput;

public class FileModelBuilder
{

	final static Logger logger = LoggerFactory
			.getLogger(FileModelBuilder.class);

	private OsmFile fileInput;
	private List<String> prefixes;
	private List<Fix> fixes;

	private ModelBuilder modelBuilder = null;

	public FileModelBuilder(OsmFile fileInput, List<String> prefixes,
			List<Fix> fixes)
	{
		this.fileInput = fileInput;
		this.prefixes = prefixes;
		this.fixes = fixes;
	}

	public DraftModel getModel()
	{
		return modelBuilder.getModel();
	}

	public void run(boolean applyFixes) throws IOException
	{
		OsmIteratorInput iterator = new OsmFileInput(fileInput)
				.createIterator(true, false);
		InMemoryMapDataSet dataSet = MapDataSetLoader.read(iterator, true, true,
				true);

		modelBuilder = new ModelBuilder(dataSet, prefixes, fixes);
		modelBuilder.run(applyFixes);
	}

}
