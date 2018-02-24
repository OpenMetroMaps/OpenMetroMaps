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

package org.openmetromaps.model.osm.inspector.actions;

import java.awt.event.ActionEvent;

import org.openmetromaps.model.osm.LinesAnalyzer;
import org.openmetromaps.model.osm.inspector.ModelInspector;
import org.openmetromaps.model.osm.inspector.TextDialog;
import org.openmetromaps.swing.Util;

import de.topobyte.lineprinter.LineBufferPrinter;
import de.topobyte.swing.util.action.SimpleAction;

public class AnalyzeLinesAction extends SimpleAction
{

	private static final long serialVersionUID = 1L;

	private ModelInspector modelInpector;

	public AnalyzeLinesAction(ModelInspector modelInpector)
	{
		super("Analyze Lines", "Analyze lines");
		this.modelInpector = modelInpector;
		setIcon("res/images/24/system-run.png");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		LinesAnalyzer analyzer = new LinesAnalyzer(modelInpector.getModel());
		LineBufferPrinter buffer = new LineBufferPrinter();
		analyzer.analyze(buffer, false);

		TextDialog dialog = new TextDialog(modelInpector.getFrame(),
				"Line Analyis", buffer.getLines());
		Util.showRelativeToOwner(dialog, 400, 300);
	}

}
