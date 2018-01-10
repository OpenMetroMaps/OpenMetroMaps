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
import java.io.InputStream;
import java.nio.file.Path;

import org.openmetromaps.maps.editor.config.PermanentConfigReader;
import org.openmetromaps.maps.editor.config.PermanentConfigWriter;
import org.openmetromaps.maps.editor.config.PermanentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.melon.io.StreamUtil;

public class TestPermanentConfigWriter
{

	final static Logger logger = LoggerFactory
			.getLogger(TestPermanentConfigWriter.class);

	public static void main(String[] args) throws IOException
	{
		PermanentConfiguration configuration = PermanentConfiguration
				.createDefaultConfiguration();

		Path path = TestPaths.PATH_PERMANENT;
		InputStream input = StreamUtil.bufferedInputStream(path);

		try {
			configuration = PermanentConfigReader.read(input);
		} catch (Exception e) {
			logger.debug("exception while reading config: " + e.getMessage());
		}

		input.close();

		PermanentConfigWriter.write(configuration, System.out);
	}

}
