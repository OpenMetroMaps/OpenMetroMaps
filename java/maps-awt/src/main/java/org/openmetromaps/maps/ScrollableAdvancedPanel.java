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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.painting.awt.AwtPainter;
import org.openmetromaps.maps.painting.core.GenericPaintFactory;
import org.openmetromaps.maps.painting.core.Painter;

import de.topobyte.viewports.geometry.Coordinate;

public class ScrollableAdvancedPanel extends BaseMapWindowPanel
{

	private static final long serialVersionUID = 1L;

	private ModelData data;
	private MapView view;
	private LineNetwork lineNetwork;
	private MapViewStatus mapViewStatus;
	private PlanRenderer renderer;

	private StationMode stationMode;
	private SegmentMode segmentMode;

	private float scale;

	public ScrollableAdvancedPanel(ModelData data, MapView view,
			MapViewStatus mapViewStatus, StationMode stationMode,
			SegmentMode segmentMode, int minZoom, int maxZoom, float scale)
	{
		super(view.getConfig().getScene());

		ViewConfig config = view.getConfig();
		setPositionX(-config.getStartPosition().getX());
		setPositionY(-config.getStartPosition().getY());

		this.data = data;
		this.view = view;
		this.lineNetwork = view.getLineNetwork();
		this.mapViewStatus = mapViewStatus;
		this.stationMode = stationMode;
		this.segmentMode = segmentMode;
		this.scale = scale;

		initRenderer();

		ViewActions.setupMovementActions(getInputMap(), getActionMap(), this);

		setFocusable(true);
	}

	private void initRenderer()
	{
		renderer = new PlanRenderer(lineNetwork, mapViewStatus, stationMode,
				segmentMode, this, this, scale, new GenericPaintFactory());
	}

	public ModelData getData()
	{
		return data;
	}

	public LineNetwork getLineNetwork()
	{
		return lineNetwork;
	}

	public void setData(ModelData data, LineNetwork lineNetwork,
			MapViewStatus mapViewStatus)
	{
		this.data = data;
		this.lineNetwork = lineNetwork;
		this.mapViewStatus = mapViewStatus;
		initRenderer();
	}

	public void setViewConfig(ViewConfig viewConfig, double zoomlevel)
	{
		setZoom(zoomlevel);
		Coordinate start = viewConfig.getStartPosition();
		setPositionX(-start.getX() + getWidth() / 2);
		setPositionY(-start.getY() + getHeight() / 2);
		scene = viewConfig.getScene();
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

		g.setColor(Color.WHITE);
		fillRect(g, scene.getX1(), scene.getY1(), scene.getX2(), scene.getY2());

		renderer.paint(painter);
	}

}
