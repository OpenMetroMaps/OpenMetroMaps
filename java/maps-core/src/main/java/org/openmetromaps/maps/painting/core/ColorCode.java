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

import de.topobyte.formatting.Formatting;

public class ColorCode
{

	private int value = 0xff000000;

	public ColorCode(int rgb)
	{
		value = 0xff000000 | rgb;
	}

	public ColorCode(int rgba, boolean hasalpha)
	{
		if (hasalpha) {
			value = rgba;
		} else {
			value = 0xff000000 | rgba;
		}
	}

	public ColorCode(int r, int g, int b)
	{
		this(r, g, b, 255);
	}

	public ColorCode(int r, int g, int b, int a)
	{
		value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8)
				| ((b & 0xFF) << 0);
	}

	public int getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return Formatting.format("0x%X", value);
	}

	public int getAlpha()
	{
		return (value >> 24) & 0xFF;
	}

	public int getRed()
	{
		return (value >> 16) & 0xFF;
	}

	public int getGreen()
	{
		return (value >> 8) & 0xFF;
	}

	public int getBlue()
	{
		return value & 0xFF;
	}

}
