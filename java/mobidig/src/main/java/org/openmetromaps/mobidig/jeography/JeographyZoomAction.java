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

package org.openmetromaps.mobidig.jeography;

import java.awt.event.ActionEvent;

import de.topobyte.jeography.viewer.core.Viewer;
import de.topobyte.swing.util.action.SimpleAction;
import de.topobyte.viewports.scrolling.ZoomAction;
import de.topobyte.viewports.scrolling.ZoomAction.Type;

public class JeographyZoomAction extends SimpleAction
{

	private static final long serialVersionUID = 1L;

	private Viewer viewer;
	private Type type;

	public JeographyZoomAction(Viewer viewer, ZoomAction.Type type)
	{
		this.viewer = viewer;
		this.type = type;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (type) {
		case IDENTITY:
			viewer.getMapWindow().zoom(13);
			break;
		case IN:
			viewer.getMapWindow().zoomIn();
			break;
		case OUT:
			viewer.getMapWindow().zoomOut();
			break;
		default:
			break;
		}
		viewer.repaint();
	}

}
