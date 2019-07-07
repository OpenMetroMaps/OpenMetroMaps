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

package org.openmetromaps.maps.rendering.components;

import java.util.List;

import org.openmetromaps.maps.graph.Edge;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.painting.core.Painter;

public interface SegmentDrawer
{

	public void startSegments();

	public void finishSegments();

	public void drawSegment(Painter g, List<NetworkLine> lines, Edge edge);

	public void zoomChanged(float factor, float lineWidth);

	public float getScale();

	public void setScale(float scale);

}
