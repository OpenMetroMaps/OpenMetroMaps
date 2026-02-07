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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;
import org.openmetromaps.swing.Theme;

public class TestPermanentConfigReader
{

	@Test
	public void darkThemeAndFlat() throws Exception
	{
		String configContent = """
				<configuration>
				  <option name="theme" value="FLATLAF_DARK"/>
				  <option name="docking-frames-theme" value="flat"/>
				</configuration>""";
		try (InputStream input = new ByteArrayInputStream(
				configContent.getBytes(StandardCharsets.UTF_8));) {
			Configuration config = ConfigurationReader.read(input);
			Assert.assertEquals(Theme.FLATLAF_DARK, config.getTheme());
			Assert.assertEquals("flat", config.getDockingFramesTheme());
		}
	}

	@Test
	public void invalidThemeValueIsLookAndFeelClassName() throws Exception
	{
		String configContent = """
				<configuration>
				  <option name="theme" value="javax.swing.plaf.metal.MetalLookAndFeel"/>
				  <option name="docking-frames-theme" value="basic"/>
				</configuration>""";
		try (InputStream input = new ByteArrayInputStream(
				configContent.getBytes(StandardCharsets.UTF_8));) {
			Configuration config = ConfigurationReader.read(input);
			Assert.assertEquals(Theme.FLATLAF_LIGHT, config.getTheme());
			Assert.assertEquals("basic", config.getDockingFramesTheme());
		}
	}

}
