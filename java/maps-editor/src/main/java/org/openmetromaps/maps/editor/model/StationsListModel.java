// Copyright 2026 Sebastian Kuerten
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

package org.openmetromaps.maps.editor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import org.openmetromaps.maps.model.Stop;

public class StationsListModel extends AbstractListModel<Stop>
{

	private static final long serialVersionUID = 1L;

	private List<Stop> stops;

	public StationsListModel(List<Stop> stops)
	{
		this.stops = new ArrayList<>(stops);
	}

	@Override
	public int getSize()
	{
		return stops.size();
	}

	@Override
	public Stop getElementAt(int index)
	{
		return stops.get(index);
	}

	public List<Stop> getStops()
	{
		return stops;
	}

	public void moveUp(int index)
	{
		if (index <= 0 || index >= stops.size()) {
			return;
		}
		Collections.swap(stops, index, index - 1);
		fireContentsChanged(this, index - 1, index);
	}

	public void moveDown(int index)
	{
		if (index < 0 || index >= stops.size() - 1) {
			return;
		}
		Collections.swap(stops, index, index + 1);
		fireContentsChanged(this, index, index + 1);
	}

	public void addFirst(Stop stop)
	{
		stops.add(0, stop);
		fireIntervalAdded(this, 0, 0);
	}

	public void addLast(Stop stop)
	{
		int index = stops.size();
		stops.add(stop);
		fireIntervalAdded(this, index, index);
	}

	public void remove(int index)
	{
		if (index < 0 || index >= stops.size()) {
			return;
		}
		stops.remove(index);
		fireIntervalRemoved(this, index, index);
	}

}
