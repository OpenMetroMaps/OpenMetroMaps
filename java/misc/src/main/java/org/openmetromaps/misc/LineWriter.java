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

package org.openmetromaps.misc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import de.topobyte.webpaths.WebPath;

public abstract class LineWriter
{

	protected Context context;
	protected Path file;
	protected Line line;

	protected WebPath path;
	protected MarkdownWriter output;

	public LineWriter(Context context, Path file, Line line)
	{
		this.context = context;
		this.file = file;
		this.line = line;

		path = context.path(line);
	}

	public abstract void write() throws IOException;

	protected void writeStops(List<Stop> stops) throws IOException
	{
		for (Stop stop : stops) {
			Station station = stop.getStation();
			WebPath relative = path.relativize(context.path(station));
			String link = String.format("[%s](%s)", station.getName(),
					relative.toString());

			List<Line> lines = new ArrayList<>(
					context.getStationToLines().get(station));

			StringBuilder text = new StringBuilder();
			text.append(link);
			text.append(" ");
			lines.remove(line);
			Iterator<Line> iterator = lines.iterator();
			while (iterator.hasNext()) {
				Line other = iterator.next();
				text.append(other.getName());
				if (iterator.hasNext()) {
					text.append(", ");
				}
			}
			output.unordered(text.toString());
		}
	}

}
