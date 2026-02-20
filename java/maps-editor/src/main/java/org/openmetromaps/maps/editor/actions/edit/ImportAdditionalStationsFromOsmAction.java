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

package org.openmetromaps.maps.editor.actions.edit;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openmetromaps.heavyutil.regression.CoordinateRegression;
import org.openmetromaps.heavyutil.regression.RegressionCoordinateConverter;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.editor.actions.MapEditorAction;
import org.openmetromaps.maps.editor.history.MapEditorSnapshot;
import org.openmetromaps.maps.editor.model.ImportAdditionalStationsFromOsmDialog;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.swing.util.EmptyIcon;
import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.common.errors.OsmApiException;
import de.westnordost.osmapi.map.MapDataApi;

public class ImportAdditionalStationsFromOsmAction extends MapEditorAction
{

	private static final long serialVersionUID = 1L;

	final static Logger logger = LoggerFactory
			.getLogger(ImportAdditionalStationsFromOsmAction.class);

	public ImportAdditionalStationsFromOsmAction(MapEditor mapEditor)
	{
		super(mapEditor, "Import additional stations from OSM",
				"Import a set of new stations into the map from OpenStreetMap.");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		ImportAdditionalStationsFromOsmDialog dialog = new ImportAdditionalStationsFromOsmDialog(
				mapEditor.getFrame());
		dialog.setModal(true);

		dialog.pack();
		dialog.setLocationRelativeTo(mapEditor.getFrame());
		dialog.setVisible(true);

		if (!dialog.isOk()) {
			return;
		}

		List<Long> nodeIds = dialog.getNodeIds();
		if (nodeIds.isEmpty()) {
			return;
		}

		try {
			importNodes(nodeIds);
		} catch (Exception ex) {
			logger.error("Error importing nodes from OSM", ex);
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Error importing nodes: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void importNodes(List<Long> nodeIds)
	{
		List<de.westnordost.osmapi.map.data.Node> osmNodes;
		try {
			osmNodes = fetchNodes(nodeIds);
		} catch (OsmApiException e) {
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"An error occurred while fetching data from OSM: "
							+ e.getMessage(),
					"Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (osmNodes.size() != nodeIds.size()) {
			// Ideally we would show which ones failed, but for now we just warn
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Some nodes could not be fetched from OSM.", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}

		if (osmNodes.isEmpty()) {
			return;
		}

		RegressionCoordinateConverter converter;
		try {
			converter = CoordinateRegression
					.createRegressionConverter(mapEditor.getModel());
		} catch (Exception e) {
			logger.error("Error creating coordinate converter", e);
			JOptionPane.showMessageDialog(mapEditor.getFrame(),
					"Could not create coordinate converter. Make sure you have enough stations with WGS84 coordinates.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		convertAndImport(osmNodes, converter);
	}

	private List<de.westnordost.osmapi.map.data.Node> fetchNodes(
			List<Long> nodeIds)
	{
		int timeout = 10000;

		OsmConnection connection = new OsmConnection(
				"https://api.openstreetmap.org/api/0.6/",
				"OpenMetroMaps-Editor", null, timeout);

		MapDataApi mapDataApi = new MapDataApi(connection);

		return mapDataApi.getNodes(nodeIds);
	}

	private void convertAndImport(
			List<de.westnordost.osmapi.map.data.Node> osmNodes,
			RegressionCoordinateConverter converter)
	{
		// TODO: implement a more memory efficient undo command
		MapEditorSnapshot before = mapEditor.captureSnapshot();

		ModelData modelData = mapEditor.getModel().getData();
		LineNetwork lineNetwork = mapEditor.getView().getLineNetwork();

		int maxId = -1;
		for (Station s : modelData.stations) {
			maxId = Math.max(maxId, s.getId());
		}
		int nextId = maxId + 1;

		for (de.westnordost.osmapi.map.data.Node osmNode : osmNodes) {
			String name = osmNode.getTags().get("name");
			if (name == null) {
				name = "OSM Node " + osmNode.getId();
			}

			double lon = osmNode.getPosition().getLongitude();
			double lat = osmNode.getPosition().getLatitude();

			Station station = new Station(nextId++, name,
					new Coordinate(lon, lat), new ArrayList<>());

			modelData.stations.add(station);

			Node node = new Node(station);
			node.location = converter.convert(new Point(lon, lat));
			System.out.println(String.format("%f, %f",
					station.getLocation().getLongitude(),
					station.getLocation().getLatitude()));
			System.out.println(
					String.format("%f, %f", node.location.x, node.location.y));
			lineNetwork.nodes.add(node);
			lineNetwork.getStationToNode().put(station, node);
		}

		MapEditorSnapshot after = mapEditor.captureSnapshot();
		mapEditor.getHistory().record("Import additional stations from OSM",
				before, after);

		mapEditor.triggerDataChanged();
		mapEditor.getMap().repaint();

	}

}
