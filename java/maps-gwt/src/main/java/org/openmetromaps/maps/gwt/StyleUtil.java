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

package org.openmetromaps.maps.gwt;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.UIObject;

public class StyleUtil
{

	public static void setSize(UIObject object, double width, double height,
			Unit unit)
	{
		setSize(object.getElement(), width, height, unit);
	}

	public static void setSize(Element element, double width, double height,
			Unit unit)
	{
		setSize(element.getStyle(), width, height, unit);
	}

	public static void setSize(Style style, double width, double height,
			Unit unit)
	{
		style.setWidth(width, unit);
		style.setHeight(height, unit);
	}

	public static void setWidth(UIObject object, double width, Unit unit)
	{
		setWidth(object.getElement(), width, unit);
	}

	public static void setWidth(Element element, double width, Unit unit)
	{
		setWidth(element.getStyle(), width, unit);
	}

	public static void setWidth(Style style, double width, Unit unit)
	{
		style.setWidth(width, unit);
	}

	public static void setHeight(UIObject object, double height, Unit unit)
	{
		setHeight(object.getElement(), height, unit);
	}

	public static void setHeight(Element element, double height, Unit unit)
	{
		setHeight(element.getStyle(), height, unit);
	}

	public static void setHeight(Style style, double height, Unit unit)
	{
		style.setHeight(height, unit);
	}

	public static void setProperty(UIObject object, String name, double value,
			Unit unit)
	{
		setProperty(object.getElement(), name, value, unit);
	}

	public static void setProperty(Element element, String name, double value,
			Unit unit)
	{
		setProperty(element.getStyle(), name, value, unit);
	}

	public static void setProperty(Style style, String name, double value,
			Unit unit)
	{
		style.setProperty(name, value, unit);
	}

	public static void absoluteTopRight(UIObject object, double top,
			double right, Unit unit)
	{
		absoluteTopRight(object.getElement(), top, right, unit);
	}

	public static void absoluteTopRight(Element element, double top,
			double right, Unit unit)
	{
		absoluteTopRight(element.getStyle(), top, right, unit);
	}

	public static void absoluteTopRight(Style style, double top, double right,
			Unit unit)
	{
		style.setPosition(Position.ABSOLUTE);
		style.setTop(top, Unit.EM);
		style.setRight(right, Unit.EM);
	}

	public static void marginTop(UIObject object, double value, Unit unit)
	{
		marginTop(object.getElement(), value, unit);
	}

	public static void marginTop(Element element, double value, Unit unit)
	{
		marginTop(element.getStyle(), value, unit);
	}

	public static void marginTop(Style style, double value, Unit unit)
	{
		style.setMarginTop(value, unit);
	}

	public static void absolute(UIObject object, double top, double right,
			double bottom, double left, Unit unit)
	{
		absolute(object.getElement(), top, right, bottom, left, unit);
	}

	public static void absolute(Element element, double top, double right,
			double bottom, double left, Unit unit)
	{
		absolute(element.getStyle(), top, right, bottom, left, unit);
	}

	public static void absolute(Style style, double top, double right,
			double bottom, double left, Unit unit)
	{
		style.setPosition(Position.ABSOLUTE);
		style.setLeft(left, unit);
		style.setRight(right, unit);
		style.setTop(top, unit);
		style.setBottom(bottom, unit);
	}

}
