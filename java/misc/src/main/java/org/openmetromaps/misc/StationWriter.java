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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openmetromaps.maps.xml.XmlLine;
import org.openmetromaps.maps.xml.XmlStation;

import com.google.common.collect.Multimap;

public class StationWriter
{

	private Path file;
	private XmlStation station;
	private Multimap<XmlStation, XmlLine> stationToLines;

	public StationWriter(Path file, XmlStation station,
			Multimap<XmlStation, XmlLine> stationToLines)
	{
		this.file = file;
		this.station = station;
		this.stationToLines = stationToLines;
	}

	public void write() throws IOException
	{
		MarkdownWriter output = new MarkdownWriter(file);

		output.heading(1, station.getName());

		List<XmlLine> lines = new ArrayList<>(stationToLines.get(station));
		Collections.sort(lines, new Comparator<XmlLine>() {

			@Override
			public int compare(XmlLine o1, XmlLine o2)
			{
				String name1 = o1.getName();
				String name2 = o2.getName();
				return name1.compareTo(name2);
			}

		});
		for (XmlLine line : lines) {
			// TODO: make links
			output.unordered(line.getName());
		}

		output.close();
	}

}
