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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import de.topobyte.formatting.Formatting;
import de.topobyte.lineprinter.LinePrinter;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmRelationMember;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public class StopsAnalyzer
{

	private DraftModel model;

	public StopsAnalyzer(DraftModel model)
	{
		this.model = model;
	}

	public void analyze(LinePrinter output)
	{
		Multiset<String> nodeRoles = HashMultiset.create();

		for (DraftLine line : model.getLines()) {
			OsmRelation relation = line.getSource();
			for (OsmRelationMember member : OsmModelUtil
					.membersAsList(relation)) {
				EntityType type = member.getType();
				String role = member.getRole();
				if (type == EntityType.Node) {
					nodeRoles.add(role);
				}
			}
		}

		List<String> roles = new ArrayList<>(nodeRoles.elementSet());
		Collections.sort(roles);
		for (String role : roles) {
			output.println(
					Formatting.format("%s: %d", role, nodeRoles.count(role)));
		}
	}

}
