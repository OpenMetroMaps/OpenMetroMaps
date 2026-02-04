package org.openmetromaps.maps.editor.icons;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.bvg.BvgAwtPainter;
import de.topobyte.bvg.BvgIO;
import de.topobyte.bvg.BvgImage;
import de.topobyte.melon.resources.Resources;

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
		float sx = (float) (width / image.getWidth());
		float sy = (float) (height / image.getHeight());
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		BvgAwtPainter.draw(g2d, image, x, y, sx, sy);
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
