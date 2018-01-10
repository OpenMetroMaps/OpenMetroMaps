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

package org.openmetromaps.maps.editor.model;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.openmetromaps.maps.model.Line;

public class LinesCellRenderer extends JLabel implements ListCellRenderer<Line>
{

	private static final long serialVersionUID = 1L;

	public LinesCellRenderer()
	{
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Line> list,
			Line line, int index, boolean isSelected, boolean cellHasFocus)
	{
		String name = line.getName();
		int numStops = line.getStops().size();

		setText(String.format("%s: %d", name, numStops));

		setBackground(list, isSelected);

		return this;
	}

	protected void setBackground(JList<?> list, boolean isSelected)
	{
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
	}

}
