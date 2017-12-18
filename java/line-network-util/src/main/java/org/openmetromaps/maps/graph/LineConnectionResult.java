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

package org.openmetromaps.maps.graph;

public class LineConnectionResult
{

	private int idxNode1;
	private int idxNode2;
	private boolean valid;

	public void setIndex1(int idxNode1)
	{
		this.idxNode1 = idxNode1;
	}

	public void setIndex2(int idxNode2)
	{
		this.idxNode2 = idxNode2;
	}

	public int getIdxNode1()
	{
		return idxNode1;
	}

	public void setIdxNode1(int idxNode1)
	{
		this.idxNode1 = idxNode1;
	}

	public int getIdxNode2()
	{
		return idxNode2;
	}

	public void setIdxNode2(int idxNode2)
	{
		this.idxNode2 = idxNode2;
	}

	public void setValid(boolean valid)
	{
		this.valid = valid;
	}

	public boolean isValid()
	{
		return valid;
	}

}
