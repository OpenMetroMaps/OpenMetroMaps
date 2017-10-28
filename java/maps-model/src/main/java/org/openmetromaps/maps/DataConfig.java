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

package org.openmetromaps.maps;

import org.openmetromaps.maps.model.BBox;
import org.openmetromaps.maps.model.Coordinate;

public class DataConfig
{

	private BBox bbox;
	private Coordinate startPosition;

	public DataConfig(BBox bbox, Coordinate startPosition)
	{
		this.bbox = bbox;
		this.startPosition = startPosition;
	}

	public BBox getBbox()
	{
		return bbox;
	}

	public void setBbox(BBox bbox)
	{
		this.bbox = bbox;
	}

	public Coordinate getStartPosition()
	{
		return startPosition;
	}

	public void setStartPosition(Coordinate startPosition)
	{
		this.startPosition = startPosition;
	}

}
