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

package org.openmetromaps.maps.xml;

import java.util.List;

public class XmlModel
{

	private String version;
	private List<XmlStation> stations;
	private List<XmlLine> lines;
	private List<XmlView> xmlViews;

	public XmlModel(String version, List<XmlStation> stations,
			List<XmlLine> lines, List<XmlView> xmlViews)
	{
		this.version = version;
		this.stations = stations;
		this.lines = lines;
		this.xmlViews = xmlViews;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public List<XmlStation> getStations()
	{
		return stations;
	}

	public void setStations(List<XmlStation> stations)
	{
		this.stations = stations;
	}

	public List<XmlLine> getLines()
	{
		return lines;
	}

	public void setLines(List<XmlLine> lines)
	{
		this.lines = lines;
	}

	public List<XmlView> getXmlViews()
	{
		return xmlViews;
	}

	public void setXmlViews(List<XmlView> xmlViews)
	{
		this.xmlViews = xmlViews;
	}

}
