// Copyright 2018 Sebastian Kuerten
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

package org.openmetromaps.imports.config;

import java.util.ArrayList;
import java.util.List;

public class Processing
{

	private List<String> prefixes = new ArrayList<>();
	private List<String> suffixes = new ArrayList<>();

	public List<String> getPrefixes()
	{
		return prefixes;
	}

	public void setPrefixes(List<String> prefixes)
	{
		this.prefixes = prefixes;
	}

	public List<String> getSuffixes()
	{
		return suffixes;
	}

	public void setSuffixes(List<String> suffixes)
	{
		this.suffixes = suffixes;
	}

}
