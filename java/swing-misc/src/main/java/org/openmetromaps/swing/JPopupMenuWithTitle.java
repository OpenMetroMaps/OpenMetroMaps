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

package org.openmetromaps.swing;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

public class JPopupMenuWithTitle extends JPopupMenu
{

	private static final long serialVersionUID = 1L;

	private final JLabel label;

	public JPopupMenuWithTitle(String title)
	{
		label = new JLabel(title);
		label.setHorizontalAlignment(SwingConstants.LEFT);

		Font originalFont = label.getFont();
		Font font = originalFont.deriveFont(Font.BOLD)
				.deriveFont((float) (originalFont.getSize() + 2));
		label.setFont(font);

		label.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));

		add(label);
		addSeparator();
	}

	@Override
	public void setLabel(String title)
	{
		label.setText(title);
	}

	@Override
	public String getLabel()
	{
		return label.getText();
	}

}
