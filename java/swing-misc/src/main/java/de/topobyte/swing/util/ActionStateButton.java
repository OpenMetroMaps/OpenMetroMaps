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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;

public class ActionStateButton extends JButton implements PropertyChangeListener
{

	private static final long serialVersionUID = 1L;

	public ActionStateButton(Action action)
	{
		super(action);
		setText(null);
		action.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (Action.SELECTED_KEY.equals(evt.getPropertyName())) {
			repaint();
		}
	}

	@Override
	public void removeNotify()
	{
		Action action = getAction();
		if (action != null) {
			action.removePropertyChangeListener(this);
		}
		super.removeNotify();
	}

}
