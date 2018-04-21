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

package org.openmetromaps.mobidig.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.mobidig.MapViewer;
import org.openmetromaps.mobidig.Util;

public class AufzugViewer
{

	public static void main(String[] args) throws Exception
	{
		MapModel model = Util.stuttgartSchematic();

		Map<String, ColorCode> colorMap = new HashMap<>();

		List<String> elevators = Util.lines("aufzug.txt");
		for (String line : elevators) {
			colorMap.put(line, DemoOptions.GREEN);
		}
		List<String> broken = Util.lines("aufzug-kaputt.txt");
		for (String line : broken) {
			colorMap.put(line, DemoOptions.RED);
		}

		MapViewer mapViewer = new MapViewer(model, null, colorMap,
				"Stuttgart S-Bahn", "Fahrstuhlinformation");
		mapViewer.show();
	}

}
