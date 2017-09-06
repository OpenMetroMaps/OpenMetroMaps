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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import com.google.common.io.Files;

import de.topobyte.melon.io.StreamUtil;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmIoUtils;
import de.topobyte.osm4j.utils.OsmOutputConfig;

public abstract class Filter
{

	private Path input;
	private Path output;

	public Filter(Path input, Path output)
	{
		this.input = input;
		this.output = output;
	}

	protected abstract boolean take(OsmNode node);

	protected abstract boolean take(OsmWay way);

	protected abstract boolean take(OsmRelation relation);

	public void execute() throws IOException
	{
		InputStream is = StreamUtil.bufferedInputStream(input);
		OsmIterator iterator = OsmIoUtils.setupOsmIterator(is, FileFormat.TBO,
				true, false);

		OsmOutputConfig outputConfig = new OsmOutputConfig(FileFormat.TBO,
				false);

		Path dir = Files.createTempDir().toPath();
		Path pathNodes = dir.resolve("nodes.tbo");
		Path pathWays = dir.resolve("ways.tbo");
		Path pathRelations = dir.resolve("relations.tbo");

		OutputStream osNodes = StreamUtil.bufferedOutputStream(pathNodes);
		OutputStream osWays = StreamUtil.bufferedOutputStream(pathWays);
		OutputStream osRelations = StreamUtil
				.bufferedOutputStream(pathRelations);

		OsmOutputStream outputNodes = OsmIoUtils.setupOsmOutput(osNodes,
				outputConfig);
		OsmOutputStream outputWays = OsmIoUtils.setupOsmOutput(osWays,
				outputConfig);
		OsmOutputStream outputRelations = OsmIoUtils.setupOsmOutput(osRelations,
				outputConfig);

		while (iterator.hasNext()) {
			EntityContainer container = iterator.next();
			if (container.getType() == EntityType.Node) {
				OsmNode node = (OsmNode) container.getEntity();
				if (take(node)) {
					outputNodes.write(node);
				}
			} else if (container.getType() == EntityType.Way) {
				OsmWay way = (OsmWay) container.getEntity();
				if (take(way)) {
					outputWays.write(way);
				}
			} else if (container.getType() == EntityType.Relation) {
				OsmRelation relation = (OsmRelation) container.getEntity();
				if (take(relation)) {
					outputRelations.write(relation);
				}
			}
		}

		outputNodes.complete();
		outputWays.complete();
		outputRelations.complete();
		osNodes.close();
		osWays.close();
		osRelations.close();
	}

}
