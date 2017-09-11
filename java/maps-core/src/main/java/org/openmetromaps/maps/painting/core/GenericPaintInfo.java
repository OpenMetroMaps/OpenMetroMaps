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

package org.openmetromaps.maps.painting.core;

public class GenericPaintInfo implements IPaintInfo
{

	private float width;
	private ColorCode color;
	private Cap cap;
	private Join join;
	private int fontSize;
	private PaintType type;

	public GenericPaintInfo()
	{
		// empty
	}

	public GenericPaintInfo(ColorCode color)
	{
		this.color = color;
	}

	public GenericPaintInfo(ColorCode color, float width)
	{
		this.color = color;
		this.width = width;
	}

	public GenericPaintInfo(ColorCode color, float width, Cap cap)
	{
		this(color, width);
		this.cap = cap;
	}

	public GenericPaintInfo(ColorCode color, float width, Join join)
	{
		this(color, width);
		this.join = join;
	}

	public GenericPaintInfo(ColorCode color, float width, Cap cap, Join join)
	{
		this(color, width);
		this.cap = cap;
		this.join = join;
	}

	public float getWidth()
	{
		return width;
	}

	@Override
	public void setWidth(float width)
	{
		this.width = width;
	}

	public ColorCode getColor()
	{
		return color;
	}

	@Override
	public void setColor(ColorCode color)
	{
		this.color = color;
	}

	public Cap getCap()
	{
		return cap;
	}

	@Override
	public void setCap(Cap cap)
	{
		this.cap = cap;
	}

	public Join getJoin()
	{
		return join;
	}

	@Override
	public void setJoin(Join join)
	{
		this.join = join;
	}

	public int getFontSize()
	{
		return fontSize;
	}

	@Override
	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;
	}

	@Override
	public Object getPaintObject()
	{
		return this;
	}

	public PaintType getStyle()
	{
		return type;
	}

	@Override
	public void setStyle(PaintType type)
	{
		this.type = type;
	}

}
