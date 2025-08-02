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

package org.openmetromaps.maps.gwt.input;

import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.gwt.ScrollableAdvancedPlanPanel;
import org.openmetromaps.maps.gwt.Util;
import org.openmetromaps.maps.gwt.touchevents.Vector2;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.ui.Widget;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.scrolling.DragInfo;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class ContextMouseProcessor extends BaseMouseProcessor
{

	private boolean pressed = false;
	private DragInfo dragInfo = null;

	private ScrollableAdvancedPlanPanel panel;

	public ContextMouseProcessor(ScrollableAdvancedPlanPanel panel)
	{
		this.panel = panel;
	}

	@Override
	public void onMouseDown(MouseDownEvent e)
	{
		super.onMouseDown(e);
		pressed = true;
		dragInfo = new DragInfo(e.getX(), e.getY());
	}

	@Override
	public void onMouseUp(MouseUpEvent e)
	{
		super.onMouseUp(e);
		pressed = false;
		dragInfo = null;

		Node mouseNode = mouseNode(e.getX(), e.getY());
		if (mouseNode != null) {
			ContextMenu menu = new ContextMenu(mouseNode);

			Widget source = (Widget) e.getSource();
			int x = source.getAbsoluteLeft() + e.getRelativeX(source.getElement());
			int y = source.getAbsoluteTop() + e.getRelativeY(source.getElement());

			menu.setPopupPosition(x, y);
			menu.show();
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent e)
	{
		super.onMouseMove(e);
		if (pressed) {
			onMouseDragged(e);
		}
	}

	private void onMouseDragged(MouseMoveEvent e)
	{
		dragInfo.update(e.getX(), e.getY());
		Coordinate delta = dragInfo.getDeltaToLast();

		double dx = delta.getX();
		double dy = delta.getY();

		panel.move(new Vector2((float) dx, (float) dy));
		panel.render();
	}

	protected Node mouseNode(int x, int y)
	{
		Node best = closestNode(x, y);

		if (best == null) {
			return null;
		}

		double sx = ViewportUtil.getViewX(panel, best.location.x);
		double sy = ViewportUtil.getViewY(panel, best.location.y);

		double dx = Math.abs(sx - x * Util.getDevicePixelRatio());
		double dy = Math.abs(sy - y * Util.getDevicePixelRatio());
		double d = Math.sqrt(dx * dx + dy * dy);

		if (d < 8 * Util.getDevicePixelRatio()) {
			return best;
		}

		return null;
	}

	protected Node closestNode(int vx, int vy)
	{
		double x = ViewportUtil.getRealX(panel,
				vx * Util.getDevicePixelRatio());
		double y = ViewportUtil.getRealY(panel,
				vy * Util.getDevicePixelRatio());

		LineNetwork lineNetwork = panel.getPlanRenderer().getLineNetwork();

		double bestDistance = Double.MAX_VALUE;
		Node best = null;

		// TODO: use an index to speed this up
		for (Node node : lineNetwork.nodes) {
			Point location = node.location;
			de.topobyte.lightgeom.lina.Vector2 v1 = new de.topobyte.lightgeom.lina.Vector2(location);
			de.topobyte.lightgeom.lina.Vector2 v2 = new de.topobyte.lightgeom.lina.Vector2(x, y);
			double d = v2.sub(v1).length2();
			if (d < bestDistance) {
				bestDistance = d;
				best = node;
			}
		}

		return best;
	}

}
