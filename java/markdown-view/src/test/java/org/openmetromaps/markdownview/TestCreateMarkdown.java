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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.TestData;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.markdownview.MarkdownViewCreator;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class TestCreateMarkdown
{

	public static void main(String[] args) throws IOException, ParsingException
	{
		Path pathOutput = Paths.get("/tmp/markdownview");

		XmlModel xmlModel = TestData.berlinXml();

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		MarkdownViewCreator markdownCreator = new MarkdownViewCreator(model);
		markdownCreator.create(pathOutput);
	}

}
