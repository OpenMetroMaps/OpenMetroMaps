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

package org.openmetromaps.maps.actions;

import org.openmetromaps.maps.MapViewer;
import org.openmetromaps.maps.PlanRenderer;
import org.openmetromaps.maps.ScrollableAdvancedPanel;

import de.topobyte.swing.util.EmptyIcon;
import de.topobyte.swing.util.action.SimpleBooleanAction;

public class DebugTangentsAction extends SimpleBooleanAction
{

	private static final long serialVersionUID = 1L;

	private MapViewer mapViewer;

	public DebugTangentsAction(MapViewer mapViewer)
	{
		super("Debug tangents", "Toggle edge tangent visibility");
		this.mapViewer = mapViewer;
		setIcon(new EmptyIcon(24));
	}

	@Override
	public boolean getState()
	{
		return mapViewer.getMap().getPlanRenderer().isDebugTangents();
	}

	@Override
	public void toggleState()
	{
		ScrollableAdvancedPanel map = mapViewer.getMap();
		PlanRenderer planRenderer = map.getPlanRenderer();
		planRenderer.setDebugTangents(!planRenderer.isDebugTangents());
		map.repaint();
	}

}
