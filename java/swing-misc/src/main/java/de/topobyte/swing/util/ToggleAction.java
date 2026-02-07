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

import javax.swing.Action;
import javax.swing.Icon;

import de.topobyte.swing.util.action.SimpleBooleanAction;
import de.topobyte.swing.util.action.enums.BooleanValueHolder;

public class ToggleAction extends SimpleBooleanAction
{

	private static final long serialVersionUID = 1L;

	private BooleanValueHolder valueHolder;

	public ToggleAction(String name, String description,
			BooleanValueHolder valueHolder, Icon uncheckedIcon,
			Icon checkedIcon)
	{
		super(name, description);
		this.valueHolder = valueHolder;
		setIcon(new ToggleIcon(uncheckedIcon, checkedIcon, () -> getState()));
		valueHolder.addPropertyChangeListener(evt -> {
			firePropertyChange(Action.SELECTED_KEY, null, getState());
			firePropertyChange(Action.SMALL_ICON, null, null);
		});
	}

	@Override
	public boolean getState()
	{
		return valueHolder.getValue();
	}

	@Override
	public void toggleState()
	{
		valueHolder.setValue(!valueHolder.getValue());
	}

}
