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

package org.openmetromaps.maps;

import org.openmetromaps.maps.model.Station;

public class Segment
{

	private Station a;
	private Station b;

	public Segment(Station a, Station b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Segment)) {
			return false;
		}
		Segment other = (Segment) obj;
		return other.a == a && other.b == b || other.b == a && other.a == b;
	}

	@Override
	public int hashCode()
	{
		return a.hashCode() + b.hashCode();
	}

	public Station getA()
	{
		return a;
	}

	public Station getB()
	{
		return b;
	}

}
