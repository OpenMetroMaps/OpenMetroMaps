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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.slimjars.dist.gnu.trove.iterator.TLongObjectIterator;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmRelationMember;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFileInput;
import de.topobyte.osm4j.utils.OsmIoUtils;
import de.topobyte.osm4j.utils.OsmOutputConfig;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class CompareWithCurrentGeofabrikData
{

	final static Logger logger = LoggerFactory
			.getLogger(CompareWithCurrentGeofabrikData.class);

	public static void main(String[] args)
	{
		Path projectRoot = CiTools.determineProjetRoot();
		try {
			processAndCompare(projectRoot,
					projectRoot.resolve(
							"java/import-config/src/test/resources/berlin.xml"),
					projectRoot.resolve(
							"java/test-data/src/main/resources/berlin-geographic.omm"),
					"https://download.geofabrik.de/europe/germany/brandenburg-latest.osm.pbf",
					projectRoot.resolve("brandenburg-latest.osm.pbf"),
					projectRoot.resolve("berlin.geofabrik.omm"),
					projectRoot.resolve("berlin-filtered.osm.pbf"));
		} catch (Exception e) {
			logger.error("Error while processing", e);
			System.exit(1);
		}
	}

	private static void processAndCompare(Path projectRoot, Path pathConfig,
			Path pathReferenceOmm, String url, Path pathPbf, Path pathOutput,
			Path pathOutputFilteredOsm)
			throws IOException, ParsingException, ParserConfigurationException
	{
		System.out.println("Running CI Check");
		System.out.println("Config: " + pathConfig);
		System.out.println("Reference: " + pathReferenceOmm);

		ImportConfig importConfig = CiTools.loadConfig(pathConfig);
		Path pbfPath = ensurePbfExists(pathPbf, url);
		ModelData osmData = loadOsmDataFromPbf(pbfPath, importConfig,
				pathOutputFilteredOsm);
		ModelData referenceData = CiTools.loadReferenceData(pathReferenceOmm);

		CiTools.save(osmData, pathOutput);

		System.out.println("Stats OSM: " + osmData.stations.size()
				+ " stations, " + osmData.lines.size() + " lines");
		System.out.println("Stats Ref: " + referenceData.stations.size()
				+ " stations, " + referenceData.lines.size() + " lines");

		CiTools.compareAndExit(osmData, referenceData);
	}

	private static Path ensurePbfExists(Path path, String url)
			throws IOException
	{
		if (Files.exists(path)) {
			System.out.println(
					"PBF file already exists: " + path.toAbsolutePath());
			return path;
		}

		System.out.println("Downloading PBF file from " + url);
		try (InputStream in = new URL(url).openStream()) {
			Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		}
		System.out.println("Download complete.");
		return path;
	}

	private static ModelData loadOsmDataFromPbf(Path pathPbf,
			ImportConfig config, Path pathOutputFilteredOsm) throws IOException
	{
		if (!(config.getSource() instanceof OsmSource)) {
			throw new IllegalArgumentException(
					"Config is not an OSM configuration");
		}

		OsmSource source = (OsmSource) config.getSource();
		Processing processing = config.getProcessing();

		RouteFilter routeFilter = new OsmSourceRouteFilter(source);
		List<Fix> fixes = new ArrayList<>();

		InMemoryMapDataSet data = filterOsmData(pathPbf, routeFilter);

		System.out
				.println(String.format("Loaded %d nodes, %d ways, %d relations",
						data.getNodes().size(), data.getWays().size(),
						data.getRelations().size()));

		saveOsmData(data, pathOutputFilteredOsm);

		ModelBuilder modelBuilder = new ModelBuilder(data, routeFilter,
				processing.getPrefixes(), processing.getSuffixes(), fixes);
		modelBuilder.run(true, true);

		DraftModel draftModel = modelBuilder.getModel();
		ModelData model = new DraftModelConverter().convert(draftModel);

		return model;
	}

	private static InMemoryMapDataSet filterOsmData(Path pbfPath,
			RouteFilter routeFilter) throws IOException
	{
		OsmFileInput input = new OsmFileInput(pbfPath, FileFormat.PBF);

		Set<Long> relevantRelations = new HashSet<>();
		Set<Long> relevantWays = new HashSet<>();
		Set<Long> relevantNodes = new HashSet<>();

		System.out.println("Scanning relations...");
		for (EntityContainer container : input.createIterator(true, false)
				.getIterator()) {
			OsmEntity entity = container.getEntity();
			if (entity instanceof OsmRelation) {
				OsmRelation relation = (OsmRelation) entity;
				if (routeFilter.useRoute(relation)) {
					relevantRelations.add(relation.getId());
					for (int i = 0; i < relation.getNumberOfMembers(); i++) {
						OsmRelationMember member = relation.getMember(i);
						if (member.getType() == EntityType.Way) {
							relevantWays.add(member.getId());
						} else if (member.getType() == EntityType.Node) {
							// Add nodes directly referenced by
							// relations (stops/platforms)
							relevantNodes.add(member.getId());
						}
					}
				}
			}
		}

		System.out.println("Scanning ways...");
		for (EntityContainer container : input.createIterator(true, false)
				.getIterator()) {
			OsmEntity entity = container.getEntity();
			if (entity instanceof OsmWay) {
				if (relevantWays.contains(entity.getId())) {
					OsmWay way = (OsmWay) entity;
					for (int i = 0; i < way.getNumberOfNodes(); i++) {
						relevantNodes.add(way.getNodeId(i));
					}
				}
			}
		}

		System.out.println("Loading data...");
		InMemoryMapDataSet data = new InMemoryMapDataSet();

		for (EntityContainer container : input.createIterator(true, false)
				.getIterator()) {
			OsmEntity entity = container.getEntity();
			EntityType type = container.getType();
			if (type == EntityType.Node) {
				if (relevantNodes.contains(entity.getId())) {
					data.getNodes().put(entity.getId(), (OsmNode) entity);
				}
			} else if (type == EntityType.Way) {
				if (relevantWays.contains(entity.getId())) {
					data.getWays().put(entity.getId(), (OsmWay) entity);
				}
			} else if (type == EntityType.Relation) {
				if (relevantRelations.contains(entity.getId())) {
					data.getRelations().put(entity.getId(),
							(OsmRelation) entity);
				}
			}
		}

		return data;
	}

	private static void saveOsmData(InMemoryMapDataSet data,
			Path pathOutputFilteredOsm) throws IOException
	{
		try (OutputStream os = Files.newOutputStream(pathOutputFilteredOsm)) {
			OsmOutputStream osmOutput = OsmIoUtils.setupOsmOutput(os,
					new OsmOutputConfig(FileFormat.PBF));

			TLongObjectIterator<OsmNode> nodes = data.getNodes().iterator();
			while (nodes.hasNext()) {
				nodes.advance();
				osmOutput.write(nodes.value());
			}

			TLongObjectIterator<OsmWay> ways = data.getWays().iterator();
			while (ways.hasNext()) {
				ways.advance();
				osmOutput.write(ways.value());
			}

			TLongObjectIterator<OsmRelation> relations = data.getRelations()
					.iterator();
			while (relations.hasNext()) {
				relations.advance();
				osmOutput.write(relations.value());
			}

			osmOutput.complete();
		}
	}

}
