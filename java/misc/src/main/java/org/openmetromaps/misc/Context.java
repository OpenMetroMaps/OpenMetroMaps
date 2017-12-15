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

package org.openmetromaps.misc;

import org.openmetromaps.maps.xml.XmlLine;
import org.openmetromaps.maps.xml.XmlStation;

import de.topobyte.webpaths.WebPath;
import de.topobyte.webpaths.WebPaths;

public class Context
{

	private final WebPath subpathLines = WebPaths.get("lines/");
	private final WebPath subpathStations = WebPaths.get("stations/");

	public WebPath getSubpathLines()
	{
		return subpathLines;
	}

	public WebPath getSubpathStations()
	{
		return subpathStations;
	}

	public WebPath path(XmlLine line)
	{
		return subpathLines.resolve(sane(line.getName()) + ".md");
	}

	public WebPath path(XmlStation station)
	{
		return subpathStations.resolve(sane(station.getName()) + ".md");
	}

	private String sane(String name)
	{
		return name.replaceAll("/", "-").replaceAll(" ", "-");
	}

}
