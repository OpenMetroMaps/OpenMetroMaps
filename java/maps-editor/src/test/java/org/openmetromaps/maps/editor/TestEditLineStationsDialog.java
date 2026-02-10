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

package org.openmetromaps.maps.editor;

import java.util.List;

import javax.swing.JDialog;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.TestData;
import org.openmetromaps.maps.editor.model.EditLineStationsDialog;
import org.openmetromaps.maps.editor.model.LineSelectionDialog;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import de.topobyte.shared.preferences.SharedPreferences;
import de.topobyte.swing.util.SwingUtils;

public class TestEditLineStationsDialog
{

	public static void main(String[] args) throws Exception
	{
		if (SharedPreferences.isUIScalePresent()) {
			SwingUtils.setUiScale(SharedPreferences.getUIScale());
		}

		XmlModel xmlModel = TestData.berlinSchematicXml();

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		ModelData data = model.getData();
		LineSelectionDialog dialog = new LineSelectionDialog(data,
				(d, positive) -> lineSelectionDone(d, positive, data));

		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialog.setSize(400, 300);
		dialog.setVisible(true);
	}

	private static void lineSelectionDone(LineSelectionDialog dialog,
			boolean positive, ModelData data)
	{
		if (!positive) {
			dialog.dispose();
			System.exit(0);
			return;
		}

		List<Line> lines = dialog.getSelectedLines();
		dialog.dispose();

		if (lines.isEmpty()) {
			System.exit(0);
			return;
		}

		Line line = lines.get(0);

		EditLineStationsDialog editDialog = new EditLineStationsDialog(data,
				line, (d, pos) -> editDialogDone(d, pos, line));

		editDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		editDialog.setSize(500, 400);
		editDialog.setVisible(true);
	}

	private static void editDialogDone(EditLineStationsDialog dialog,
			boolean positive, Line line)
	{
		if (positive) {
			List<Stop> stops = dialog.getStops();
			System.out.println("Stations for line " + line.getName() + ":");
			for (int i = 0; i < stops.size(); i++) {
				System.out.println("  " + (i + 1) + ". "
						+ stops.get(i).getStation().getName());
			}
		}
		System.exit(0);
	}

}
