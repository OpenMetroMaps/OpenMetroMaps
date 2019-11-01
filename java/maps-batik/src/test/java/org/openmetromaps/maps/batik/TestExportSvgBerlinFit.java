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
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;

import de.topobyte.system.utils.SystemPaths;
import de.topobyte.viewports.geometry.Rectangle;

public class TestExportSvgBerlinFit
{

	public static void main(String[] args) throws Exception
	{
		Path berlin = SystemPaths.HOME
				.resolve("github/OpenMetroMapsData/berlin");

		MapModel schematic = DesktopUtil.load(berlin.resolve("schematic.omm"));

		MapView view = schematic.getViews().get(0);
		Rectangle scene = view.getConfig().getScene();

		double width = scene.getWidth();
		double height = scene.getHeight();

		double x = 0;
		double y = 0;
		double zoom = 3;

		StationMode stationMode = StationMode.CONVEX;
		SegmentMode segmentMode = SegmentMode.CURVE;

		BatikImageUtil.createImage(schematic,
				Paths.get("/tmp/schematic-fit.svg"),
				(int) Math.ceil(width * zoom), (int) Math.ceil(height * zoom),
				x, y, zoom, stationMode, segmentMode);
	}

}
