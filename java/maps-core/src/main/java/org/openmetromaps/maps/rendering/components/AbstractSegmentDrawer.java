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
import java.util.Map;

import org.openmetromaps.maps.LocationToPoint;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.NetworkLine;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintFactory;
import org.openmetromaps.maps.painting.core.PaintType;

public abstract class AbstractSegmentDrawer implements SegmentDrawer
{

	protected LocationToPoint ltp;

	protected LineNetwork data;
	private float scale;

	protected float spreadFactor;
	protected float lineWidth;

	protected IPaintInfo[] lineToPaintForLines;

	public AbstractSegmentDrawer(PaintFactory pf, LineNetwork data,
			Map<NetworkLine, ColorCode> colors, float scale,
			LocationToPoint ltp, float spreadFactor, float lineWidth)
	{
		this.data = data;
		this.scale = scale;

		this.ltp = ltp;
		this.spreadFactor = spreadFactor;
		this.lineWidth = lineWidth;

		List<NetworkLine> lines = data.getLines();
		lineToPaintForLines = new IPaintInfo[lines.size()];
		for (NetworkLine line : lines) {
			IPaintInfo paint = pf.create(colors.get(line));
			paint.setStyle(PaintType.STROKE);
			lineToPaintForLines[line.line.getId()] = paint;
		}
	}

	@Override
	public void zoomChanged(float factor, float lineWidth)
	{
		this.lineWidth = lineWidth;

		List<NetworkLine> lines = data.getLines();
		final int nLines = lines.size();
		for (int i = 0; i < nLines; i++) {
			NetworkLine line = lines.get(i);
			IPaintInfo paint = lineToPaintForLines[line.line.getId()];
			paint.setWidth(lineWidth);
		}
	}

	@Override
	public float getScale()
	{
		return scale;
	}

	@Override
	public void setScale(float scale)
	{
		this.scale = scale;
	}

	@Override
	public void startSegments()
	{
		// do nothing
	}

	@Override
	public void finishSegments()
	{
		// do nothing
	}

}
