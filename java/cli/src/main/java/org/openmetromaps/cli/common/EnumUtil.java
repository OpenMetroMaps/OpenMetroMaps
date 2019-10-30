// Copyright 2019 Sebastian Kuerten
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

package org.openmetromaps.cli.common;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class EnumUtil<T extends Enum<T>>
{

	public static <T extends Enum<T>> String buildNameList(T[] values)
	{
		List<String> options = new ArrayList<>();
		for (T mode : values) {
			options.add(mode.toString().toLowerCase());
		}
		return Joiner.on(", ").join(options);
	}

}
