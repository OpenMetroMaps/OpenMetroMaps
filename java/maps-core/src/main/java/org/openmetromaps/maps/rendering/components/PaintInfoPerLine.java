package org.openmetromaps.maps.rendering.components;

import java.util.List;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.painting.core.IPaintInfo;
import org.openmetromaps.maps.painting.core.PaintFactory;

public class PaintInfoPerLine
{

	public static interface Initializer
	{
		public IPaintInfo init(PaintFactory pf, Line line);
	}

	private PaintFactory pf;
	private IPaintInfo[] lineToPaintForLines;
	private List<Line> lines;
	private Initializer initializer;

	public PaintInfoPerLine(PaintFactory pf, List<Line> lines,
			Initializer initializer)
	{
		this.pf = pf;
		this.lines = lines;
		this.initializer = initializer;

		init();
	}

	private void init()
	{
		lineToPaintForLines = new IPaintInfo[lines.size()];
		for (Line line : lines) {
			lineToPaintForLines[line.getId()] = initializer.init(pf, line);
		}
	}

	public IPaintInfo get(Line line)
	{
		return lineToPaintForLines[line.getId()];
	}

}
