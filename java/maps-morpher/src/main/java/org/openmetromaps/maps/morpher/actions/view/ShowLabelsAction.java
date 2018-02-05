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

package org.openmetromaps.maps.morpher.actions.view;

import org.openmetromaps.maps.morpher.MapMorpher;
import org.openmetromaps.maps.morpher.actions.MapMorpherBooleanAction;

import de.topobyte.swing.util.EmptyIcon;

public class ShowLabelsAction extends MapMorpherBooleanAction
{

	private static final long serialVersionUID = 1L;

	public ShowLabelsAction(MapMorpher mapMorpher)
	{
		super(mapMorpher, "Show labels", "Toggle map label visibility");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public boolean getState()
	{
		return mapMorpher.isShowLabels();
	}

	@Override
	public void toggleState()
	{
		mapMorpher.setShowLabels(!mapMorpher.isShowLabels());
	}

}
