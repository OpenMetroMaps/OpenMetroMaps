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

public class XmlView
{

	private String name;
	private double sceneWidth;
	private double sceneHeight;
	private double startX;
	private double startY;
	private List<XmlViewStation> stations = new ArrayList<>();
	private List<XmlEdges> edges = new ArrayList<>();

	public XmlView(String name, double sceneWidth, double sceneHeight,
			double startX, double startY)
	{
		this.name = name;
		this.sceneWidth = sceneWidth;
		this.sceneHeight = sceneHeight;
		this.startX = startX;
		this.startY = startY;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public double getSceneWidth()
	{
		return sceneWidth;
	}

	public void setSceneWidth(double sceneWidth)
	{
		this.sceneWidth = sceneWidth;
	}

	public double getSceneHeight()
	{
		return sceneHeight;
	}

	public void setSceneHeight(double sceneHeight)
	{
		this.sceneHeight = sceneHeight;
	}

	public double getStartX()
	{
		return startX;
	}

	public void setStartX(double startX)
	{
		this.startX = startX;
	}

	public double getStartY()
	{
		return startY;
	}

	public void setStartY(double startY)
	{
		this.startY = startY;
	}

	public List<XmlViewStation> getStations()
	{
		return stations;
	}

	public void setStations(List<XmlViewStation> stations)
	{
		this.stations = stations;
	}

	public List<XmlEdges> getEdges()
	{
		return edges;
	}

	public void setEdges(List<XmlEdges> edges)
	{
		this.edges = edges;
	}

}
