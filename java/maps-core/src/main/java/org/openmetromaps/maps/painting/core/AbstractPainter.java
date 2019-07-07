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

package org.openmetromaps.maps.painting.core;

import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.painting.core.ref.LineEdgeReference;
import org.openmetromaps.maps.painting.core.ref.NodeReference;
import org.openmetromaps.maps.painting.core.ref.Reference;

public abstract class AbstractPainter implements Painter
{

	protected Reference reference = null;

	@Override
	public void setRef(Node node)
	{
		reference = new NodeReference(node);
	}

	@Override
	public void setRef(Edge edge, NetworkLine line)
	{
		reference = new LineEdgeReference(edge, line);
	}

	@Override
	public void setNoRef()
	{
		reference = null;
	}

}
