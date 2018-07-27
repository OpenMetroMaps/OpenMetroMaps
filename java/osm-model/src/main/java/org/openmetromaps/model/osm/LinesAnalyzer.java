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

package org.openmetromaps.model.osm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import de.topobyte.formatting.Formatting;
import de.topobyte.lineprinter.LinePrinter;

public class LinesAnalyzer
{

	private DraftModel model;
	private LinePrinter output;

	public LinesAnalyzer(DraftModel model)
	{
		this.model = model;
	}

	public void analyze(LinePrinter output, boolean removeReverse)
	{
		this.output = output;

		List<DraftLine> lines = model.getLines();

		Multiset<String> nameCounts = HashMultiset.create();
		Multimap<String, DraftLine> nameToLines = HashMultimap.create();
		for (DraftLine line : lines) {
			String name = line.getName();
			nameCounts.add(name);
			nameToLines.put(name, line);
		}

		output.println(Formatting.format("Found %d lines", lines.size()));

		output.println("Line names:");
		for (DraftLine line : lines) {
			String name = line.getName();
			output.println("line: " + name);
		}

		output.println("Lines with != 2 occurrences:");
		List<String> names = new ArrayList<>(nameCounts.elementSet());
		Collections.sort(names);
		for (String name : names) {
			int count = nameCounts.count(name);
			if (count == 2) {
				continue;
			}
			output.println(Formatting.format("%s: %d", name, count));
		}

		output.println("Comparing lines with 2 occurrences...");
		for (String name : names) {
			int count = nameCounts.count(name);
			if (count != 2) {
				continue;
			}
			List<DraftLine> list = new ArrayList<>(nameToLines.get(name));
			DraftLine line1 = list.get(0);
			DraftLine line2 = list.get(1);
			compare(name, line1, line2, removeReverse);
		}
	}

	private void compare(String name, DraftLine line1, DraftLine line2,
			boolean removeReverse)
	{
		List<DraftStation> stations1 = line1.getStations();
		List<DraftStation> stations2 = line2.getStations();
		if (stations1.size() != stations2.size()) {
			output.println(Formatting.format("Line: %s, %d vs. %d", name,
					stations1.size(), stations2.size()));
			return;
		}
		int n = stations1.size();
		int different = 0;
		for (int i = 0; i < n; i++) {
			DraftStation station1 = stations1.get(i);
			DraftStation station2 = stations2.get(n - i - 1);
			if (!station1.getName().equals(station2.getName())) {
				different += 1;
			}
		}
		if (different == 0) {
			output.println(Formatting.format("Line: %s, %d stations, all clear",
					name, n));
			if (removeReverse) {
				model.getLines().remove(line2);
			}
		} else {
			output.println(Formatting.format(
					"Line: %s, %d stations, %d different", name, n, different));
			for (int i = 0; i < n; i++) {
				DraftStation station1 = stations1.get(i);
				DraftStation station2 = stations2.get(n - i - 1);
				if (!station1.getName().equals(station2.getName())) {
					output.println(Formatting.format("%s - %s",
							station1.getName(), station2.getName()));
				}
			}
		}
	}

}
