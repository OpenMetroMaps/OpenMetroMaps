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

import java.util.ArrayList;
import java.util.List;

public class XmlEdges
{

	private String line;
	private List<XmlInterval> intervals = new ArrayList<>();

	public XmlEdges(String line)
	{
		this.line = line;
	}

	public String getName()
	{
		return line;
	}

	public void setName(String name)
	{
		this.line = name;
	}

	public void addInterval(XmlInterval interval)
	{
		intervals.add(interval);
	}

	public List<XmlInterval> getIntervals()
	{
		return intervals;
	}

}
