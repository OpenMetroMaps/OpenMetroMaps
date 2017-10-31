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

package org.openmetromaps.js;

import static def.dom.Globals.console;
import static def.dom.Globals.document;
import static def.dom.Globals.window;
import static def.jquery.Globals.$;

import java.util.ArrayList;
import java.util.List;

import def.dom.CanvasRenderingContext2D;
import def.dom.Element;
import def.dom.HTMLCanvasElement;
import def.dom.MouseEvent;
import def.dom.NodeListOf;
import def.dom.XMLDocument;
import jsweet.util.StringTypes;

public class Drawing
{

	public static void main(String[] args)
	{
		window.onload = e -> {
			return new Drawing();
		};
	}

	private HTMLCanvasElement canvas;
	private CanvasRenderingContext2D ctx;

	private boolean initialized = false;

	private double x = 100;
	private double y = 100;

	private boolean mouseDown = false;
	private double lastX = 0;
	private double lastY = 0;

	public Drawing()
	{
		console.info("constructor");
		canvas = (HTMLCanvasElement) document.getElementById("canvas");

		updateCanvasSize();

		window.onresize = e -> {
			updateCanvasSize();
			return null;
		};

		canvas.onmousedown = e -> {
			mouseDown(e);
			return null;
		};

		canvas.onmouseout = canvas.onmouseup = e -> {
			mouseUp();
			return null;
		};

		canvas.onmousemove = e -> {
			mouseMove(e);
			return null;
		};

		String url = "berlin.xml";
		$.get(url, null, (data, result, query) -> {

			XMLDocument doc = (XMLDocument) data;
			parse(doc);
			draw();
			return null;

		}, "xml");
	}

	protected void mouseDown(MouseEvent t)
	{
		mouseDown = true;

		lastX = t.clientX;
		lastY = t.clientY;

		draw();
	}

	protected void mouseUp()
	{
		mouseDown = false;
	}

	protected void mouseMove(MouseEvent t)
	{
		if (mouseDown) {
			double diffX = t.clientX - lastX;
			x += diffX;
			lastX = t.clientX;

			double diffY = t.clientY - lastY;
			y += diffY;
			lastY = t.clientY;

			draw();
		}
	}

	private void updateCanvasSize()
	{
		Element body = document.querySelector("body");
		double height = body.clientHeight;
		double width = body.clientWidth;
		canvas.width = width - 20;
		canvas.height = height - 20;
		canvas.style.top = (body.clientHeight / 2 - height / 2 + 10) + "px";
		canvas.style.left = (body.clientWidth / 2 - width / 2 + 10) + "px";

		if (!initialized) {
			initialized = true;
			x = canvas.width / 2;
			y = canvas.height / 2;
		}

		ctx = canvas.getContext(StringTypes._2d);
		draw();
	}

	private void draw()
	{
		ctx.clearRect(0, 0, canvas.width, canvas.height);
		ctx.beginPath();
		ctx.moveTo(0, 0);
		ctx.lineTo(canvas.width, canvas.height);
		ctx.stroke();

		double radius = 5;
		ctx.beginPath();
		ctx.arc(x, y, radius, 0, Math.PI * 2);
		ctx.fill();

		radius = 10;
		ctx.beginPath();
		ctx.arc(x, y, radius, 0, Math.PI * 2);
		ctx.stroke();

		String text = "dot: " + x + " " + y + ", stations: " + names.size();
		ctx.fillText(text, 2, 10);
	}

	private List<String> names = new ArrayList<>();

	private void parse(XMLDocument doc)
	{
		NodeListOf<Element> stationss = doc.getElementsByTagName("stations");

		Element stations0 = stationss.item(0);

		NodeListOf<Element> stations = stations0
				.getElementsByTagName("station");

		for (Element station : stations) {
			String name = station.getAttribute("name");
			names.add(name);
		}
	}

}