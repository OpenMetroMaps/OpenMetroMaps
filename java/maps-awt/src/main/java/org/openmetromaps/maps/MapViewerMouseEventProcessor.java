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

import java.awt.event.MouseEvent;

import org.openmetromaps.swing.Util;

public class MapViewerMouseEventProcessor extends BaseMouseEventProcessor
{

	private MapViewer mapViewer;

	public MapViewerMouseEventProcessor(MapViewer mapViewer)
	{
		super(mapViewer.getMap(), mapViewer.getMap().getMapWindow());
		this.mapViewer = mapViewer;
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		mapViewer.updateStatusBar(e.getX(), e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		boolean control = Util.isControlPressed(e);

		if (!control) {
			super.mouseDragged(e);
		}
	}

}
