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

package org.openmetromaps.maps.gwt;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.MapViewStatus;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.PlanRenderer;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.gwt.input.ContextMenu;
import org.openmetromaps.maps.gwt.input.ContextMouseProcessor;
import org.openmetromaps.maps.gwt.input.PanTouchProcessor;
import org.openmetromaps.maps.gwt.input.ZoomMouseProcessor;
import org.openmetromaps.maps.gwt.mouseevents.MouseProcessor;
import org.openmetromaps.maps.painting.core.GenericPaintFactory;
import org.openmetromaps.maps.painting.core.Painter;
import org.openmetromaps.maps.painting.gwt.GwtPainter;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import de.topobyte.formatting.Formatting;

public class ScrollableAdvancedPlanPanel extends BaseMapWindowPanel
{

	private MapModel mapModel;
	private MapView mapView;
	private LineNetwork lineNetwork;

	private MapViewStatus mapViewStatus = new MapViewStatus();
	private StationMode stationMode = StationMode.CONVEX;
	private SegmentMode segmentMode = SegmentMode.CURVE;

	private PlanRenderer renderer;
	private ContextMenu contextMenu = null;

	private boolean viewportInitialized = false;

	private boolean debugSize = false;

	public ScrollableAdvancedPlanPanel()
	{
		float devicePixelRatio = (float) getDevicePixelRatio();

		MouseProcessor panMouseHandler = new ContextMouseProcessor(this);
		MouseProcessor zoomMouseHandler = new ZoomMouseProcessor<>(this,
				devicePixelRatio, this::hideContextMenu);
		Util.addHandler(canvas, panMouseHandler);
		Util.addHandler(canvas, zoomMouseHandler);

		TouchProcessor panTouchHandler = new PanTouchProcessor<>(this);
		Util.addHandler(canvas, panTouchHandler);
	}

	public void setModel(MapModel mapModel)
	{
		this.mapModel = mapModel;
		MapView baseView = mapModel.getViews().get(0);
		double scale = Util.getDevicePixelRatio();
		mapView = scale == 1.0 ? baseView
				: ModelUtil.getScaledInstance(baseView, scale);
		lineNetwork = mapView.getLineNetwork();

		setScene(mapView.getConfig().getScene());

		initRenderer();

		// Wait until the panel is attached and sized, then zoom to fit the
		// scene
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute()
			{
				zoomFitScene();
				viewportInitialized = true;
				render();
			}
		});
	}

	public boolean isDebugSize()
	{
		return debugSize;
	}

	public void setDebugSize(boolean debugSize)
	{
		this.debugSize = debugSize;
	}

	private void initRenderer()
	{
		float scale = (float) getDevicePixelRatio();
		renderer = new PlanRenderer(lineNetwork, mapViewStatus, stationMode,
				segmentMode, this, this, scale, new GenericPaintFactory());
	}

	@Override
	public void render()
	{
		Context2d c = canvas.getContext2d();

		// g.setColor(Color.WHITE);
		// fillRect(g, scene.getX1(), scene.getY1(), scene.getX2(),
		// scene.getY2());

		fillBackground(c);

		renderContent(c);
	}

	private void fillBackground(Context2d c)
	{
		c.setFillStyle(CssColor.make("#eeeeee"));
		c.fillRect(0, 0, width, height);
	}

	private void renderContent(Context2d c)
	{
		if (!viewportInitialized || renderer == null) {
			return;
		}

		Painter painter = new GwtPainter(c);
		renderer.paint(painter);

		if (debugSize) {
			c.setFont("16px Arial");
			c.setTextAlign(TextAlign.LEFT);
			c.setFillStyle("#000000");
			c.fillText(Formatting.format("%dx%d %.2f", getWidth(), getHeight(),
					getDevicePixelRatio()), 5, 21);
		}
	}

	public PlanRenderer getPlanRenderer()
	{
		return renderer;
	}

	public LineNetwork getLineNetwork()
	{
		return lineNetwork;
	}

	public void setContextMenu(ContextMenu menu)
	{
		hideContextMenu();
		contextMenu = menu;
		if (menu != null) {
			menu.addCloseHandler(e -> {
				if (contextMenu == menu) {
					contextMenu = null;
				}
			});
		}
	}

	public void hideContextMenu()
	{
		if (contextMenu != null) {
			contextMenu.hide();
		}
	}

}
