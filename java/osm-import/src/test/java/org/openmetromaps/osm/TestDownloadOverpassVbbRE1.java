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

package org.openmetromaps.osm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class TestDownloadOverpassVbbRE1
{

	public static void main(String[] args) throws MalformedURLException,
			IOException, ParserConfigurationException, TransformerException
	{
		Path pathOutput = Paths.get("/tmp/re1.osm.xml");

		OverpassApiDownloader downloader = new OverpassApiDownloader();
		downloader.execute(OverpassQueries.Q_VBB_RE1_BY_ID, pathOutput);
	}

}
