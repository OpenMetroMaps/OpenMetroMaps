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

package org.openmetromaps.gtfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

public class GtfsBomRemover
{

	private Path pathInput;
	private Path pathOutput;

	private ZipFile zip;

	public GtfsBomRemover(Path pathInput, Path pathOutput)
	{
		this.pathInput = pathInput;
		this.pathOutput = pathOutput;
	}

	public void execute() throws ZipException, IOException
	{
		zip = new ZipFile(pathInput.toFile());

		OutputStream output = Files.newOutputStream(pathOutput);
		ZipOutputStream zipOutput = new ZipOutputStream(output);

		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			InputStream input = zip.getInputStream(entry);
			BOMInputStream wrapped = new BOMInputStream(input);
			zipOutput.putNextEntry(new ZipEntry(entry.getName()));
			IOUtils.copy(wrapped, zipOutput);
			zipOutput.closeEntry();
		}

		zipOutput.close();
	}

}
