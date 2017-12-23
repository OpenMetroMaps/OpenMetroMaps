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

package org.openmetromaps.model.osm.inspector.about;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.openmetromaps.swing.AboutPanel;
import org.openmetromaps.swing.LicensePanel;
import org.openmetromaps.swing.Util;

public class AboutDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public static enum Page {
		ABOUT,
		LGPL,
		GPL
	}

	private AboutPanel aboutPanel;
	private LicensePanel lgplPanel;
	private LicensePanel gplPanel;

	public AboutDialog(Window owner, Page page)
	{
		super(owner, "OpenMetroMaps Model Inspector");

		aboutPanel = new AboutPanel("res/about.html");
		lgplPanel = new LicensePanel("res/lgpl.html");
		gplPanel = new LicensePanel("res/gpl.html");

		JTabbedPane tabs = new JTabbedPane();
		tabs.add("About", aboutPanel);
		tabs.add("LGPL", lgplPanel);
		tabs.add("GPL", gplPanel);

		if (page == Page.ABOUT) {
			tabs.setSelectedIndex(0);
		} else if (page == Page.LGPL) {
			tabs.setSelectedIndex(1);
		} else if (page == Page.GPL) {
			tabs.setSelectedIndex(2);
		}

		setContentPane(tabs);
	}

	public static void showDialog(Window owner, Page page)
	{
		AboutDialog dialog = new AboutDialog(owner, page);
		Util.showRelativeToOwner(dialog, 400, 400);
	}

}
