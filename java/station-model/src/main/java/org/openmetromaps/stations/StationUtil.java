// Copyright 2018 Sebastian Kuerten
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

package org.openmetromaps.stations;

import java.util.List;

import org.openmetromaps.maps.model.Line;

public class StationUtil
{

	public static Line findLine(List<Line> lines, String lineName)
	{
		// TODO: this is inefficient, replace with map from line names to lines
		for (Line line : lines) {
			if (line.getName().equals(lineName)) {
				return line;
			}
		}
		return null;
	}

}
