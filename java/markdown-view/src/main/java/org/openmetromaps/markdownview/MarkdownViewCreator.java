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

package org.openmetromaps.markdownview;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.openmetromaps.heavyutil.HeavyUtil;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapModelUtil;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
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

	private Context context;

	private Multimap<Station, Line> stationToLines = HashMultimap.create();

	private MapModel model;
	private LineNetwork lineNetwork;

	public MarkdownViewCreator(MapModel model)
	{
		this.model = model;

		LineNetworkBuilder builder = new LineNetworkBuilder(model.getData(),
				MapModelUtil.allEdges(model));
		lineNetwork = builder.getGraph();

		context = new Context(stationToLines, lineNetwork);
	}

	public void create(Path pathOutput) throws IOException
	{
		Files.createDirectories(pathOutput);

		Path dirLines = NioPaths.resolve(pathOutput, context.getSubpathLines());
		Path dirStations = NioPaths.resolve(pathOutput,
				context.getSubpathStations());
		Files.createDirectories(dirLines);
		Files.createDirectories(dirStations);

		HeavyUtil.fillStationToLines(stationToLines, model);

		for (NetworkLine line : lineNetwork.lines) {
			WebPath pathLine = context.path(line.line);
			Path path = NioPaths.resolve(pathOutput, pathLine);
			createLine(path, line);
		}

		for (Station station : model.getData().stations) {
			WebPath pathStation = context.path(station);
			Path path = NioPaths.resolve(pathOutput, pathStation);
			createStation(path, station);
		}
	}

	private void createStation(Path file, Station station) throws IOException
	{
		logger.info("creating file : " + file);
		StationWriter writer = new StationWriter(context, file, station);
		writer.write();
	}

	private void createLine(Path file, NetworkLine line) throws IOException
	{
		logger.info("creating file : " + file);
		if (line.line.isCircular()) {
			CircularLineWriter writer = new CircularLineWriter(context, file,
					line);
			writer.write();
		} else {
			NormalLineWriter writer = new NormalLineWriter(context, file, line);
			writer.write();
		}
	}

}
