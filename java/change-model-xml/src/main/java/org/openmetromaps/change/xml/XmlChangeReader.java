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

package org.openmetromaps.change.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.change.Change;
import org.openmetromaps.change.ChangeModel;
import org.openmetromaps.change.Exit;

import de.topobyte.xml.domabstraction.iface.IDocument;
import de.topobyte.xml.domabstraction.iface.IDocumentFactory;
import de.topobyte.xml.domabstraction.iface.IElement;
import de.topobyte.xml.domabstraction.iface.INodeList;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class XmlChangeReader
{

	public static ChangeModel read(IDocumentFactory factory, InputStream is)
			throws ParsingException
	{
		XmlChangeReader reader = new XmlChangeReader();
		return reader.readModel(factory, is);
	}

	public static ChangeModel read(IDocument document) throws ParsingException
	{
		XmlChangeReader reader = new XmlChangeReader();
		return reader.readModel(document);
	}

	private List<Change> changes = new ArrayList<>();
	private List<Exit> exits = new ArrayList<>();

	private XmlChangeReader()
	{
		// private constructor
	}

	private ChangeModel readModel(IDocumentFactory factory, InputStream is)
			throws ParsingException
	{
		IDocument doc = factory.parse(is);
		return readModel(doc);
	}

	private ChangeModel readModel(IDocument doc) throws ParsingException
	{
		parse(doc);

		return new ChangeModel(changes, exits);
	}

	private void parse(IDocument doc)
	{
		INodeList allChanges = doc.getElementsByTagName("changes");
		IElement firstChanges = allChanges.element(0);
		INodeList changeList = firstChanges.getChildElementsByTagName("change");

		for (int i = 0; i < changeList.getLength(); i++) {
			IElement eChange = changeList.element(i);

			String line = eChange.getAttribute("line");
			String towards = eChange.getAttribute("towards");

			changes.add(new Change(line, towards));
		}
	}

}
