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

package org.openmetromaps.maps.editor.actions.edit;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.openmetromaps.maps.editor.history.EditLineStationsCommand;
import org.openmetromaps.maps.editor.logic.LineStationsManipulator;
import org.openmetromaps.maps.editor.model.EditLineStationsDialog;
import org.openmetromaps.maps.editor.model.LineSelectionDialog;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.swing.util.EmptyIcon;

public class EditLineStationsAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory
			.getLogger(EditLineStationsAction.class);

	private static final long serialVersionUID = 1L;

	public EditLineStationsAction(MapEditor mapEditor)
	{
		super(mapEditor, "Edit Line Stations",
				"Edit the stations within a line");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		MapModel model = mapEditor.getModel();
		ModelData data = model.getData();

		LineSelectionDialog dialog = new LineSelectionDialog(
				mapEditor.getFrame(), data,
				(d, positive) -> lineSelectionDone(d, positive, data));
		dialog.setModal(true);

		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(mapEditor.getFrame());
		dialog.setVisible(true);
	}

	private void lineSelectionDone(LineSelectionDialog dialog, boolean positive,
			ModelData data)
	{
		if (!positive) {
			dialog.dispose();
			return;
		}

		List<Line> lines = dialog.getSelectedLines();
		dialog.dispose();

		if (lines.isEmpty()) {
			return;
		}

		Line line = lines.get(0);

		EditLineStationsDialog editDialog = new EditLineStationsDialog(
				mapEditor.getFrame(), data, line,
				(d, pos) -> editDialogDone(d, pos, line));
		editDialog.setModal(true);

		editDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		editDialog.setSize(500, 400);
		editDialog.setLocationRelativeTo(mapEditor.getFrame());
		editDialog.setVisible(true);
	}

	private void editDialogDone(EditLineStationsDialog dialog, boolean positive,
			Line line)
	{
		if (!positive) {
			dialog.dispose();
			return;
		}

		dialog.dispose();

		List<Stop> oldStops = new ArrayList<>(line.getStops());
		List<Stop> newStops = dialog.getStops();

		LineStationsManipulator manipulator = new LineStationsManipulator();
		manipulator.applyStops(mapEditor, line, oldStops, newStops);

		mapEditor.getHistory().record(new EditLineStationsCommand(manipulator,
				line, oldStops, newStops));
	}

}
