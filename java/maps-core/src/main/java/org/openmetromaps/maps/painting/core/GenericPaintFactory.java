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

public class GenericPaintFactory implements PaintFactory
{

	@Override
	public IPaintInfo create()
	{
		return new GenericPaintInfo();
	}

	@Override
	public IPaintInfo create(ColorCode color)
	{
		return new GenericPaintInfo(color);
	}

	@Override
	public IPaintInfo create(ColorCode color, float width)
	{
		return new GenericPaintInfo(color, width);
	}

	@Override
	public IPaintInfo create(ColorCode color, float width, Cap cap)
	{
		return new GenericPaintInfo(color, width, cap);
	}

	@Override
	public IPaintInfo create(ColorCode color, float width, Join join)
	{
		return new GenericPaintInfo(color, width, join);
	}

	@Override
	public IPaintInfo create(ColorCode color, float width, Cap cap, Join join)
	{
		return new GenericPaintInfo(color, width, cap, join);
	}

}
