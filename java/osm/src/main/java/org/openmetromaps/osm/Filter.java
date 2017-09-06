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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.slimjars.dist.gnu.trove.set.TLongSet;
import com.slimjars.dist.gnu.trove.set.hash.TLongHashSet;

import de.topobyte.adt.graph.Graph;
import de.topobyte.melon.io.StreamUtil;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.access.OsmIteratorInput;
import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.dataset.ListDataSetLoader;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmRelationMember;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.core.util.NodeIterator;
import de.topobyte.osm4j.core.util.RelationIterator;
import de.topobyte.osm4j.core.util.WayIterator;
import de.topobyte.osm4j.extra.relations.RelationGraph;
import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFile;
import de.topobyte.osm4j.utils.OsmFileInput;
import de.topobyte.osm4j.utils.OsmIoUtils;
import de.topobyte.osm4j.utils.OsmOutputConfig;
import de.topobyte.osm4j.utils.merge.sorted.SortedMerge;
import de.topobyte.osm4j.utils.split.EntitySplitter;

/**
 * This is a pretty nice fully-fledged filter base class that could be moved to
 * osm4j at some point. It can be used to filter some entities of interest by
 * implementing the {@link #take(OsmNode)}, {@link #take(OsmWay)} and
 * {@link #take(OsmRelation)} methods and cares about referential integrity
 * within the extracted data set, i.e. it makes sure to also extract referenced
 * relation members and way nodes. It even makes sure to keep recursive
 * relations intact (relations with relation members).
 */
public abstract class Filter
{

	final static Logger logger = LoggerFactory.getLogger(Filter.class);

	private OsmFile input;
	private OsmFile output;
	private boolean useMetadata;

	private FileFormat formatIntermediate;

	private OsmOutputConfig outputConfigIntermediate;
	private OsmOutputConfig outputConfigTarget;

	public Filter(OsmFile input, OsmFile output, boolean useMetadata)
	{
		this.input = input;
		this.output = output;
		this.useMetadata = useMetadata;

		formatIntermediate = FileFormat.TBO;

		outputConfigIntermediate = new OsmOutputConfig(formatIntermediate,
				useMetadata);
		outputConfigTarget = new OsmOutputConfig(output.getFileFormat(),
				useMetadata);
	}

	protected abstract boolean take(OsmNode node);

	protected abstract boolean take(OsmWay way);

	protected abstract boolean take(OsmRelation relation);

	public void execute() throws IOException
	{
		Path dir = Files.createTempDir().toPath();

		// These are the original entities from the input, for faster access
		// store them in temporary files, one per entity type.
		OsmFile fileNodes = new OsmFile(dir.resolve("nodes.tbo"),
				formatIntermediate);
		OsmFile fileWays = new OsmFile(dir.resolve("ways.tbo"),
				formatIntermediate);
		OsmFile fileRelations = new OsmFile(dir.resolve("relations.tbo"),
				formatIntermediate);

		// These store the entities selected with the take() methods.
		OsmFile fileNodesFiltered = new OsmFile(
				dir.resolve("nodes-filtered.tbo"), formatIntermediate);
		OsmFile fileWaysFiltered = new OsmFile(dir.resolve("ways-filtered.tbo"),
				formatIntermediate);
		OsmFile fileRelationsFiltered = new OsmFile(
				dir.resolve("relations-filtered.tbo"), formatIntermediate);

		// The ways references by relations
		OsmFile fileRelationWays = new OsmFile(dir.resolve("relation-ways.tbo"),
				formatIntermediate);
		// The nodes from ways, relations, and relation ways
		OsmFile fileAdditionalNodes = new OsmFile(
				dir.resolve("additional-nodes.tbo"), formatIntermediate);

		OsmFileInput inputFilteredRelations = new OsmFileInput(
				fileRelationsFiltered);

		logger.info("Splitting to separate files...");

		OsmIteratorInput iterator = new OsmFileInput(input).createIterator(true,
				useMetadata);
		EntitySplitter splitter = new EntitySplitter(iterator.getIterator(),
				fileNodes.getPath(), fileWays.getPath(),
				fileRelations.getPath(), outputConfigIntermediate);
		splitter.execute();
		iterator.close();

		logger.info("Building relation graph...");
		RelationGraph relationGraph = new RelationGraph(true, false);
		OsmIteratorInput relationIterator = new OsmFileInput(fileRelations)
				.createIterator(false, false);
		relationGraph.build(relationIterator.getIterator());
		relationIterator.close();

		logger.info("Filtering...");

		filter(fileNodes, fileNodesFiltered, EntityType.Node);
		filter(fileWays, fileWaysFiltered, EntityType.Way);
		filter(fileRelations, fileRelationsFiltered, EntityType.Relation);

		logger.info("Selecting additional relations...");

		InMemoryListDataSet relations = ListDataSetLoader.read(
				inputFilteredRelations.createIterator(true, useMetadata), true,
				true, true);

		TLongSet all = new TLongHashSet();
		Graph<Long> graph = relationGraph.getGraph();
		TLongSet simple = relationGraph.getIdsSimpleRelations();
		for (OsmRelation relation : relations.getRelations()) {
			if (simple.contains(relation.getId())) {
				all.add(relation.getId());
			} else {
				all.addAll(graph.getReachable(relation.getId()));
			}
		}

		logger.info("Original number of relations: "
				+ relations.getRelations().size());
		logger.info("Final number of relations: " + all.size());

		logger.info("Extracting extended relation set...");
		filter(fileRelations, fileRelationsFiltered, EntityType.Relation, all);

		logger.info("Collecting relation member ids...");
		TLongSet nodeIds = new TLongHashSet();
		TLongSet wayIds = new TLongHashSet();

		collectMemberIds(inputFilteredRelations, nodeIds, wayIds);

		logger.info("Extracting relation ways...");
		filter(fileWays, fileRelationWays, EntityType.Way, wayIds);

		logger.info("Extracting way nodes...");
		collectWayNodeIds(new OsmFileInput(fileWaysFiltered), nodeIds);
		collectWayNodeIds(new OsmFileInput(fileRelationWays), nodeIds);

		logger.info("Extracting additional nodes...");
		filter(fileNodes, fileAdditionalNodes, EntityType.Node, nodeIds);

		logger.info("Merging...");

		List<OsmFile> files = new ArrayList<>();
		files.add(fileNodesFiltered);
		files.add(fileAdditionalNodes);
		files.add(fileWaysFiltered);
		files.add(fileRelationWays);
		files.add(fileRelationsFiltered);

		List<OsmIterator> iterators = new ArrayList<>();
		for (OsmFile file : files) {
			OsmIteratorInput input = new OsmFileInput(file).createIterator(true,
					useMetadata);
			iterators.add(input.getIterator());
		}

		OutputStream os = StreamUtil.bufferedOutputStream(output.getPath());
		OsmOutputStream output = OsmIoUtils.setupOsmOutput(os,
				outputConfigTarget);

		SortedMerge merge = new SortedMerge(output, iterators);
		merge.run();

		logger.info("Deleting intermediate files...");
		FileUtils.deleteDirectory(dir.toFile());
	}

	private void filter(OsmFile fileInput, OsmFile fileOutput, EntityType type)
			throws IOException
	{
		OsmIteratorInput iterator = new OsmFileInput(fileInput)
				.createIterator(true, useMetadata);

		OutputStream os = StreamUtil.bufferedOutputStream(fileOutput.getPath());
		OsmOutputStream output = OsmIoUtils.setupOsmOutput(os,
				outputConfigIntermediate);

		if (type == EntityType.Node) {
			filterNodes(iterator.getIterator(), output);
		} else if (type == EntityType.Way) {
			filterWays(iterator.getIterator(), output);
		} else if (type == EntityType.Relation) {
			filterRelations(iterator.getIterator(), output);
		}

		output.complete();
		os.close();
		iterator.close();
	}

	private void filter(OsmFile fileInput, OsmFile fileOutput, EntityType type,
			TLongSet ids) throws IOException
	{
		OsmIteratorInput iterator = new OsmFileInput(fileInput)
				.createIterator(true, useMetadata);

		OutputStream os = StreamUtil.bufferedOutputStream(fileOutput.getPath());
		OsmOutputStream output = OsmIoUtils.setupOsmOutput(os,
				outputConfigIntermediate);

		if (type == EntityType.Node) {
			filterNodes(iterator.getIterator(), output, ids);
		} else if (type == EntityType.Way) {
			filterWays(iterator.getIterator(), output, ids);
		} else if (type == EntityType.Relation) {
			filterRelations(iterator.getIterator(), output, ids);
		}

		output.complete();
		os.close();
		iterator.close();
	}

	private void filterNodes(OsmIterator iterator, OsmOutputStream output)
			throws IOException
	{
		for (OsmNode node : new NodeIterator(iterator)) {
			if (take(node)) {
				output.write(node);
			}
		}
	}

	private void filterWays(OsmIterator iterator, OsmOutputStream output)
			throws IOException
	{
		for (OsmWay way : new WayIterator(iterator)) {
			if (take(way)) {
				output.write(way);
			}
		}
	}

	private void filterRelations(OsmIterator iterator, OsmOutputStream output)
			throws IOException
	{
		for (OsmRelation relation : new RelationIterator(iterator)) {
			if (take(relation)) {
				output.write(relation);
			}
		}
	}

	private void filterNodes(OsmIterator iterator, OsmOutputStream output,
			TLongSet ids) throws IOException
	{
		for (OsmNode node : new NodeIterator(iterator)) {
			if (ids.contains(node.getId())) {
				output.write(node);
			}
		}
	}

	private void filterWays(OsmIterator iterator, OsmOutputStream output,
			TLongSet ids) throws IOException
	{
		for (OsmWay way : new WayIterator(iterator)) {
			if (ids.contains(way.getId())) {
				output.write(way);
			}
		}
	}

	private void filterRelations(OsmIterator iterator, OsmOutputStream output,
			TLongSet ids) throws IOException
	{
		for (OsmRelation relation : new RelationIterator(iterator)) {
			if (ids.contains(relation.getId())) {
				output.write(relation);
			}
		}
	}

	private void collectMemberIds(OsmFileInput input, TLongSet nodeIds,
			TLongSet wayIds) throws IOException
	{
		OsmIteratorInput iterator = input.createIterator(false, false);
		for (OsmRelation relation : new RelationIterator(
				iterator.getIterator())) {
			for (OsmRelationMember member : OsmModelUtil
					.membersAsList(relation)) {
				if (member.getType() == EntityType.Node) {
					nodeIds.add(member.getId());
				} else if (member.getType() == EntityType.Way) {
					wayIds.add(member.getId());
				}
			}
		}
		iterator.close();
	}

	private void collectWayNodeIds(OsmFileInput input, TLongSet nodeIds)
			throws IOException
	{
		OsmIteratorInput iterator = input.createIterator(false, false);
		for (OsmWay way : new WayIterator(iterator.getIterator())) {
			nodeIds.addAll(OsmModelUtil.nodesAsList(way));
		}
		iterator.close();
	}

}
