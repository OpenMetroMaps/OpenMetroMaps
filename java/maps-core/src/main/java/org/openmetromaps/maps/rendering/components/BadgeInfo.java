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

package org.openmetromaps.maps.rendering.components;

public class BadgeInfo
{

	private int fontSize;
	private float height;
	private float paddingH;
	private float paddingBetween;
	private float arc;

	public BadgeInfo(int fontSize, float height, float paddingH,
			float paddingBetween, float arc)
	{
		this.fontSize = fontSize;
		this.height = height;
		this.paddingH = paddingH;
		this.paddingBetween = paddingBetween;
		this.arc = arc;
	}

	public int getFontSize()
	{
		return fontSize;
	}

	public float getHeight()
	{
		return height;
	}

	public float getPaddingH()
	{
		return paddingH;
	}

	public float getPaddingBetween()
	{
		return paddingBetween;
	}

	public float getArc()
	{
		return arc;
	}

}
