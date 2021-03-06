// Copyright 2019 Sebastian Kuerten
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

import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.openmetromaps.maps.editor.algorithms.HeavyComputationOptimization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeavyComputationAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory
			.getLogger(HeavyComputationAction.class);

	private static final long serialVersionUID = 1L;

	public HeavyComputationAction(MapEditor mapEditor)
	{
		super(mapEditor, "Heavy computation demo",
				"Run an algorithm that takes a while to finish and update view repeatedly");
		setIcon("res/images/24/system-run.png");
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		HeavyComputationOptimization optimization = new HeavyComputationOptimization();
		optimization.runOptimization(mapEditor, 120, 500);
	}

}
