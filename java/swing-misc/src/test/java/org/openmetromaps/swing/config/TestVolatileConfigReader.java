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
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class TestVolatileConfigReader
{

	@Test
	public void defaultConfigHasNoLastUsedDirectory() throws Exception
	{
		String configContent = """
				<?xml version="1.0" encoding="UTF-8" standalone="no"?>
				<configuration/>
				""";
		try (InputStream input = new ByteArrayInputStream(
				configContent.getBytes(StandardCharsets.UTF_8));) {
			VolatileConfiguration config = VolatileConfigurationReader
					.read(input);
			Assert.assertNull(config.getLastUsedDirectory());
		}
	}

	@Test
	public void validLastUsedDirectory() throws Exception
	{
		String configContent = """
				<?xml version="1.0" encoding="UTF-8" standalone="no"?>
				<configuration>
				  <option name="last-used-directory" value="/tmp/foo"/>
				</configuration>
				""";
		try (InputStream input = new ByteArrayInputStream(
				configContent.getBytes(StandardCharsets.UTF_8));) {
			VolatileConfiguration config = VolatileConfigurationReader
					.read(input);
			Assert.assertEquals(Paths.get("/tmp/foo"),
					config.getLastUsedDirectory());
		}
	}

}
