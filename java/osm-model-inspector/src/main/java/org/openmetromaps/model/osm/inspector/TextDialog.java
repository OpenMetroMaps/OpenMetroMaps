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

package org.openmetromaps.model.osm.inspector;

import java.awt.Window;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.google.common.base.Joiner;

public class TextDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public TextDialog(Window window, String title, String text)
	{
		super(window);

		setTitle(title);
		JTextArea textField = new JTextArea(text);
		JScrollPane jsp = new JScrollPane(textField);
		setContentPane(jsp);
	}

	public TextDialog(Window window, String title, List<String> lines)
	{
		this(window, title, Joiner.on("\n").join(lines));
	}

}
