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

package org.openmetromaps.model.osm.inspector.actions;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.xml.XmlModelWriter;
import org.openmetromaps.model.osm.DraftModel;
import org.openmetromaps.model.osm.DraftModelConverter;
import org.openmetromaps.model.osm.inspector.ModelInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.swing.util.action.SimpleAction;

public class ExportModelAction extends SimpleAction
{

	final static Logger logger = LoggerFactory
			.getLogger(ExportModelAction.class);

	private static final long serialVersionUID = 1L;

	private ModelInspector modelInpector;

	public ExportModelAction(ModelInspector modelInpector)
	{
		super("Export Model", "Export the model to file");
		this.modelInpector = modelInpector;
		setIcon("res/images/24/document-save.png");
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Window frame = modelInpector.getFrame();
		JFileChooser chooser = new JFileChooser();
		int value = chooser.showSaveDialog(frame);
		if (value != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = chooser.getSelectedFile();
		Path pathOutput = file.toPath();
		logger.debug("attempting to save document to file: " + pathOutput);

		// TODO: if file exists, ask user if we should overwrite it
		try {
			tryExportModel(pathOutput);
		} catch (IOException | ParserConfigurationException
				| TransformerException e) {
			// TODO: show warning
		}
	}

	private void tryExportModel(Path pathOutput) throws IOException,
			ParserConfigurationException, TransformerException
	{
		OutputStream os = Files.newOutputStream(pathOutput);

		DraftModel draft = modelInpector.getModel();
		ModelData data = new DraftModelConverter().convert(draft);

		new XmlModelWriter().write(os, data, new ArrayList<>());
		os.close();
	}

}
