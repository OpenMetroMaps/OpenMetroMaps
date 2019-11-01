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

package org.openmetromaps.maps.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.MapViewStatus;
import org.openmetromaps.maps.PlanRenderer;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.painting.awt.AwtPainter;
import org.openmetromaps.maps.painting.core.GenericPaintFactory;

import de.topobyte.viewports.geometry.Rectangle;

public class ImageUtil
{

	public static void createPng(MapModel model, Path path, int width,
			int height, double x, double y, double zoom,
			StationMode stationMode, SegmentMode segmentMode) throws IOException
	{
		createImage(model, path, "png", width, height, x, y, zoom, stationMode,
				segmentMode);
	}

	public static void createImage(MapModel model, Path path, String format,
			int width, int height, double x, double y, double zoom,
			StationMode stationMode, SegmentMode segmentMode) throws IOException
	{
		MapView view = model.getViews().get(0);
		LineNetwork lineNetwork = view.getLineNetwork();
		MapViewStatus mapViewStatus = new MapViewStatus();

		Rectangle scene = view.getConfig().getScene();
		ImageView imageView = new ImageView(scene, width, height);
		imageView.setZoom(zoom);
		imageView.setPositionX(x);
		imageView.setPositionY(y);

		PlanRenderer planRenderer = new PlanRenderer(lineNetwork, mapViewStatus,
				stationMode, segmentMode, imageView, imageView, 1,
				new GenericPaintFactory());

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		AwtPainter painter = new AwtPainter(g);

		planRenderer.paint(painter);
		planRenderer.setRenderLabels(true);

		ImageIO.write(image, format, path.toFile());
	}

}
