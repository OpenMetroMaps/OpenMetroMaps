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

package org.openmetromaps.swing.config;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;
import org.openmetromaps.swing.Theme;

public class TestPermanentConfigWriter
{

	@Test
	public void writesDefaultConfiguration() throws Exception
	{
		Configuration config = Configuration.createDefaultConfiguration();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ConfigurationWriter.write(config, output);

		String actual = output.toString(StandardCharsets.UTF_8);
		String expected = """
				<?xml version="1.0" encoding="UTF-8" standalone="no"?>
				<configuration>
				  <option name="theme" value="FLATLAF_LIGHT"/>
				  <option name="docking-frames-theme" value="basic"/>
				</configuration>
				""";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void writesConfigurationDarkTheme() throws Exception
	{
		Configuration config = Configuration.createDefaultConfiguration();

		config.setTheme(Theme.FLATLAF_DARK);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ConfigurationWriter.write(config, output);

		String actual = output.toString(StandardCharsets.UTF_8);
		String expected = """
				<?xml version="1.0" encoding="UTF-8" standalone="no"?>
				<configuration>
				  <option name="theme" value="FLATLAF_DARK"/>
				  <option name="docking-frames-theme" value="basic"/>
				</configuration>
				""";
		Assert.assertEquals(expected, actual);
	}

}
