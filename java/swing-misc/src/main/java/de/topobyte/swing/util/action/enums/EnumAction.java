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

package de.topobyte.swing.util.action.enums;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.swing.util.action.SimpleAction;

public class EnumAction<T extends Enum<T>> extends SimpleAction
{

	final static Logger logger = LoggerFactory.getLogger(EnumAction.class);

	private static final long serialVersionUID = 1L;

	private EnumValueHolder<T> valueHolder;
	private T value;

	public EnumAction(EnumValueHolder<T> valueHolder,
			EnumAppearance<T> appearance, T value)
	{
		this.valueHolder = valueHolder;
		this.value = value;
		setName(appearance.getName(value));
		setIcon(appearance.getIcon(value));

		valueHolder.getChangeSupport().addPropertyChangeListener(
				valueHolder.getPropertyName(), new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt)
					{
						String propertyName = evt.getPropertyName();
						logger.debug(
								String.format("property changed: %s -> %s: ",
										evt.getOldValue(), evt.getNewValue()));
						if (propertyName.equals(propertyName)) {
							if (EnumAction.this.value == evt.getOldValue()
									|| EnumAction.this.value == evt
											.getNewValue()) {
								firePropertyChange(Action.SELECTED_KEY, true,
										false);
							}
						}
					}

				});
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		valueHolder.setValue(value);
	}

	@Override
	public Object getValue(String key)
	{
		if (key.equals(Action.SELECTED_KEY)) {
			return valueHolder.getValue().equals(value);
		}
		return super.getValue(key);
	}

}
