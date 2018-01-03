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

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MarkdownWriter implements Closeable
{

	private BufferedWriter output;

	public MarkdownWriter(Path file) throws IOException
	{
		output = Files.newBufferedWriter(file);
	}

	@Override
	public void close() throws IOException
	{
		output.close();
	}

	public void write(String text) throws IOException
	{
		output.write(text);
	}

	public void newLine() throws IOException
	{
		output.newLine();
	}

	public void writeLine(String line) throws IOException
	{
		output.write(line);
		output.newLine();
	}

	public void heading(int level, String text) throws IOException
	{
		switch (level) {
		case 1:
			writeLine("# " + text);
			break;
		case 2:
			writeLine("## " + text);
			break;
		case 3:
			writeLine("### " + text);
			break;
		case 4:
			writeLine("#### " + text);
			break;
		case 5:
			writeLine("##### " + text);
			break;
		case 6:
			writeLine("###### " + text);
			break;
		}
	}

	public void unordered(String text) throws IOException
	{
		writeLine("* " + text);
	}

	public void ordered(String text) throws IOException
	{
		writeLine("1. " + text);
	}

}
