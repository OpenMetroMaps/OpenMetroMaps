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

package org.openmetromaps.maps.editor.actions.edit;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JDialog;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.openmetromaps.maps.editor.model.LineSelectionDialog;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.swing.util.EmptyIcon;

public class SelectLinesAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory
			.getLogger(SelectLinesAction.class);

	private static final long serialVersionUID = 1L;

	public SelectLinesAction(MapEditor mapEditor)
	{
		super(mapEditor, "Select Lines",
				"Choose lines and select all their stations");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		MapModel model = mapEditor.getModel();
		ModelData data = model.getData();

		LineSelectionDialog dialog = new LineSelectionDialog(
				mapEditor.getFrame(), data,
				(d, positive) -> dialogDone(d, positive));
		dialog.setModal(true);

		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(mapEditor.getFrame());
		dialog.setVisible(true);
	}

	private void dialogDone(LineSelectionDialog dialog, boolean positive)
	{
		if (!positive) {
			dialog.dispose();
			return;
		}

		LineNetwork lineNetwork = mapEditor.getMap().getLineNetwork();

		List<Line> lines = dialog.getSelectedLines();
		for (Line line : lines) {
			for (Stop stop : line.getStops()) {
				Node node = LineNetworkUtil.getNode(lineNetwork, stop);
				mapEditor.getMapViewStatus().selectNode(node);
			}
		}
		dialog.dispose();

		mapEditor.updateStationPanel();
		mapEditor.getMap().repaint();
	}

}
