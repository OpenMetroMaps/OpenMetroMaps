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

package org.openmetromaps.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slimjars.dist.gnu.trove.iterator.TLongObjectIterator;

import de.topobyte.geomath.WGS84;
import de.topobyte.osm4j.core.access.OsmIteratorInput;
import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;
import de.topobyte.osm4j.core.dataset.MapDataSetLoader;
import de.topobyte.osm4j.core.dataset.sort.IdComparator;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmRelationMember;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.utils.OsmFile;
import de.topobyte.osm4j.utils.OsmFileInput;

public class BuildModel
{

	final static Logger logger = LoggerFactory.getLogger(BuildModel.class);

	private OsmFile fileInput;
	private List<Fix> fixes = new ArrayList<>();

	protected Map<String, OsmNode> stationMap;
	protected List<DraftLine> lines;

	public BuildModel(OsmFile fileInput, List<Fix> fixes)
	{
		this.fileInput = fileInput;
		this.fixes = fixes;
	}

	public void run(boolean applyFixes) throws Exception
	{
		OsmIteratorInput iterator = new OsmFileInput(fileInput)
				.createIterator(true, false);
		InMemoryMapDataSet dataSet = MapDataSetLoader.read(iterator, true, true,
				true);

		List<OsmRelation> relationsList = new ArrayList<>();

		stationMap = new HashMap<>();
		TLongObjectIterator<OsmNode> nIter = dataSet.getNodes().iterator();
		while (nIter.hasNext()) {
			nIter.advance();
			OsmNode node = nIter.value();
			Map<String, String> tags = OsmModelUtil.getTagsAsMap(node);
			String railway = tags.get("railway");
			if (railway == null) {
				continue;
			}
			if (!railway.equals("station")) {
				continue;
			}
			String stationName = tags.get("name");
			if (stationName == null) {
				continue;
			}
			stationName = stripPrefix(stationName);
			if (!stationMap.containsKey(stationName)) {
				stationMap.put(stationName, node);
			}
		}

		relationsList.addAll(dataSet.getRelations().valueCollection());
		Collections.sort(relationsList, new IdComparator());

		Set<String> interstingRouteTypes = new HashSet<>();
		interstingRouteTypes
				.addAll(Arrays.asList(new String[] { "light_rail", "subway" }));

		int nBugsNotFound = 0;
		int nBugsNoName = 0;

		lines = new ArrayList<>();

		for (OsmRelation relation : relationsList) {
			Map<String, String> rTags = OsmModelUtil.getTagsAsMap(relation);
			String route = rTags.get("route");
			if (route == null) {
				continue;
			}
			if (!interstingRouteTypes.contains(route)) {
				continue;
			}
			String name = rTags.get("name");
			String ref = rTags.get("ref");
			logger.info(String.format("Name: '%s', Ref: '%s'", name, ref));

			List<DraftStation> stations = new ArrayList<>();

			for (int i = 0; i < relation.getNumberOfMembers(); i++) {
				OsmRelationMember member = relation.getMember(i);
				if (member.getType() != EntityType.Node) {
					continue;
				}
				String role = member.getRole();
				if (!role.equals("stop")) {
					continue;
				}
				OsmNode node = dataSet.getNodes().get(member.getId());
				if (node == null) {
					logger.info("not found: " + member.getId());
					nBugsNotFound += 1;
					continue;
				}
				Map<String, String> nTags = OsmModelUtil.getTagsAsMap(node);
				String sName = nTags.get("name");
				if (sName == null) {
					sName = nTags.get("description");
				}
				if (sName == null && applyFixes) {
					sName = determineFix(node);
				}
				// ignore nodes without any tags
				if (sName == null) {
					if (nTags.size() == 0) {
						continue;
					}
				}
				if (sName == null) {
					nBugsNoName += 1;
					logger.info("tags: " + nTags);
					continue;
				}

				sName = stripPrefix(sName);

				logger.info(sName);
				DraftStation station = new DraftStation(sName, node);
				stations.add(station);
			}

			lines.add(new DraftLine(relation, stations));
		}

		logger.info("# Bugs (not found): " + nBugsNotFound);
		logger.info("# Bugs (no name): " + nBugsNoName);

		logger.info(String.format("Found %d lines", lines.size()));
		for (DraftLine line : lines) {
			Map<String, String> tags = OsmModelUtil
					.getTagsAsMap(line.getSource());
			String ref = tags.get("ref");
			logger.info("line: " + ref);
		}
	}

	public static String stripPrefix(String sName)
	{
		if (sName.startsWith("S ")) {
			sName = sName.substring(2);
		} else if (sName.startsWith("U ")) {
			sName = sName.substring(2);
		} else if (sName.startsWith("S+U ")) {
			sName = sName.substring(4);
		} else if (sName.startsWith("U-Bhf ")) {
			sName = sName.substring(6);
		}
		return sName;
	}

	private String determineFix(OsmNode node)
	{
		for (Fix fix : fixes) {
			double distance = distance(node, fix);
			logger.debug(String.format("distance to %.6f,%.6f: %.2f",
					node.getLatitude(), node.getLongitude(), distance));
			if (distance < 200) {
				return fix.getName();
			}
		}
		return null;
	}

	private double distance(OsmNode node, Fix fix)
	{
		double lon1 = node.getLongitude();
		double lat1 = node.getLatitude();
		double lon2 = fix.getLon();
		double lat2 = fix.getLat();
		return WGS84.haversineDistance(lon1, lat1, lon2, lat2);
	}

}
