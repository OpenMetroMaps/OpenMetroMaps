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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openmetromaps.maps.xml.XmlLine;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import de.topobyte.collections.util.ListUtil;
import de.topobyte.webpaths.NioPaths;
import de.topobyte.webpaths.WebPath;
import de.topobyte.webpaths.WebPaths;

public class MarkdownViewCreator
{

	// TODO: line lists: stations should be links

	final static Logger logger = LoggerFactory
			.getLogger(MarkdownViewCreator.class);

	private XmlModel model;

	private static final WebPath subpathLines = WebPaths.get("lines/");
	private static final WebPath subpathStations = WebPaths.get("stations/");

	private Multimap<XmlStation, XmlLine> stationToLines = HashMultimap
			.create();

	public MarkdownViewCreator(XmlModel model)
	{
		this.model = model;
	}

	public void create(Path pathOutput) throws IOException
	{
		Files.createDirectories(pathOutput);

		Path dirLines = NioPaths.resolve(pathOutput, subpathLines);
		Path dirStations = NioPaths.resolve(pathOutput, subpathStations);
		Files.createDirectories(dirLines);
		Files.createDirectories(dirStations);

		for (XmlLine line : model.getLines()) {
			List<XmlStation> stops = line.getStops();
			for (XmlStation station : stops) {
				stationToLines.put(station, line);
			}
		}

		for (XmlLine line : model.getLines()) {
			WebPath pathLine = path(line);
			Path path = NioPaths.resolve(pathOutput, pathLine);
			createLine(path, line);
		}

		for (XmlStation station : model.getStations()) {
			WebPath pathStation = path(station);
			Path path = NioPaths.resolve(pathOutput, pathStation);
			createStation(path, station);
		}
	}

	private WebPath path(XmlLine line)
	{
		return subpathLines.resolve(sane(line.getName()) + ".md");
	}

	private WebPath path(XmlStation station)
	{
		return subpathStations.resolve(sane(station.getName()) + ".md");
	}

	private String sane(String name)
	{
		return name.replaceAll("/", "-");
	}

	private void createStation(Path file, XmlStation station) throws IOException
	{
		logger.info("creating file : " + file);
		MarkdownWriter output = new MarkdownWriter(file);

		output.heading(1, station.getName());

		List<XmlLine> lines = new ArrayList<>(stationToLines.get(station));
		Collections.sort(lines, new Comparator<XmlLine>() {

			@Override
			public int compare(XmlLine o1, XmlLine o2)
			{
				String name1 = o1.getName();
				String name2 = o2.getName();
				return name1.compareTo(name2);
			}

		});
		for (XmlLine line : lines) {
			// TODO: make links
			output.unordered(line.getName());
		}

		output.close();
	}

	private void createLine(Path file, XmlLine line) throws IOException
	{
		logger.info("creating file : " + file);
		MarkdownWriter output = new MarkdownWriter(file);

		if (line.isCircular()) {
			writeCircular(output, line);
		} else {
			writeNormal(output, line);
		}

		output.close();
	}

	private void writeNormal(MarkdownWriter output, XmlLine line)
			throws IOException
	{
		XmlStation first = line.getStops().get(0);
		XmlStation last = ListUtil.last(line.getStops());

		output.heading(1, "→ " + last.getName());
		for (XmlStation station : line.getStops()) {
			output.unordered(station.getName());
		}

		output.newLine();

		output.heading(1, "→ " + first.getName());
		for (XmlStation station : Lists.reverse(line.getStops())) {
			output.unordered(station.getName());
		}
	}

	private void writeCircular(MarkdownWriter output, XmlLine line)
			throws IOException
	{
		for (XmlStation station : line.getStops()) {
			output.unordered(station.getName());
		}
	}

}
