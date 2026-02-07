//Copyright 2014 Sebastian Kuerten

//
//This file is part of bvg.
//
//bvg is free software: you can redistribute it and/or modify
//it under the terms of the GNU Lesser General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//bvg is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with bvg. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.bvg.icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.function.Function;

import de.topobyte.bvg.BvgImage;
import de.topobyte.bvg.Fill;
import de.topobyte.bvg.IColor;
import de.topobyte.bvg.LineStyle;
import de.topobyte.bvg.PaintElement;
import de.topobyte.bvg.Stroke;
import de.topobyte.bvg.ToSwingUtil;
import de.topobyte.bvg.path.CompactPath;

public class BvgAwtPainter
{

	public static void draw(Graphics2D g, BvgImage bvg, float x, float y,
			float sx, float sy, Function<IColor, IColor> colorTransform)
	{
		AffineTransform backup = g.getTransform();
		g.translate(x, y);
		g.scale(sx, sy);

		List<PaintElement> elements = bvg.getPaintElements();
		List<CompactPath> paths = bvg.getPaths();

		for (int i = 0; i < elements.size(); i++) {
			PaintElement element = elements.get(i);
			CompactPath path = paths.get(i);

			GeneralPath p = ToSwingUtil.createPath(path);

			if (element instanceof Fill) {
				Fill fill = (Fill) element;
				IColor color = fill.getColor();
				IColor t = colorTransform.apply(color);
				Color c = new Color(t.getRed(), t.getGreen(), t.getBlue(),
						t.getAlpha());
				g.setColor(c);

				g.fill(p);
			} else if (element instanceof Stroke) {
				Stroke stroke = (Stroke) element;

				IColor color = stroke.getColor();
				IColor t = colorTransform.apply(color);
				Color c = new Color(t.getRed(), t.getGreen(), t.getBlue(),
						t.getAlpha());
				g.setColor(c);

				LineStyle lineStyle = stroke.getLineStyle();
				float width = lineStyle.getWidth();
				if (width < 0) {
					width = 0;
				}
				int cap = ToSwingUtil.getCap(lineStyle.getCap());
				int join = ToSwingUtil.getJoin(lineStyle.getJoin());
				float[] dashArray = lineStyle.getDashArray();
				float dashOffset = lineStyle.getDashOffset();
				float miterLimit = lineStyle.getMiterLimit();
				if (miterLimit < 1) {
					miterLimit = 1;
				}

				BasicStroke bs;
				if (dashArray == null) {
					bs = new BasicStroke(width, cap, join, miterLimit);
				} else {
					bs = new BasicStroke(width, cap, join, miterLimit,
							dashArray, dashOffset);
				}
				g.setStroke(bs);

				g.draw(p);
			}
		}

		g.setTransform(backup);
	}

}
