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

package org.openmetromaps.maps.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * We use this simple SetMultimap implementation to avoid having to use any of
 * the big libraries such as guava or commons-collections.
 */
public class SetMultimap<K, V>
{

	private Map<K, Set<V>> map = new HashMap<>();

	public void put(K key, V value)
	{
		Set<V> set = map.get(key);
		if (set == null) {
			set = new HashSet<>();
			map.put(key, set);
		}
		set.add(value);
	}

	public Set<V> get(K key)
	{
		return map.get(key);
	}

	public void remove(K key, V value)
	{
		Set<V> set = map.get(key);
		if (set == null) {
			return;
		}
		set.remove(value);
	}

}
