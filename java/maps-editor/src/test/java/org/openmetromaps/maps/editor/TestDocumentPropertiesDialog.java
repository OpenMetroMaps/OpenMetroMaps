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

package org.openmetromaps.maps.editor;

import javax.swing.JDialog;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.TestData;
import org.openmetromaps.maps.editor.DocumentPropertiesDialog;
import org.openmetromaps.maps.editor.DocumentPropertiesPanel;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

public class TestDocumentPropertiesDialog
{

	public static void main(String[] args) throws Exception
	{
		XmlModel xmlModel = TestData.berlinXml();

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		MapView view = model.getViews().get(0);
		DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(view,
				(d, positive) -> dialogDone(d, positive));

		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialog.setSize(400, 300);
		dialog.setVisible(true);
	}

	private static void dialogDone(DocumentPropertiesDialog dialog,
			boolean positive)
	{
		if (positive) {
			DocumentPropertiesPanel panel = dialog.getDocumentPropertiesPanel();

			String valWidth = panel.getWidthValue();
			String valHeight = panel.getHeightValue();
			String valStartX = panel.getStartXValue();
			String valStartY = panel.getStartYValue();

			double width = Double.parseDouble(valWidth);
			double height = Double.parseDouble(valHeight);
			double startX = Double.parseDouble(valStartX);
			double startY = Double.parseDouble(valStartY);

			System.out
					.println(String.format("Size: %.2f x %.2f", width, height));
			System.out.println(
					String.format("Start: %.2f, %.2f", startX, startY));
		}
		System.exit(0);
	}

}
