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

package org.openmetromaps.maps;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.painting.awt.AwtPainter;
import org.openmetromaps.maps.painting.core.GenericPaintFactory;
import org.openmetromaps.maps.painting.core.Painter;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.Coordinate;
import de.topobyte.jeography.core.viewbounds.BboxViewBounds;

public class ScrollableAdvancedPanel extends BaseMapWindowPanel
{

	private static final long serialVersionUID = 1L;

	private ModelData data;
	private PlanRenderer renderer;

	public ScrollableAdvancedPanel(ModelData data, StationMode stationMode,
			SegmentMode segmentMode, Coordinate startPosition, int minZoom,
			int maxZoom, BBox boundsBox)
	{
		super(startPosition, minZoom, maxZoom, new BboxViewBounds(boundsBox));

		this.data = data;

		renderer = new PlanRenderer(data, stationMode, segmentMode, mapWindow,
				this, 1, new GenericPaintFactory());

		final int scrollDistance = 16;

		addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e)
			{
				super.keyTyped(e);
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					mapWindow.move(0, -scrollDistance);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					mapWindow.move(0, scrollDistance);
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					mapWindow.move(-scrollDistance, 0);
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					mapWindow.move(scrollDistance, 0);
				}
				repaint();
			}

		});

		setFocusable(true);
	}

	public ModelData getData()
	{
		return data;
	}

	public PlanRenderer getPlanRenderer()
	{
		return renderer;
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Painter painter = new AwtPainter(g);

		renderer.paint(painter);
	}

}
