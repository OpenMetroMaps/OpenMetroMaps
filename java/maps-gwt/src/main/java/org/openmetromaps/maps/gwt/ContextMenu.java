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

package org.openmetromaps.maps.gwt;

import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Coordinate;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

import de.topobyte.formatting.Formatting;

public class ContextMenu extends PopupPanel
{

	public ContextMenu(Node node)
	{
		super(true);

		Style style = getElement().getStyle();
		style.setBackgroundColor("#fff");
		style.setPadding(0.5, Unit.EM);
		style.setProperty("borderRadius", 5, Unit.PX);

		String name = node.station.getName();

		FlowPanel panel = new FlowPanel();
		setWidget(panel);

		panel.add(new Label(name));

		Coordinate location = node.station.getLocation();

		String osm = Formatting.format(
				"https://www.openstreetmap.org/#map=17/%f/%f",
				location.getLatitude(), location.getLongitude());

		panel.add(new Anchor("map", osm));
	}

}
