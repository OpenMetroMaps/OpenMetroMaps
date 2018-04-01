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

package org.openmetromaps.maps.editor.actions.file;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.Edges;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ViewConfig;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

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
		List<Line> lines = new ArrayList<>();
		List<Station> stations = new ArrayList<>();
		ModelData data = new ModelData(lines, stations);
		MapModel model = new MapModel(data);

		List<Edges> edges = new ArrayList<>();

		LineNetworkBuilder builder = new LineNetworkBuilder(model.getData(),
				edges);
		LineNetwork lineNetwork = builder.getGraph();

		ViewConfig viewConfig = new ViewConfig(new Rectangle(0, 0, 1000, 1000),
				new Coordinate(500, 500));
		model.getViews()
				.add(new MapView("Test", edges, lineNetwork, viewConfig));

		mapEditor.setSource(null);

		mapEditor.setModel(model);
		mapEditor.getMap().repaint();
	}

}
