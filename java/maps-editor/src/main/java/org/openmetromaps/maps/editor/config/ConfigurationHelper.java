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

package org.openmetromaps.maps.editor.config;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import de.topobyte.melon.io.StreamUtil;
import de.topobyte.system.utils.SystemPaths;

public class ConfigurationHelper
{

	private static Path openmetromaps = SystemPaths.HOME.resolve(".config")
			.resolve("openmetromaps");

	public static Path getUserConfigurationFilePath()
	{
		return openmetromaps.resolve("map-editor-config.xml");
	}

	public static Path getUserVolatileFilePath()
	{
		return openmetromaps.resolve("map-editor-volatile.xml");
	}

	public static void store(PermanentConfiguration config) throws IOException
	{
		Path path = getUserConfigurationFilePath();
		OutputStream out = StreamUtil.bufferedOutputStream(path);
		PermanentConfigWriter.write(config, out);
		out.close();
	}

	public static void store(VolatileConfiguration config) throws IOException
	{
		Path path = ConfigurationHelper.getUserVolatileFilePath();
		OutputStream out = StreamUtil.bufferedOutputStream(path);
		VolatileConfigWriter.write(config, out);
		out.close();
	}

}
