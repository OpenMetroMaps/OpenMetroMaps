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

package org.openmetromaps.maps.editor.algorithms;

import static java.util.Arrays.asList;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmetromaps.maps.editor.algorithms.Util;

public class TestFindConsecutive
{

	@Test
	public void test()
	{
		test(asList(4), asList(asList(4)));
		test(asList(4, 5), asList(asList(4, 5)));
		test(asList(4, 5, 6), asList(asList(4, 5, 6)));
		test(asList(4, 5, 6, 7), asList(asList(4, 5, 6, 7)));

		test(asList(4, 5, 7), asList(asList(4, 5), asList(7)));
		test(asList(4, 6, 7), asList(asList(4), asList(6, 7)));
		test(asList(4, 5, 7, 8), asList(asList(4, 5), asList(7, 8)));

		test(asList(4, 5, 6, 8, 9, 10),
				asList(asList(4, 5, 6), asList(8, 9, 10)));
		test(asList(4, 5, 6, 8, 9, 10, 12, 13, 14),
				asList(asList(4, 5, 6), asList(8, 9, 10), asList(12, 13, 14)));
	}

	private void test(List<Integer> ids, List<List<Integer>> expected)
	{
		List<List<Integer>> results = Util.findConsecutive(ids);

		Assert.assertEquals("number of results", expected.size(),
				results.size());
		for (int i = 0; i < results.size(); i++) {
			List<Integer> resultI = results.get(i);
			List<Integer> expectedI = expected.get(i);
			Assert.assertEquals("list #" + i, expectedI, resultI);
		}
	}

}
