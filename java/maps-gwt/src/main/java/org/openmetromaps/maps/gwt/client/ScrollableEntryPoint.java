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

import java.util.Map;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.maps.xml.XmlModelReader;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

import de.topobyte.xml.domabstraction.gwtimpl.GwtDocument;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public abstract class ScrollableEntryPoint implements EntryPoint
{

	private BaseMapWindowPanel panel;
	private Label status;

	@Override
	public void onModuleLoad()
	{
		ResizingAbsolutePanel main = new ResizingAbsolutePanel();
		RootLayoutPanel.get().add(main);

		DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
		StyleUtil.absolute(dock, 0, 0, 0, 0, Unit.PX);
		main.add(dock);

		status = new Label("Initializing...");
		FlowPanel headline = Util
				.headline("Below is a scrollable advanced panel", status);

		dock.addNorth(headline, 2);

		panel = createPanel();
		dock.add(panel);

		addButtons(main);

		Map<String, String> params = Util.loadParameters("parameters");

		setParameters(params);

		String filename = params.get("file");
		Util.load(filename, xml -> parseXml(xml));
	}

	protected abstract BaseMapWindowPanel createPanel();

	protected abstract void setParameters(Map<String, String> params);

	protected abstract void setModel(MapModel mapModel);

	private void addButtons(AbsolutePanel main)
	{
		VerticalPanel buttons = new VerticalPanel();
		StyleUtil.absoluteTopRight(buttons, 3, 1, Unit.EM);
		main.add(buttons);

		Button buttonIn = new Button("zoom in");
		StyleUtil.setSize(buttonIn, 50, 50, Unit.PX);
		buttons.add(buttonIn);

		Button buttonOut = new Button("zoom out");
		StyleUtil.setSize(buttonOut, 50, 50, Unit.PX);
		buttons.add(buttonOut);

		StyleUtil.marginTop(buttonOut, 0.5, Unit.EM);
		buttonOut.getElement().getStyle().setMarginTop(0.5, Unit.EM);

		buttonIn.addClickHandler(e -> {
			zoomIn();
		});

		buttonOut.addClickHandler(e -> {
			zoomOut();
		});
	}

	protected void zoomIn()
	{
		panel.setZoom(panel.getZoom() * 1.1);
	}

	protected void zoomOut()
	{
		panel.setZoom(panel.getZoom() / 1.1);
	}

	protected void parseXml(String xml)
	{
		Document doc = XMLParser.parse(xml);
		GwtDocument gwtDoc = new GwtDocument(doc);
		try {
			XmlModel xmlModel = XmlModelReader.read(gwtDoc);
			status.setText("stations: " + xmlModel.getStations().size());
			MapModel mapModel = new XmlModelConverter().convert(xmlModel);
			MapView view = mapModel.getViews().get(0);
			String name = view.getName();
			status.setText("view: " + name);
			setModel(mapModel);
			panel.render();
		} catch (ParsingException e) {
			Window.alert("error while parsing document");
		}
	}

}
