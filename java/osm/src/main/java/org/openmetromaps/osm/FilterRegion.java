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

public class FilterRegion
{

	private OsmFile input;
	private OsmFile output;
	private Geometry region;
	private boolean useMetadata;

	private FileFormat formatIntermediate;

	private OsmOutputConfig outputConfigIntermediate;
	private OsmOutputConfig outputConfigTarget;

	public FilterRegion(OsmFile input, OsmFile output, Geometry region,
			boolean useMetadata)
	{
		this.input = input;
		this.output = output;
		this.region = region;
		this.useMetadata = useMetadata;

		formatIntermediate = FileFormat.TBO;

		outputConfigIntermediate = new OsmOutputConfig(formatIntermediate,
				useMetadata);
		outputConfigTarget = new OsmOutputConfig(output.getFileFormat(),
				useMetadata);
	}

	public void execute() throws IOException
	{
		OutputStream os = StreamUtil.bufferedOutputStream(output.getPath());
		OsmOutputStream output = OsmIoUtils.setupOsmOutput(os,
				outputConfigTarget);

		OsmIteratorInput iterator = new OsmFileInput(input).createIterator(true,
				useMetadata);

		RegionFilter filter = new RegionFilter(output, iterator.getIterator(),
				region, false);
		filter.run();
	}

}
