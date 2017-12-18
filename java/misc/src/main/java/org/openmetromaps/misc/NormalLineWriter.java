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
import java.util.List;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import com.google.common.collect.Lists;

import de.topobyte.collections.util.ListUtil;
import de.topobyte.webpaths.WebPath;

public class NormalLineWriter
{

	private Context context;
	private Path file;
	private Line line;

	private WebPath path;
	private MarkdownWriter output;

	public NormalLineWriter(Context context, Path file, Line line)
	{
		this.context = context;
		this.file = file;
		this.line = line;

		path = context.path(line);
	}

	public void write() throws IOException
	{
		output = new MarkdownWriter(file);

		Stop firstStop = line.getStops().get(0);
		Stop lastStop = ListUtil.last(line.getStops());

		Station first = firstStop.getStation();
		Station last = lastStop.getStation();

		output.heading(1, line.getName() + " → " + last.getName());
		writeStops(line.getStops());

		output.newLine();

		output.heading(1, line.getName() + " → " + first.getName());
		writeStops(Lists.reverse(line.getStops()));

		output.close();
	}

	private void writeStops(List<Stop> stops) throws IOException
	{
		for (Stop stop : stops) {
			Station station = stop.getStation();
			WebPath relative = path.relativize(context.path(station));
			String link = String.format("[%s](%s)", station.getName(),
					relative.toString());
			output.unordered(link);
		}
	}

}
