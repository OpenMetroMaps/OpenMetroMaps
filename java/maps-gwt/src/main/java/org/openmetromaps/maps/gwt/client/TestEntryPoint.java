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

package org.openmetromaps.maps.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class TestEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
		RootLayoutPanel.get().add(dock);

		Label title = new Label("Below is a simple panel");
		Anchor linkOMM = new Anchor("OpenMetroMaps", false, "http://www.openmetromaps.org");

		FlowPanel headline = new FlowPanel();
		headline.getElement().getStyle().setMarginTop(1, Unit.EM);
		headline.getElement().getStyle().setMarginBottom(1, Unit.EM);
		headline.getElement().getStyle().setMarginLeft(10, Unit.PX);
		headline.getElement().getStyle().setMarginRight(10, Unit.PX);

		headline.add(linkOMM);
		headline.add(title);

		linkOMM.getElement().getStyle().setMarginLeft(10, Unit.PX);

		title.getElement().getStyle().setFloat(Float.LEFT);

		dock.addNorth(headline, 2);

		TestPanel test = new TestPanel();
		dock.add(test);
	}

}
