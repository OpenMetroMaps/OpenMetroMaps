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

package org.openmetromaps.maps.rendering.components;

import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.LocationToPoint;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintFactory;
import org.openmetromaps.maps.painting.core.Painter;
import org.openmetromaps.maps.painting.core.geom.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.lightgeom.lina.Point;

public class StationDrawerDot extends AbstractStationDrawer
{

	static final Logger logger = LoggerFactory
			.getLogger(StationDrawerDot.class);

	public StationDrawerDot(PaintFactory pf, LineNetwork data,
			Map<NetworkLine, ColorCode> colors, float scale,
			LocationToPoint ltp, float spreadFactor)
	{
		super(pf, data, colors, scale, ltp, spreadFactor);
	}

	@Override
	public void drawStation(Painter g, Node node, Path path, boolean selected,
			boolean renderCenter)
	{
		g.setRef(node);
		drawStationInternal(g, node, path, selected, renderCenter);
		g.setNoRef();
	}

	private void drawStationInternal(Painter g, Node node, Path path,
			boolean selected, boolean renderCenter)
	{
		List<Stop> stops = node.station.getStops();
		Point location = node.location;

		double px = ltp.getX(location.x);
		double py = ltp.getY(location.y);

		if (stops.size() == 1) {
			Stop stop = stops.get(0);
			IPaintInfo paint = lineToPaintForStations[stop.getLine().getId()];
			drawSinglePuntal(g, px, py, paint, selected);
		} else {
			drawMultiPuntal(g, px, py, selected);
		}
	}

}
