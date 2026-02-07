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

package org.openmetromaps.swing.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.system.utils.SystemPaths;

public class ConfigurationStorage
{

	final static Logger logger = LoggerFactory
			.getLogger(ConfigurationStorage.class);

	private static final Path OPENMETROMAPS_DIR = SystemPaths.HOME
			.resolve(".config").resolve("openmetromaps");

	public static Path getUserConfigurationFilePath()
	{
		return OPENMETROMAPS_DIR.resolve("openmetromaps-config.xml");
	}

	public static Path getUserVolatileFilePath()
	{
		return OPENMETROMAPS_DIR.resolve("openmetromaps-volatile.xml");
	}

	public static Configuration loadConfiguration()
	{
		Path path = getUserConfigurationFilePath();
		if (!Files.exists(path)) {
			return Configuration.createDefaultConfiguration();
		}
		try (InputStream input = Files.newInputStream(path)) {
			return ConfigurationReader.read(input);
		} catch (Exception e) {
			logger.warn("Error while reading configuration", e);
			return Configuration.createDefaultConfiguration();
		}
	}

	public static VolatileConfiguration loadVolatileConfiguration()
	{
		Path path = getUserVolatileFilePath();
		if (!Files.exists(path)) {
			return VolatileConfiguration.createDefaultConfiguration();
		}
		try (InputStream input = Files.newInputStream(path)) {
			return VolatileConfigurationReader.read(input);
		} catch (Exception e) {
			logger.warn("Error while reading volatile configuration", e);
			return VolatileConfiguration.createDefaultConfiguration();
		}
	}

	public static void store(Configuration config) throws IOException
	{
		Path path = getUserConfigurationFilePath();
		Files.createDirectories(path.getParent());
		try (OutputStream out = Files.newOutputStream(path)) {
			ConfigurationWriter.write(config, out);
		}
	}

	public static void store(VolatileConfiguration config) throws IOException
	{
		Path path = getUserVolatileFilePath();
		Files.createDirectories(path.getParent());
		try (OutputStream out = Files.newOutputStream(path)) {
			VolatileConfigurationWriter.write(config, out);
		}
	}

}
