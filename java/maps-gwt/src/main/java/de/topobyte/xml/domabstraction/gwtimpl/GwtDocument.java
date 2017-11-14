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

package de.topobyte.xml.domabstraction.gwtimpl;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;

import de.topobyte.xml.domabstraction.iface.IDocument;
import de.topobyte.xml.domabstraction.iface.INodeList;

public class GwtDocument implements IDocument
{

	private Document doc;

	public GwtDocument(Document doc)
	{
		this.doc = doc;
	}

	@Override
	public INodeList getElementsByTagName(String name)
	{
		NodeList list = doc.getElementsByTagName(name);
		return new GwtNodeList(list);
	}

}
