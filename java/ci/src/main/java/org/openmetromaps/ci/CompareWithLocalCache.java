// Copyright 2026 Sebastian Kuerten
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

package org.openmetromaps.ci;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.imports.config.ImportConfig;
import org.openmetromaps.imports.config.Processing;
import org.openmetromaps.imports.config.osm.OsmSource;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.model.osm.DraftModel;
import org.openmetromaps.model.osm.DraftModelConverter;
import org.openmetromaps.model.osm.Fix;
import org.openmetromaps.model.osm.ModelBuilder;
import org.openmetromaps.model.osm.filter.RouteFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;
import de.topobyte.osm4j.core.dataset.MapDataSetLoader;
import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFileInput;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class CompareWithLocalCache
{

	final static Logger logger = LoggerFactory
			.getLogger(CompareWithLocalCache.class);

	public static void main(String[] args)
	{
		Path projectRoot = CiTools.determineProjetRoot();
		try {
			processAndCompare(projectRoot,
					projectRoot.resolve(
							"java/import-config/src/test/resources/berlin.xml"),
					projectRoot.resolve(
							"java/test-data/src/main/resources/berlin-geographic.omm"),
					projectRoot.resolve("berlin.geofabrik.omm"),
					projectRoot.resolve("berlin-filtered.osm.pbf"));
		} catch (Exception e) {
			logger.error("Error while processing", e);
			System.exit(1);
		}
	}

	private static void processAndCompare(Path projectRoot, Path pathConfig,
			Path pathReferenceOmm, Path pathOutput, Path pathFilteredOsm)
			throws IOException, ParsingException, ParserConfigurationException
	{
		System.out.println("Running CI Check");
		System.out.println("Config: " + pathConfig);
		System.out.println("Reference: " + pathReferenceOmm);

		ImportConfig importConfig = CiTools.loadConfig(pathConfig);
		ModelData osmData = loadOsmDataFromPbf(pathFilteredOsm, importConfig);
		ModelData referenceData = CiTools.loadReferenceData(pathReferenceOmm);

		CiTools.save(osmData, pathOutput);

		System.out.println("Stats OSM: " + osmData.stations.size()
				+ " stations, " + osmData.lines.size() + " lines");
		System.out.println("Stats Ref: " + referenceData.stations.size()
				+ " stations, " + referenceData.lines.size() + " lines");

		CiTools.compareAndExit(osmData, referenceData);
	}

	private static ModelData loadOsmDataFromPbf(Path pathPbf,
			ImportConfig config) throws IOException
	{
		if (!(config.getSource() instanceof OsmSource)) {
			throw new IllegalArgumentException(
					"Config is not an OSM configuration");
		}

		OsmSource source = (OsmSource) config.getSource();
		Processing processing = config.getProcessing();

		RouteFilter routeFilter = new OsmSourceRouteFilter(source);
		List<Fix> fixes = new ArrayList<>();

		InMemoryMapDataSet data = readOsmData(pathPbf);

		System.out
				.println(String.format("Loaded %d nodes, %d ways, %d relations",
						data.getNodes().size(), data.getWays().size(),
						data.getRelations().size()));

		ModelBuilder modelBuilder = new ModelBuilder(data, routeFilter,
				processing.getPrefixes(), processing.getSuffixes(), fixes);
		modelBuilder.run(true, true);

		DraftModel draftModel = modelBuilder.getModel();
		ModelData model = new DraftModelConverter().convert(draftModel);

		return model;
	}

	private static InMemoryMapDataSet readOsmData(Path pbfPath)
			throws IOException
	{
		OsmFileInput input = new OsmFileInput(pbfPath, FileFormat.PBF);
		InMemoryMapDataSet dataSet = MapDataSetLoader
				.read(input.createIterator(true, false), true, true, true);
		return dataSet;
	}

}
