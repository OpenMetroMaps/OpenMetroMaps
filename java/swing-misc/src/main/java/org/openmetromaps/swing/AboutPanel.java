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

package org.openmetromaps.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.melon.resources.Resources;

public class AboutPanel extends JPanel
{

	final static Logger logger = LoggerFactory.getLogger(AboutPanel.class);

	static final long serialVersionUID = 1L;

	public AboutPanel(String filename)
	{
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		JScrollPane jsp = new JScrollPane();
		add(jsp, c);

		JEditorPane pane = new JEditorPane();
		jsp.setViewportView(pane);
		pane.setEditable(false);

		HTMLEditorKit kit = new HTMLEditorKit();
		pane.setEditorKit(kit);

		URL url = Resources.url(filename);
		try {
			logger.debug("url: " + url);
			pane.setPage(url);
		} catch (IOException e) {
			logger.debug("unable to set page: " + e.getMessage());
		}
	}

}
