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

import java.util.function.Function;

import def.dom.CanvasRenderingContext2D;
import def.dom.Element;
import def.dom.HTMLCanvasElement;
import def.dom.UIEvent;
import jsweet.util.StringTypes;

public class Drawing
{

	public static void main(String[] args)
	{
		window.onload = (e) -> {
			return new Drawing();
		};
	}

	private HTMLCanvasElement canvas;
	private CanvasRenderingContext2D ctx;

	public Drawing()
	{
		console.info("constructor");
		canvas = (HTMLCanvasElement) document.getElementById("canvas");

		updateCanvasSize();

		window.onresize = new Function<UIEvent, Object>() {

			@Override
			public Object apply(UIEvent t)
			{
				updateCanvasSize();
				return null;
			}

		};
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
	}

}