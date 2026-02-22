// Copyright 2026 Sebastian Kuerten
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

package org.openmetromaps.maps.rendering.components;

import java.util.List;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintFactory;

public class PaintInfoPerLine
{

	public static interface Initializer
	{
		public IPaintInfo init(PaintFactory pf, Line line);
	}

	private PaintFactory pf;
	private IPaintInfo[] lineToPaintForLines;
	private List<Line> lines;
	private Initializer initializer;

	public PaintInfoPerLine(PaintFactory pf, List<Line> lines,
			Initializer initializer)
	{
		this.pf = pf;
		this.lines = lines;
		this.initializer = initializer;

		init();
	}

	private void init()
	{
		lineToPaintForLines = new IPaintInfo[lines.size()];
		for (Line line : lines) {
			lineToPaintForLines[line.getId()] = initializer.init(pf, line);
		}
	}

	public IPaintInfo get(Line line)
	{
		return lineToPaintForLines[line.getId()];
	}

}
