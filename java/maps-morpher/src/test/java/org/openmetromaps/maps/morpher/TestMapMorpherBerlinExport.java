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

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.image.ImageUtil;

import de.topobyte.system.utils.SystemPaths;

public class TestMapMorpherBerlinExport
{

	public static void main(String[] args) throws Exception
	{
		Path berlin = SystemPaths.HOME
				.resolve("github/OpenMetroMapsData/berlin");

		MapModel geographic = Util.load(berlin.resolve("geographic.xml"));
		MapModel schematic = Util.load(berlin.resolve("schematic.xml"));

		int num = 150;
		int width = 1440;
		int height = 1080;
		int x = 240;
		int y = 80;
		double zoom = 2.0;

		Path dir = Paths.get("/tmp/video");
		Files.createDirectories(dir);

		for (int i = 1; i <= num; i++) {
			String filename = String.format("%04d.png", i);
			Path file = dir.resolve(filename);

			double relative = (i - 1) / (double) (num - 1);
			System.out.println(String.format("%.2f: %s", relative, filename));

			MapModel model = MapMorphing.deriveModel(geographic, schematic,
					relative);
			ImageUtil.createPng(model, file, width, height, x, y, zoom);
		}
	}

}
