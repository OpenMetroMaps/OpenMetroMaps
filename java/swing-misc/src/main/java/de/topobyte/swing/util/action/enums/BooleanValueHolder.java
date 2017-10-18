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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooleanValueHolder
{

	final static Logger logger = LoggerFactory
			.getLogger(BooleanValueHolder.class);

	private PropertyChangeSupport changeSupport;
	private String propertyName;
	private Consumer<Boolean> changeAction;

	private boolean value;

	public BooleanValueHolder(PropertyChangeSupport changeSupport,
			String propertyName, Consumer<Boolean> changeAction, boolean value)
	{
		this.changeSupport = changeSupport;
		this.propertyName = propertyName;
		this.value = value;
		this.changeAction = changeAction;
	}

	public void setValue(boolean value)
	{
		boolean oldValue = this.value;
		this.value = value;

		changeSupport.firePropertyChange(propertyName, oldValue, value);
		changeAction.accept(value);
	}

	public boolean getValue()
	{
		return value;
	}

}
