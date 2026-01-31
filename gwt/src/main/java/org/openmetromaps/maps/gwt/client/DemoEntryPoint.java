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
import org.openmetromaps.maps.gwt.ResizingAbsolutePanel;
import org.openmetromaps.maps.gwt.ScrollableAdvancedPlanPanel;
import org.openmetromaps.maps.gwt.StyleUtil;
import org.openmetromaps.maps.gwt.Util;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.maps.xml.XmlModelReader;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

import de.topobyte.xml.domabstraction.gwtimpl.GwtDocument;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class DemoEntryPoint implements EntryPoint
{

	private ScrollableAdvancedPlanPanel panel;
	private Label status;

	protected void setParameters(Map<String, String> params)
	{
		panel.setDebugSize(Util.getBoolean(params, "debug-size", false));
	}

	protected void setModel(MapModel mapModel)
	{
		panel.setModel(mapModel);
	}

	@Override
	public void onModuleLoad()
	{
		RootPanel root = RootPanel.get("gwt");

		ResizingAbsolutePanel main = new ResizingAbsolutePanel();
		root.add(main);
		StyleUtil.absolute(main, 0, 0, 0, 0, Unit.PX);

		// This is required because RootPanel does not implement ProvidesResize
		Window.addResizeHandler(e -> main.onResize());

		DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
		StyleUtil.absolute(dock, 0, 0, 0, 0, Unit.PX);
		main.add(dock);

		status = new Label("Initializing...");

		panel = new ScrollableAdvancedPlanPanel();
		dock.add(panel);

		addButtons(main);

		Map<String, String> params = Util.loadParameters("parameters");

		setParameters(params);

		String filename = params.get("file");
		Util.load(filename, xml -> parseXml(xml));
	}

	private void addButtons(AbsolutePanel main)
	{
		VerticalPanel buttons = new VerticalPanel();
		StyleUtil.absoluteTopRight(buttons, 1, 1, Unit.EM);
		main.add(buttons);

		Button buttonIn = new Button("+");
		buttonIn.setTitle("Zoom in");
		StyleUtil.setHeight(buttonIn, 3, Unit.EM);
		StyleUtil.setWidth(buttonIn, 100, Unit.PCT);
		StyleUtil.setProperty(buttonIn, "minWidth", 3, Unit.EM);
		buttons.add(buttonIn);

		Button buttonOut = new Button("-");
		buttonOut.setTitle("Zoom out");
		StyleUtil.setHeight(buttonOut, 3, Unit.EM);
		StyleUtil.setWidth(buttonOut, 100, Unit.PCT);
		StyleUtil.setProperty(buttonOut, "minWidth", 3, Unit.EM);
		buttons.add(buttonOut);

		StyleUtil.marginTop(buttonOut, 0.5, Unit.EM);

		Button buttonFit = new Button("F");
		buttonFit.setTitle("Fit to screen");
		StyleUtil.setHeight(buttonFit, 3, Unit.EM);
		StyleUtil.setWidth(buttonFit, 100, Unit.PCT);
		StyleUtil.setProperty(buttonFit, "minWidth", 3, Unit.EM);
		buttons.add(buttonFit);

		StyleUtil.marginTop(buttonFit, 0.5, Unit.EM);

		buttonIn.addClickHandler(e -> {
			zoomIn();
		});

		buttonOut.addClickHandler(e -> {
			zoomOut();
		});

		buttonFit.addClickHandler(e -> {
			zoomFit();
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

	private void zoomFit()
	{
		panel.zoomFitScene();
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
