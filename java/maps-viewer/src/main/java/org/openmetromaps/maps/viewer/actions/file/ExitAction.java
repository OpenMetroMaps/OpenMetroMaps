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

package org.openmetromaps.maps.viewer.actions.file;

import java.awt.event.ActionEvent;

import org.openmetromaps.maps.viewer.MapViewer;
import org.openmetromaps.maps.viewer.actions.MapViewerAction;

public class ExitAction extends MapViewerAction
{

	private static final long serialVersionUID = 1L;

	public ExitAction(MapViewer mapViewer)
	{
		super(mapViewer, "Exit", "Quit the application");
		setIcon("res/images/24/gtk-quit.png");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		mapViewer.showReallyExitDialog();
	}

}
