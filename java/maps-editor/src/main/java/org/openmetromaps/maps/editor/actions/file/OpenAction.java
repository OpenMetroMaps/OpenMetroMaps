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

package org.openmetromaps.maps.editor.actions.file;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFileChooser;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.openmetromaps.maps.editor.config.ConfigurationHelper;
import org.openmetromaps.maps.editor.config.VolatileConfiguration;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class OpenAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory.getLogger(OpenAction.class);

	private static final long serialVersionUID = 1L;

	public OpenAction(MapEditor mapEditor)
	{
		super(mapEditor, "Open", "Open a file");
		setIcon("res/images/24/document-open.png");
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		VolatileConfiguration config = mapEditor.getVolatileConfig();
		Path lastUsed = config.getLastUsedDirectory();

		Window frame = mapEditor.getFrame();
		JFileChooser chooser = new JFileChooser();
		if (lastUsed != null) {
			chooser.setCurrentDirectory(lastUsed.toFile());
		}
		int value = chooser.showOpenDialog(frame);
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			logger.debug("attempting to open document from file: " + file);

			try {
				FileInputStream is = new FileInputStream(file);
				XmlModel xmlModel = DesktopXmlModelReader.read(is);
				is.close();

				MapModel model = new XmlModelConverter().convert(xmlModel);
				mapEditor.setModel(model);
				mapEditor.getMap().repaint();
				mapEditor.setSource(file.toPath());
			} catch (IOException | ParsingException e) {
				logger.error("Error while loading file", e);
				// TODO: display an error dialog
			}

			Path newLastUsed = file.toPath().getParent();
			config.setLastUsedDirectory(newLastUsed);
			try {
				ConfigurationHelper.store(config);
			} catch (IOException e) {
				logger.warn("Unable to store volatile configuration", e);
			}
		}
	}

}
