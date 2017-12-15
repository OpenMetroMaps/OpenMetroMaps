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

package org.openmetromaps.misc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.openmetromaps.maps.xml.XmlLine;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.topobyte.webpaths.NioPaths;
import de.topobyte.webpaths.WebPath;

public class MarkdownViewCreator
{

	final static Logger logger = LoggerFactory
			.getLogger(MarkdownViewCreator.class);

	private Context context = new Context();
	private XmlModel model;

	private Multimap<XmlStation, XmlLine> stationToLines = HashMultimap
			.create();

	public MarkdownViewCreator(XmlModel model)
	{
		this.model = model;
	}

	public void create(Path pathOutput) throws IOException
	{
		Files.createDirectories(pathOutput);

		Path dirLines = NioPaths.resolve(pathOutput, context.getSubpathLines());
		Path dirStations = NioPaths.resolve(pathOutput,
				context.getSubpathStations());
		Files.createDirectories(dirLines);
		Files.createDirectories(dirStations);

		for (XmlLine line : model.getLines()) {
			List<XmlStation> stops = line.getStops();
			for (XmlStation station : stops) {
				stationToLines.put(station, line);
			}
		}

		for (XmlLine line : model.getLines()) {
			WebPath pathLine = context.path(line);
			Path path = NioPaths.resolve(pathOutput, pathLine);
			createLine(path, line);
		}

		for (XmlStation station : model.getStations()) {
			WebPath pathStation = context.path(station);
			Path path = NioPaths.resolve(pathOutput, pathStation);
			createStation(path, station);
		}
	}

	private void createStation(Path file, XmlStation station) throws IOException
	{
		logger.info("creating file : " + file);
		StationWriter writer = new StationWriter(context, file, station,
				stationToLines);
		writer.write();
	}

	private void createLine(Path file, XmlLine line) throws IOException
	{
		logger.info("creating file : " + file);
		if (line.isCircular()) {
			CircularLineWriter writer = new CircularLineWriter(context, file,
					line);
			writer.write();
		} else {
			NormalLineWriter writer = new NormalLineWriter(context, file, line);
			writer.write();
		}
	}

}
