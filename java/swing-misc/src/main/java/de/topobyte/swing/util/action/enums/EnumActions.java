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

import java.beans.PropertyChangeSupport;
import java.util.function.Consumer;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumActions
{

	final static Logger logger = LoggerFactory.getLogger(EnumActions.class);

	public static <T extends Enum<T>> void add(JMenu menu, Class<T> c,
			PropertyChangeSupport changeSupport, String propertyName, T value,
			Consumer<T> changeAction, EnumAppearance<T> appearance)
	{
		T[] contants = c.getEnumConstants();
		EnumValueHolder<T> valueHolder = new EnumValueHolder<>(changeSupport,
				propertyName, changeAction, value);
		for (T constant : contants) {
			EnumAction<T> action = new EnumAction<>(valueHolder, appearance,
					constant);
			menu.add(new JCheckBoxMenuItem(action));
		}
	}

	public static <T extends Enum<T>> void add(JMenu menu, Class<T> c,
			EnumValueHolder<T> valueHolder, Consumer<T> changeAction,
			EnumAppearance<T> appearance)
	{
		T[] contants = c.getEnumConstants();
		for (T constant : contants) {
			EnumAction<T> action = new EnumAction<>(valueHolder, appearance,
					constant);
			menu.add(new JCheckBoxMenuItem(action));
		}
	}

}
