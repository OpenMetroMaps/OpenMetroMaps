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

package org.openmetromaps.model.osm.inspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;

import org.openmetromaps.model.osm.DraftLine;
import org.openmetromaps.model.osm.DraftModel;

public class LinesListModel extends AbstractListModel<DraftLine>
{

	private static final long serialVersionUID = 1L;

	private List<DraftLine> lines;

	public LinesListModel(DraftModel model)
	{
		lines = new ArrayList<>(model.getLines());
		Collections.sort(lines, new Comparator<DraftLine>() {

			@Override
			public int compare(DraftLine o1, DraftLine o2)
			{
				String name1 = o1.getName();
				String name2 = o1.getName();
				return name1.compareTo(name2);
			}

		});
	}

	@Override
	public int getSize()
	{
		return lines.size();
	}

	@Override
	public DraftLine getElementAt(int index)
	{
		return lines.get(index);
	}

	public DraftLine remove(int index)
	{
		return lines.remove(index);
	}

}
