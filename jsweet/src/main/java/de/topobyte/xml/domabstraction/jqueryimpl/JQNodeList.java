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

package de.topobyte.xml.domabstraction.jqueryimpl;

import de.topobyte.xml.domabstraction.iface.IElement;
import de.topobyte.xml.domabstraction.iface.INodeList;
import def.dom.Element;
import def.dom.NodeList;
import def.dom.NodeListOf;

class JQNodeList implements INodeList
{

	private NodeListOf<Element> list;

	public JQNodeList(NodeListOf<Element> list)
	{
		this.list = list;
	}

	@Override
	public int getLength()
	{
		return ((NodeList) list).length;
	}

	@Override
	public IElement element(int i)
	{
		return new JQElement(list.item(i));
	}

}
