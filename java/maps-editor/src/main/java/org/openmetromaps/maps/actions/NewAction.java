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

package org.openmetromaps.maps.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.MapEditor;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.geo.Coordinate;

public class NewAction extends MapEditorAction
{

	final static Logger logger = LoggerFactory.getLogger(NewAction.class);

	private static final long serialVersionUID = 1L;

	public NewAction(MapEditor mapEditor)
	{
		super(mapEditor, "New", "Create a new document");
		setIcon("res/images/24/document-new.png");
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		// TODO: if there are pending changes, let user confirm
		// TODO: avoid dummy node creation
		List<Line> lines = new ArrayList<>();
		List<Station> stations = new ArrayList<>();
		ModelData data = new ModelData(lines, stations);
		MapModel model = new MapModel(data);

		stations.add(new Station(0, "Test", new Coordinate(10, 10),
				new ArrayList<>()));

		mapEditor.setModel(model);
		mapEditor.getMap().repaint();
	}

}
