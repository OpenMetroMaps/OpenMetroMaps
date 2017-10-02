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

import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.model.ModelData;

public class MapModel
{

	private ModelData data;
	private List<MapView> views;

	public MapModel(ModelData data)
	{
		this.data = data;
		views = new ArrayList<>();
	}

	public ModelData getData()
	{
		return data;
	}

	public void setData(ModelData data)
	{
		this.data = data;
	}

	public List<MapView> getViews()
	{
		return views;
	}

	public void setViews(List<MapView> views)
	{
		this.views = views;
	}

}
