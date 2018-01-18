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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;

public class Util
{

	public static void load(String filename, Consumer<String> callback)
	{
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				filename);

		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request req, Response resp)
				{
					if (resp.getStatusCode() == Response.SC_OK) {
						String text = resp.getText();
						callback.accept(text);
					} else {
						Window.alert("request failed: " + resp.getStatusCode());
					}
				}

				@Override
				public void onError(Request res, Throwable throwable)
				{
					System.out.println("Error occurred while fetching data");
					Window.alert("request failed");
				}

			});
		} catch (RequestException e) {
			System.out.println("Error while fetching data");
			e.printStackTrace();
		}
	}

	public static FlowPanel headline(String titleText, Label status)
	{
		Anchor linkIndex = new Anchor("Home", false, "index.html");
		Label title = new Label(titleText);
		Anchor linkOMM = new Anchor("OpenMetroMaps", false,
				"http://www.openmetromaps.org");

		FlowPanel headline = new FlowPanel();
		headline.getElement().getStyle().setMarginTop(0.5, Unit.EM);
		headline.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
		headline.getElement().getStyle().setMarginLeft(10, Unit.PX);
		headline.getElement().getStyle().setMarginRight(10, Unit.PX);

		headline.add(linkIndex);
		headline.add(title);
		headline.add(linkOMM);
		headline.add(status);

		title.getElement().getStyle().setMarginLeft(10, Unit.PX);
		linkOMM.getElement().getStyle().setMarginLeft(10, Unit.PX);

		linkIndex.getElement().getStyle().setFloat(Float.LEFT);
		title.getElement().getStyle().setFloat(Float.LEFT);
		linkOMM.getElement().getStyle().setFloat(Float.LEFT);
		status.getElement().getStyle().setFloat(Float.RIGHT);

		return headline;
	}

	public static void addHandler(FocusWidget widget,
			MouseProcessor mouseProcessor)
	{
		widget.addMouseUpHandler(mouseProcessor);
		widget.addMouseDownHandler(mouseProcessor);
		widget.addMouseMoveHandler(mouseProcessor);
		widget.addMouseWheelHandler(mouseProcessor);
	}

	public static void addHandler(FocusWidget widget,
			TouchProcessor touchProcessor)
	{
		widget.addTouchStartHandler(touchProcessor);
		widget.addTouchEndHandler(touchProcessor);
		widget.addTouchCancelHandler(touchProcessor);
		widget.addTouchMoveHandler(touchProcessor);
	}

	public static Map<String, String> loadParameters(String parameterElementId)
	{
		Map<String, String> map = new HashMap<>();
		Element params = DOM.getElementById(parameterElementId);
		if (params == null) {
			return map;
		}
		NodeList<Element> paramList = params.getElementsByTagName("div");
		for (int i = 0; i < paramList.getLength(); i++) {
			Element element = paramList.getItem(i);
			String id = element.getId();
			String text = element.getInnerText();
			map.put(id, text);
		}
		return map;
	}

	public static boolean getBoolean(Map<String, String> params, String name,
			boolean defaultValue)
	{
		String value = params.get(name);
		if (value == null) {
			return defaultValue;
		}
		return value.equals("true");
	}

}
