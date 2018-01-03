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

import org.openmetromaps.maps.graph.NetworkLine;

public class CircularLineWriter extends LineWriter
{

	public CircularLineWriter(Context context, Path file, NetworkLine line)
	{
		super(context, file, line);
	}

	@Override
	public void write() throws IOException
	{
		output = new MarkdownWriter(file);

		writeStops(line.line.getStops());

		output.close();
	}

}
