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

package org.openmetromaps.model.osm.inspector.actions;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import org.openmetromaps.model.osm.inspector.about.AboutDialog;

import de.topobyte.swing.util.action.SimpleAction;

public class LicenseAction extends SimpleAction
{

	private static final long serialVersionUID = 1L;

	private JFrame frame;

	public LicenseAction(JFrame frame)
	{
		super("License", "Show license information about this software");
		this.frame = frame;
		setIcon("res/images/24/help-about.png");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		AboutDialog.showDialog(frame, AboutDialog.Page.LGPL);
	}

}
