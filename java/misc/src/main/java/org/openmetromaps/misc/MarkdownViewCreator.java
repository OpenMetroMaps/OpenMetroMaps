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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.openmetromaps.maps.xml.XmlLine;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import de.topobyte.collections.util.ListUtil;

public class MarkdownViewCreator
{

	final static Logger logger = LoggerFactory
			.getLogger(MarkdownViewCreator.class);

	private XmlModel model;

	public MarkdownViewCreator(XmlModel model)
	{
		this.model = model;
	}

	public void create(Path pathOutput) throws IOException
	{
		Files.createDirectories(pathOutput);

		for (XmlLine line : model.getLines()) {
			Path pathLine = pathOutput.resolve(line.getName() + ".md");
			createLine(pathLine, line);
		}
	}

	private void createLine(Path file, XmlLine line) throws IOException
	{
		logger.info("creating file : " + file);
		BufferedWriter output = Files.newBufferedWriter(file);

		if (line.isCircular()) {
			writeCircular(output, line);
		} else {
			writeNormal(output, line);
		}

		output.close();
	}

	private void writeNormal(BufferedWriter output, XmlLine line)
			throws IOException
	{
		XmlStation first = line.getStops().get(0);
		XmlStation last = ListUtil.last(line.getStops());

		output.write("# → " + last.getName());
		output.newLine();
		for (XmlStation station : line.getStops()) {
			output.write("* " + station.getName());
			output.newLine();
		}

		output.newLine();

		output.write("# → " + first.getName());
		output.newLine();
		for (XmlStation station : Lists.reverse(line.getStops())) {
			output.write("* " + station.getName());
			output.newLine();
		}
	}

	private void writeCircular(BufferedWriter output, XmlLine line)
			throws IOException
	{
		for (XmlStation station : line.getStops()) {
			output.write("* " + station.getName());
			output.newLine();
		}
	}

}
