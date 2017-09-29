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

package org.openmetromaps.maps.graph;

import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.model.Station;

import de.topobyte.adt.geo.Coordinate;

public class Node
{

	public Coordinate location;
	public Station station;
	public List<Edge> edges = new ArrayList<>();

	public boolean isLastStopOfALine;
	public int rank;

	public Node(Station station)
	{
		this.station = station;
		location = station.getLocation();
	}

	public void setIsLastStopOfALine(boolean isLastStop)
	{
		this.isLastStopOfALine = isLastStop;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

}
