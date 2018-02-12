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

package org.openmetromaps.markdownview;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;

import de.topobyte.webpaths.WebPath;

public class StationWriter
{

	private Context context;
	private Path file;
	private Station station;

	public StationWriter(Context context, Path file, Station station)
	{
		this.context = context;
		this.file = file;
		this.station = station;
	}

	public void write() throws IOException
	{
		MarkdownWriter output = new MarkdownWriter(file);

		output.heading(1, station.getName());

		List<Line> lines = new ArrayList<>(
				context.getStationToLines().get(station));
		MapModelUtil.sortLinesByName(lines);

		WebPath path = context.path(station);

		for (Line line : lines) {
			WebPath relative = path.relativize(context.path(line));
			String link = String.format("[%s](%s)", line.getName(),
					relative.toString());
			output.unordered(link);
		}

		output.close();
	}

}
