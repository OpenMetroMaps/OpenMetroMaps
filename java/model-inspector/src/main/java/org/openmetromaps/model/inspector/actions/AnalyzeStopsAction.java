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

package org.openmetromaps.model.inspector.actions;

import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.openmetromaps.model.StopsAnalyzer;
import org.openmetromaps.model.inspector.ModelInspector;
import org.openmetromaps.model.inspector.TextDialog;
import org.openmetromaps.model.inspector.Util;

import de.topobyte.swing.util.action.SimpleAction;

public class AnalyzeStopsAction extends SimpleAction
{

	private static final long serialVersionUID = 1L;

	private ModelInspector modelInpector;

	public AnalyzeStopsAction(ModelInspector modelInpector)
	{
		super("Analyze Stops",
				"Analyze the roles of public transport relations");
		this.modelInpector = modelInpector;
		setIcon("res/images/24/system-run.png");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		StopsAnalyzer analyzer = new StopsAnalyzer(modelInpector.getModel());
		StringWriter buf = new StringWriter();
		analyzer.analyze(new PrintWriter(buf));

		String output = buf.toString();
		TextDialog dialog = new TextDialog(modelInpector.getFrame(),
				"Stops Analyis", output);
		Util.showRelativeToOwner(dialog, 400, 300);
	}

}
