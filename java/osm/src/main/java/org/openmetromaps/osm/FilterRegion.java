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

package org.openmetromaps.osm;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.vividsolutions.jts.geom.Geometry;

import de.topobyte.melon.io.StreamUtil;
import de.topobyte.osm4j.core.access.OsmIteratorInput;
import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFile;
import de.topobyte.osm4j.utils.OsmFileInput;
import de.topobyte.osm4j.utils.OsmIoUtils;
import de.topobyte.osm4j.utils.OsmOutputConfig;
import de.topobyte.osm4j.utils.areafilter.RegionFilter;
import de.topobyte.osm4j.utils.split.EntitySplitter;

public class FilterRegion
{

	final static Logger logger = LoggerFactory.getLogger(FilterRegion.class);

	private OsmFile input;
	private OsmFile output;
	private Geometry region;

	private FileFormat formatIntermediate;

	private boolean useMetadata;
	private OsmOutputConfig outputConfigTarget;
	private OsmOutputConfig outputConfigIntermediate;

	public FilterRegion(OsmFile input, OsmFile output, Geometry region,
			OsmOutputConfig outputConfig)
	{
		this.input = input;
		this.output = output;
		this.region = region;
		this.outputConfigTarget = outputConfig;
		useMetadata = outputConfig.isWriteMetadata();

		formatIntermediate = FileFormat.TBO;

		outputConfigIntermediate = new OsmOutputConfig(formatIntermediate,
				useMetadata);
	}

	public void execute() throws IOException
	{
		Path dir = Files.createTempDir().toPath();

		OsmFile fileFiltered = new OsmFile(dir.resolve("filtered.tbo"),
				formatIntermediate);

		// These are the original entities from the input, for faster access
		// store them in temporary files, one per entity type.
		OsmFile fileNodes = new OsmFile(dir.resolve("nodes.tbo"),
				formatIntermediate);
		OsmFile fileWays = new OsmFile(dir.resolve("ways.tbo"),
				formatIntermediate);
		OsmFile fileRelations = new OsmFile(dir.resolve("relations.tbo"),
				formatIntermediate);

		// These are the filtered entities, for faster access store them in
		// temporary files, one per entity type.
		OsmFile fileNodesFiltered = new OsmFile(
				dir.resolve("nodes-filtered.tbo"), formatIntermediate);
		OsmFile fileWaysFiltered = new OsmFile(dir.resolve("ways-filtered.tbo"),
				formatIntermediate);
		OsmFile fileRelationsFiltered = new OsmFile(
				dir.resolve("relations-filtered.tbo"), formatIntermediate);

		logger.info("Filtering by area...");

		OutputStream os = StreamUtil
				.bufferedOutputStream(fileFiltered.getPath());
		OsmOutputStream outputFiltered = OsmIoUtils.setupOsmOutput(os,
				outputConfigIntermediate);

		OsmIteratorInput iterator = new OsmFileInput(input).createIterator(true,
				useMetadata);

		RegionFilter filter = new RegionFilter(outputFiltered,
				iterator.getIterator(), region, false);
		filter.run();

		logger.info("Splitting original to separate files...");

		OsmIteratorInput iteratorOriginal = new OsmFileInput(input)
				.createIterator(true, useMetadata);
		EntitySplitter splitterOriginal = new EntitySplitter(
				iteratorOriginal.getIterator(), fileNodes.getPath(),
				fileWays.getPath(), fileRelations.getPath(),
				outputConfigIntermediate);
		splitterOriginal.execute();
		iteratorOriginal.close();

		logger.info("Splitting filtered to separate files...");

		OsmIteratorInput iteratorFiltered = new OsmFileInput(fileFiltered)
				.createIterator(true, useMetadata);
		EntitySplitter splitterFiltered = new EntitySplitter(
				iteratorFiltered.getIterator(), fileNodesFiltered.getPath(),
				fileWaysFiltered.getPath(), fileRelationsFiltered.getPath(),
				outputConfigIntermediate);
		splitterFiltered.execute();
		iteratorFiltered.close();

		logger.info("Collecting references...");

		Collector collector = new Collector(fileNodes, fileWays, fileRelations,
				fileNodesFiltered, fileWaysFiltered, fileRelationsFiltered,
				output, outputConfigTarget);
		collector.execute(dir);

		logger.info("Deleting intermediate files...");
		FileUtils.deleteDirectory(dir.toFile());
	}

}
