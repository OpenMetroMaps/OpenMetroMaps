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

import java.util.ArrayDeque;
import java.util.Deque;

import org.openmetromaps.maps.editor.MapEditor;

public class MapEditorHistory
{

	private final Deque<HistoryCommand> undoStack = new ArrayDeque<>();
	private final Deque<HistoryCommand> redoStack = new ArrayDeque<>();
	private boolean applying = false;

	public Capture begin(String name, MapEditorSnapshot before)
	{
		if (applying) {
			return null;
		}
		return new Capture(name, before);
	}

	public void end(Capture capture, MapEditorSnapshot after)
	{
		if (capture == null || applying) {
			return;
		}
		record(capture.name, capture.before, after);
	}

	public void record(String name, MapEditorSnapshot before,
			MapEditorSnapshot after)
	{
		if (applying || before == null || after == null) {
			return;
		}
		if (before.isSameAs(after)) {
			return;
		}
		undoStack.push(new SnapshotCommand(name, before, after));
		redoStack.clear();
	}

	public void record(HistoryCommand command)
	{
		if (applying || command == null) {
			return;
		}
		undoStack.push(command);
		redoStack.clear();
	}

	public boolean canUndo()
	{
		return !undoStack.isEmpty();
	}

	public boolean canRedo()
	{
		return !redoStack.isEmpty();
	}

	public void undo(MapEditor mapEditor)
	{
		if (!canUndo()) {
			return;
		}
		HistoryCommand command = undoStack.pop();
		applying = true;
		command.undo(mapEditor);
		applying = false;
		redoStack.push(command);
	}

	public void redo(MapEditor mapEditor)
	{
		if (!canRedo()) {
			return;
		}
		HistoryCommand command = redoStack.pop();
		applying = true;
		command.redo(mapEditor);
		applying = false;
		undoStack.push(command);
	}

	public void clear()
	{
		undoStack.clear();
		redoStack.clear();
	}

	public boolean isApplying()
	{
		return applying;
	}

}
