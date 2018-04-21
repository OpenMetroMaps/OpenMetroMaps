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

package org.openmetromaps.mobidig;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.common.base.Strings;

import de.topobyte.awt.util.GridBagConstraintsEditor;

public class StatusBar extends JPanel
{

	private static final long serialVersionUID = -6453279448326406961L;

	private JLabel label;

	public StatusBar()
	{
		setLayout(new GridBagLayout());

		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		label = new JLabel();

		c.fill(GridBagConstraints.BOTH);
		c.weight(1.0, 0).gridPos(0, 0);

		add(label, c.getConstraints());

		setText(null);
	}

	public void setText(String text)
	{
		if (Strings.isNullOrEmpty(text)) {
			// set some invisible text in this case to avoid the status bar
			// collapsing to zero height
			label.setText(" ");
		} else {
			label.setText(text);
		}
	}

}
