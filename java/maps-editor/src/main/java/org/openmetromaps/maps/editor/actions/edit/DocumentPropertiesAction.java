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

import javax.swing.JDialog;

import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.editor.DocumentPropertiesDialog;
import org.openmetromaps.maps.editor.DocumentPropertiesPanel;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.swing.util.EmptyIcon;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

public class DocumentPropertiesAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory
			.getLogger(DocumentPropertiesAction.class);

	private static final long serialVersionUID = 1L;

	public DocumentPropertiesAction(MapEditor mapEditor)
	{
		super(mapEditor, "Document Properties",
				"Configure Document Properties");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		MapView view = mapEditor.getModel().getViews().get(0);

		DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(view,
				(d, positive) -> dialogDone(d, positive));
		dialog.setModal(true);

		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(mapEditor.getFrame());
		dialog.setVisible(true);
	}

	private void dialogDone(DocumentPropertiesDialog dialog, boolean positive)
	{
		if (!positive) {
			dialog.dispose();
			return;
		}

		DocumentPropertiesPanel panel = dialog.getDocumentPropertiesPanel();

		MapView view = panel.getView();
		Rectangle scene = view.getConfig().getScene();
		Coordinate start = view.getConfig().getStartPosition();

		String valWidth = panel.getWidthValue();
		String valHeight = panel.getHeightValue();
		String valStartX = panel.getStartXValue();
		String valStartY = panel.getStartYValue();

		double width = Double.parseDouble(valWidth);
		double height = Double.parseDouble(valHeight);
		double startX = Double.parseDouble(valStartX);
		double startY = Double.parseDouble(valStartY);

		scene.setX2(width);
		scene.setY2(height);

		start.setX(startX);
		start.setY(startY);

		dialog.dispose();

		mapEditor.getMap().repaint();
	}

}
