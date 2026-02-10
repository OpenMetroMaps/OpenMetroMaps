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

package org.openmetromaps.maps.editor.history;

import java.util.List;

import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.logic.LineStationsManipulator;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Stop;

public class EditLineStationsCommand implements HistoryCommand
{

	private final LineStationsManipulator manipulator;
	private final Line line;
	private final List<Stop> before;
	private final List<Stop> after;

	public EditLineStationsCommand(LineStationsManipulator manipulator,
			Line line, List<Stop> before, List<Stop> after)
	{
		this.manipulator = manipulator;
		this.line = line;
		this.before = before;
		this.after = after;
	}

	@Override
	public void undo(MapEditor mapEditor)
	{
		manipulator.applyStops(mapEditor, line, after, before);
	}

	@Override
	public void redo(MapEditor mapEditor)
	{
		manipulator.applyStops(mapEditor, line, before, after);
	}

	@Override
	public String getName()
	{
		return "Edit Line Stations";
	}

}
