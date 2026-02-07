// Copyright 2026 Sebastian Kuerten
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

package de.topobyte.bvg.icons;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.formdev.flatlaf.FlatDarculaLaf;

import de.topobyte.bvg.BvgIO;
import de.topobyte.bvg.BvgImage;
import de.topobyte.bvg.Color;
import de.topobyte.melon.resources.Resources;

// TODO: port back to bvg
public class BvgIcon implements Icon
{

	final static Logger logger = LoggerFactory.getLogger(BvgIcon.class);

	private BvgImage image = null;
	private int width;
	private int height;

	public BvgIcon(String resource, int size)
	{
		this.width = size;
		this.height = size;
		try (InputStream stream = Resources.stream(resource)) {
			image = BvgIO.read(stream);
		} catch (IOException | NullPointerException e) {
			logger.warn(String.format("Unable to load icon resource '%s'",
					resource));
		}
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		if (image == null) {
			return;
		}

		Color tint = isDarkTheme() ? new Color(0xCCFFFFFF, true)
				: new Color(0xFF000000, true);

		float sx = (float) (width / image.getWidth());
		float sy = (float) (height / image.getHeight());
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		BvgAwtPainter.draw(g2d, image, x, y, sx, sy, color -> {
			if (color.getColorCode() == 0xFF000000) {
				return tint;
			}
			return color;
		});
	}

	private static boolean isDarkTheme()
	{
		return UIManager.getLookAndFeel() instanceof FlatDarculaLaf;
	}

	@Override
	public int getIconWidth()
	{
		return width;
	}

	@Override
	public int getIconHeight()
	{
		return height;
	}

}
