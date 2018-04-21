// Copyright 2018 Sebastian Kuerten
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

package org.openmetromaps.mobidig.actions.file;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFileChooser;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.mobidig.MapViewer;
import org.openmetromaps.mobidig.actions.MapViewerAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class OpenAction extends MapViewerAction
{

	final static Logger logger = LoggerFactory.getLogger(OpenAction.class);

	private static final long serialVersionUID = 1L;

	public OpenAction(MapViewer mapViewer)
	{
		super(mapViewer, "Open", "Open a file");
		setIcon("res/images/24/document-open.png");
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Path lastDir = null;
		Path currentFile = mapViewer.getSource();
		if (currentFile != null) {
			lastDir = currentFile.getParent();
		}

		Window frame = mapViewer.getFrame();
		JFileChooser chooser = new JFileChooser();
		if (lastDir != null) {
			chooser.setCurrentDirectory(lastDir.toFile());
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
				mapViewer.setModel(model);
				mapViewer.getMap().repaint();
				mapViewer.setSource(file.toPath());
			} catch (IOException | ParsingException e) {
				logger.error("Error while loading file", e);
				// TODO: display an error dialog
			}
		}
	}

}
