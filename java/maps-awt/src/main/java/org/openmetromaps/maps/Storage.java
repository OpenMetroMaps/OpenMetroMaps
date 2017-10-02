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

package org.openmetromaps.maps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.xml.XmlModelWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Storage
{

	final static Logger logger = LoggerFactory.getLogger(Storage.class);

	public static void save(File file, MapViewer mapViewer)
	{
		try {
			FileOutputStream os = new FileOutputStream(file);
			ModelData data = mapViewer.getModel();
			MapView view = mapViewer.getView();
			List<MapView> views = Arrays.asList(view);
			new XmlModelWriter().write(os, data, views);
			os.close();
		} catch (ParserConfigurationException | TransformerException
				| IOException e) {
			logger.error("Error while saving file", e);
			// TODO: display an error dialog
		}
	}

}
