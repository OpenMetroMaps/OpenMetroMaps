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

package org.openmetromaps.maps.viewer.actions.view;

import org.openmetromaps.maps.viewer.MapViewer;
import org.openmetromaps.maps.viewer.actions.MapViewerBooleanAction;

import de.topobyte.swing.util.EmptyIcon;

public class ShowStationCentersAction extends MapViewerBooleanAction
{

	private static final long serialVersionUID = 1L;

	public ShowStationCentersAction(MapViewer mapViewer)
	{
		super(mapViewer, "Show Station Centers",
				"Toggle visibility of station centers");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public boolean getState()
	{
		return mapViewer.isShowStationCenters();
	}

	@Override
	public void toggleState()
	{
		mapViewer.setShowStationCenters(!mapViewer.isShowStationCenters());
	}

}
