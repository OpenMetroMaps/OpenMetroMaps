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

package org.openmetromaps.maps.editor.actions.algorithms;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyOptimizationAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory
			.getLogger(DummyOptimizationAction.class);

	private static final long serialVersionUID = 1L;

	public DummyOptimizationAction(MapEditor mapEditor)
	{
		super(mapEditor, "Dummy Optimization",
				"Pretend to run an optimization algorithm");
		setIcon("res/images/24/system-run.png");
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		JOptionPane.showMessageDialog(mapEditor.getFrame(),
				"This optimization does nothing intentionally", "Info",
				JOptionPane.INFORMATION_MESSAGE);
	}

}
