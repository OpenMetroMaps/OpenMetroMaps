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

package org.openmetromaps.maps.editor.actions.view;

import javax.swing.Action;

import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorBooleanAction;

import de.topobyte.bvg.icons.BvgIcon;
import de.topobyte.bvg.icons.IconResources;

public class ToggleAntialiasingAction extends MapEditorBooleanAction
{

	private static final long serialVersionUID = 1L;

	private BvgIcon iconEnabled;
	private BvgIcon iconDisabled;

	public ToggleAntialiasingAction(MapEditor mapEditor)
	{
		super(mapEditor, "Antialiasing", "Toggle antialiasing on the map");
		iconEnabled = new BvgIcon(IconResources.ANTIALIASING_ON, 24);
		iconDisabled = new BvgIcon(IconResources.ANTIALIASING_OFF, 24);
	}

	@Override
	public Object getValue(String key)
	{
		if (key.equals(Action.SMALL_ICON)) {
			return getState() ? iconEnabled : iconDisabled;
		}
		return super.getValue(key);
	}

	@Override
	public boolean getState()
	{
		return mapEditor.isAntialiasing();
	}

	@Override
	public void toggleState()
	{
		mapEditor.setAntialiasing(!mapEditor.isAntialiasing());
		firePropertyChange(Action.SMALL_ICON, null,
				getValue(Action.SMALL_ICON));
	}

}
