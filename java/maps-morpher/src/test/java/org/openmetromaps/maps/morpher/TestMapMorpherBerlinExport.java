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

package org.openmetromaps.maps.morpher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.openmetromaps.desktop.DesktopUtil;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.image.ImageUtil;

import de.topobyte.system.utils.SystemPaths;

public class TestMapMorpherBerlinExport
{

	public static void main(String[] args) throws Exception
	{
		Path berlin = SystemPaths.HOME
				.resolve("github/OpenMetroMapsData/berlin");

		MapModel geographic = DesktopUtil
				.load(berlin.resolve("geographic.omm"));
		MapModel schematic = DesktopUtil.load(berlin.resolve("schematic.omm"));

		int before = 25;
		int num = 150;
		int after = 50;
		int total = before + num + after;

		int width = 1440;
		int height = 1080;
		int x = -120;
		int y = -180;
		double zoom = 2.0;

		StationMode stationMode = StationMode.CONVEX;
		SegmentMode segmentMode = SegmentMode.CURVE;

		Path dir = Paths.get("/tmp/video");
		Files.createDirectories(dir);

		String pattern = "%04d.png";

		String filenameFirst = String.format(pattern, 1);
		String filenameLast = String.format(pattern, total);

		Path fileFirst = dir.resolve(filenameFirst);
		Path fileLast = dir.resolve(filenameLast);

		System.out.println("creating first image: " + fileFirst);
		{
			MapModel model = MapMorphing.deriveModel(geographic, schematic, 0);
			ImageUtil.createPng(model, fileFirst, width, height, x, y, zoom,
					stationMode, segmentMode);
		}

		System.out.println("creating last image: " + fileLast);
		{
			MapModel model = MapMorphing.deriveModel(geographic, schematic, 1);
			ImageUtil.createPng(model, fileLast, width, height, x, y, zoom,
					stationMode, segmentMode);
		}

		for (int i = 1; i < before; i++) {
			int n = i + 1;

			String filename = String.format(pattern, n);
			Path file = dir.resolve(filename);
			System.out.println("Copy first to: " + file);

			Files.copy(fileFirst, file, StandardCopyOption.REPLACE_EXISTING);
		}

		for (int i = 1; i < after; i++) {
			int n = before + num + i;

			String filename = String.format(pattern, n);
			Path file = dir.resolve(filename);
			System.out.println("Copy last to: " + file);

			Files.copy(fileLast, file, StandardCopyOption.REPLACE_EXISTING);
		}

		for (int i = 1; i <= num; i++) {
			int n = before + i;

			String filename = String.format(pattern, n);
			Path file = dir.resolve(filename);

			double relative = (i - 1) / (double) (num - 1);
			System.out.println(String.format("%.2f: %s", relative, file));

			MapModel model = MapMorphing.deriveModel(geographic, schematic,
					relative);
			ImageUtil.createPng(model, file, width, height, x, y, zoom,
					stationMode, segmentMode);
		}
	}

}
