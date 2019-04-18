// Copyright 2019 Sebastian Kuerten
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

package org.openmetromaps.graphml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.xml.XmlModelWriter;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.io.GraphIOException;

public class TestConvert
{

	public static void main(String[] args) throws GraphIOException,
			ParserConfigurationException, TransformerException, IOException
	{
		InputStream input = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("wien.graphml");

		GraphMLReader graphMLReader = new GraphMLReader();
		UndirectedGraph<Vertex, Edge> graph = graphMLReader.read(input);

		GraphConverter converter = new GraphConverter();
		ModelData model = converter.convert(graph);

		List<MapView> views = new ArrayList<>();

		OutputStream os = new FileOutputStream("/tmp/test.omm");
		new XmlModelWriter().write(os, model, views);
		os.close();
	}

}
