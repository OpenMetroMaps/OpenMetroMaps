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

package org.openmetromaps.maps.editor.actions.file;

import java.awt.event.ActionEvent;

import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;

public class SettingsAction extends MapEditorAction
{

	private static final long serialVersionUID = 1L;

	public SettingsAction(MapEditor mapEditor)
	{
		super(mapEditor, "Settings", "Edit the application settings");
		setIcon("res/images/24/preferences-desktop.png");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO: implement this
		System.out.println("Not implemented yet");
	}

}
