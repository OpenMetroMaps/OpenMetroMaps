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

import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.editor.config.ConfigurationHelper;
import org.openmetromaps.maps.editor.config.VolatileConfiguration;
import org.openmetromaps.maps.xml.XmlModelWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Storage
{

	final static Logger logger = LoggerFactory.getLogger(Storage.class);

	public static void save(File file, MapEditor mapEditor)
	{
		try {
			FileOutputStream os = new FileOutputStream(file);
			MapModel model = mapEditor.getModel();
			MapView view = mapEditor.getView();
			List<MapView> views = Arrays.asList(view);
			new XmlModelWriter().write(os, model.getData(), views);
			os.close();
		} catch (ParserConfigurationException | IOException e) {
			logger.error("Error while saving file", e);
			// TODO: display an error dialog
		}
	}

	public static void saveAs(MapEditor mapEditor, String title)
	{
		VolatileConfiguration config = mapEditor.getVolatileConfig();
		Path lastUsed = config.getLastUsedDirectory();

		// TODO: if file exists, ask user if we should overwrite it
		Window frame = mapEditor.getFrame();
		JFileChooser chooser = new JFileChooser();
		if (lastUsed != null) {
			chooser.setCurrentDirectory(lastUsed.toFile());
		}
		if (title != null) {
			chooser.setDialogTitle(title);
		}
		int value = chooser.showSaveDialog(frame);
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			logger.debug("attempting to save document to file: " + file);
			save(file, mapEditor);
			mapEditor.setSource(file.toPath());

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
