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

package de.topobyte.swing.util;

import java.awt.Component;
import java.awt.Graphics;
import java.util.function.BooleanSupplier;

import javax.swing.Icon;

public class ToggleIcon implements Icon
{
	private final Icon unchecked;
	private final Icon checked;
	private final BooleanSupplier isChecked;

	public ToggleIcon(Icon unchecked, Icon checked, BooleanSupplier isChecked)
	{
		this.unchecked = unchecked;
		this.checked = checked;
		this.isChecked = isChecked;
	}

	@Override
	public int getIconWidth()
	{
		return Math.max(unchecked.getIconWidth(), checked.getIconWidth());
	}

	@Override
	public int getIconHeight()
	{
		return Math.max(unchecked.getIconHeight(), checked.getIconHeight());
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		(isChecked.getAsBoolean() ? checked : unchecked).paintIcon(c, g, x, y);
	}

}
