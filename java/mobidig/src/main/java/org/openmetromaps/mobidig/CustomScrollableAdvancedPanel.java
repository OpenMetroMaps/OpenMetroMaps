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

package org.openmetromaps.mobidig;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;

import org.openmetromaps.maps.BaseMapWindowPanel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.MapViewStatus;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.ViewActions;
import org.openmetromaps.maps.ViewConfig;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.painting.awt.AwtPainter;
import org.openmetromaps.maps.painting.core.ColorCode;
import org.openmetromaps.maps.painting.core.GenericPaintFactory;
import org.openmetromaps.maps.painting.core.Painter;

import de.topobyte.viewports.geometry.Coordinate;

public class CustomScrollableAdvancedPanel extends BaseMapWindowPanel
{

	private static final long serialVersionUID = 1L;

	private ModelData data;
	private MapView view;
	private LineNetwork lineNetwork;
	private MapViewStatus mapViewStatus;
	private CustomPlanRenderer renderer;

	private StationMode stationMode;
	private SegmentMode segmentMode;

	private Map<String, ColorCode> colorMap;
	private float scale;

	private String title;
	private String subtitle;

	public CustomScrollableAdvancedPanel(ModelData data, MapView view,
			MapViewStatus mapViewStatus, StationMode stationMode,
			SegmentMode segmentMode, int minZoom, int maxZoom,
			Map<String, ColorCode> colorMap, float scale, String title,
			String subtitle)
	{
		super(view.getConfig().getScene());
		this.title = title;
		this.subtitle = subtitle;

		ViewConfig config = view.getConfig();
		setPositionX(-config.getStartPosition().getX());
		setPositionY(-config.getStartPosition().getY());

		this.data = data;
		this.view = view;
		this.lineNetwork = view.getLineNetwork();
		this.mapViewStatus = mapViewStatus;
		this.stationMode = stationMode;
		this.segmentMode = segmentMode;
		this.colorMap = colorMap;
		this.scale = scale;

		initRenderer();

		ViewActions.setupMovementActions(getInputMap(), getActionMap(), this);

		setFocusable(true);
	}

	private void initRenderer()
	{
		renderer = new CustomPlanRenderer(lineNetwork, mapViewStatus,
				stationMode, segmentMode, this, this, scale,
				new GenericPaintFactory(), colorMap);
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

	public CustomPlanRenderer getPlanRenderer()
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

		Font font1 = new Font("Verdana", Font.BOLD, 30);
		Font font2 = new Font("Verdana", Font.BOLD, 24);
		int margin = 10;
		int padding = 5;
		int posX = 30;

		g.setFont(font1);

		int posY = font1.getSize() + margin;
		g.setColor(Color.WHITE);
		painter.outlineString(title, posX, posY);
		g.setColor(Color.BLACK);
		painter.drawString(title, posX, posY);

		g.setFont(font2);

		posY = font1.getSize() + margin + font2.getSize() + padding;
		g.setColor(Color.WHITE);
		painter.outlineString(subtitle, posX, posY);
		g.setColor(Color.BLACK);
		painter.drawString(subtitle, posX, posY);
	}

}
