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
import java.util.Iterator;
import java.util.List;

import org.openmetromaps.heavyutil.HeavyUtil;
import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import de.topobyte.webpaths.WebPath;

public abstract class LineWriter
{

	protected Context context;
	protected Path file;
	protected NetworkLine line;

	protected WebPath path;
	protected MarkdownWriter output;

	public LineWriter(Context context, Path file, NetworkLine line)
	{
		this.context = context;
		this.file = file;
		this.line = line;

		path = context.path(line.line);
	}

	public abstract void write() throws IOException;

	protected void writeStops(List<Stop> stops) throws IOException
	{
		for (Stop stop : stops) {
			Station station = stop.getStation();
			Node node = context.getLineNetwork().getStationToNode()
					.get(station);
			WebPath relStation = path.relativize(context.path(station));
			String linkStation = String.format("[%s](%s)", station.getName(),
					relStation.toString());

			StringBuilder text = new StringBuilder();
			text.append(linkStation);

			List<Line> lines = HeavyUtil.determineInterestingLines(
					context.getStationToLines(), line, node);
			MapModelUtil.sortLinesByName(lines);

			if (!lines.isEmpty()) {
				text.append(" ");
				text.append("(");
				otherLines(text, lines);
				text.append(")");
			}

			output.unordered(text.toString());
		}
	}

	private void otherLines(StringBuilder text, List<Line> lines)
	{
		Iterator<Line> iterator = lines.iterator();
		while (iterator.hasNext()) {
			Line other = iterator.next();
			WebPath relOther = path.relativize(context.path(other));
			String linkOther = String.format("[%s](%s)", other.getName(),
					relOther.toString());
			text.append(linkOther);
			if (iterator.hasNext()) {
				text.append(", ");
			}
		}
	}

}
