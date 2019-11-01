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

package org.openmetromaps.maps.batik;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.openmetromaps.desktop.DesktopUtil;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;

import de.topobyte.system.utils.SystemPaths;

public class TestExportSvgBerlin
{

	public static void main(String[] args) throws Exception
	{
		Path berlin = SystemPaths.HOME
				.resolve("github/OpenMetroMapsData/berlin");

		MapModel geographic = DesktopUtil
				.load(berlin.resolve("geographic.omm"));
		MapModel schematic = DesktopUtil.load(berlin.resolve("schematic.omm"));

		int width = 1440;
		int height = 1080;
		int x = -70;
		int y = -150;
		double zoom = 2;

		StationMode stationMode = StationMode.CONVEX;
		SegmentMode segmentMode = SegmentMode.CURVE;

		BatikImageUtil.createImage(geographic, Paths.get("/tmp/geographic.svg"),
				width, height, x, y, zoom, stationMode, segmentMode);
		BatikImageUtil.createImage(schematic, Paths.get("/tmp/schematic.svg"),
				width, height, x, y, zoom, stationMode, segmentMode);
	}

}
