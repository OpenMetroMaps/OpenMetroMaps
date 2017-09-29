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

package org.openmetromaps.maps.actions;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openmetromaps.maps.MapViewer;
import org.openmetromaps.maps.xml.XmlModelWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.swing.util.action.SimpleAction;

public class SaveAsAction extends SimpleAction
{

	final static Logger logger = LoggerFactory.getLogger(SaveAction.class);

	private static final long serialVersionUID = 1L;

	private MapViewer mapViewer;

	public SaveAsAction(MapViewer mapViewer)
	{
		super("Save As...", "Save to a different file");
		this.mapViewer = mapViewer;
		setIcon("res/images/24/document-save.png");
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		// TODO: if file exists, ask user if we should overwrite it
		Window frame = mapViewer.getFrame();
		JFileChooser chooser = new JFileChooser();
		int value = chooser.showOpenDialog(frame);
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			logger.debug("attempting to save document to file: " + file);

			try {
				FileOutputStream os = new FileOutputStream(file);
				new XmlModelWriter().write(os, mapViewer.getModel());
				os.close();
			} catch (ParserConfigurationException | TransformerException
					| IOException e) {
				logger.error("Error while saving file", e);
				// TODO: display an error dialog
			}
		}
	}
}
