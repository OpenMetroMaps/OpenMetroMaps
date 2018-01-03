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

package org.openmetromaps.markdownview;

import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;

import com.google.common.collect.Multimap;

import de.topobyte.webpaths.WebPath;
import de.topobyte.webpaths.WebPaths;

public class Context
{

	private final WebPath subpathLines = WebPaths.get("lines/");
	private final WebPath subpathStations = WebPaths.get("stations/");

	private Multimap<Station, Line> stationToLines;
	private LineNetwork lineNetwork;

	public Context(Multimap<Station, Line> stationToLines,
			LineNetwork lineNetwork)
	{
		this.stationToLines = stationToLines;
		this.lineNetwork = lineNetwork;
	}

	public WebPath getSubpathLines()
	{
		return subpathLines;
	}

	public WebPath getSubpathStations()
	{
		return subpathStations;
	}

	public WebPath path(Line line)
	{
		return subpathLines.resolve(sane(line.getName()) + ".md");
	}

	public WebPath path(Station station)
	{
		return subpathStations.resolve(sane(station.getName()) + ".md");
	}

	private String sane(String name)
	{
		return name.replaceAll("/", "-").replaceAll(" ", "-");
	}

	public Multimap<Station, Line> getStationToLines()
	{
		return stationToLines;
	}

	public LineNetwork getLineNetwork()
	{
		return lineNetwork;
	}

}
