// Copyright 2026 Sebastian Kuerten
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

package org.openmetromaps.swing.config.edit;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openmetromaps.swing.config.Configuration;

import de.topobyte.swing.util.BorderHelper;

public class EditorOptionsPane extends JPanel
{

	private static final long serialVersionUID = 1L;

	public EditorOptionsPane(Configuration configuration)
	{
		super(new BorderLayout());
		BorderHelper.addEmptyBorder(this, 10, 10, 10, 10);
		add(new JLabel("No editor-specific options yet."), BorderLayout.NORTH);
	}

	public void setValues(Configuration configuration)
	{
		// no editor-specific options yet
	}

}
