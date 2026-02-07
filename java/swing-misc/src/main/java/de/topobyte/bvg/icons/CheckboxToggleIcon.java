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

package de.topobyte.bvg.icons;

import java.util.function.BooleanSupplier;

import de.topobyte.swing.util.ToggleIcon;

public class CheckboxToggleIcon
{

	private static BvgIcon iconEnabled = new BvgIcon(IconResources.CHECKBOX_ON,
			24);
	private static BvgIcon iconDisabled = new BvgIcon(
			IconResources.CHECKBOX_OFF, 24);

	public static ToggleIcon icon(BooleanSupplier isChecked)
	{
		return new ToggleIcon(iconDisabled, iconEnabled, isChecked);
	}

}
