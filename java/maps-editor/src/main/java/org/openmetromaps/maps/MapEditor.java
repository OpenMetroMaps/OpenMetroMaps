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

package org.openmetromaps.maps;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.actions.AboutAction;
import org.openmetromaps.maps.actions.DebugRanksAction;
import org.openmetromaps.maps.actions.DebugTangentsAction;
import org.openmetromaps.maps.actions.ExitAction;
import org.openmetromaps.maps.actions.LicenseAction;
import org.openmetromaps.maps.actions.OpenAction;
import org.openmetromaps.maps.actions.SaveAction;
import org.openmetromaps.maps.actions.SaveAsAction;
import org.openmetromaps.maps.actions.ShowLabelsAction;
import org.openmetromaps.maps.dockables.DockableHelper;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.Node;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import de.topobyte.adt.geo.Coordinate;
import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.geomath.WGS84;
import de.topobyte.swing.util.EmptyIcon;
import de.topobyte.swing.util.JMenus;
import de.topobyte.swing.util.action.enums.DefaultAppearance;
import de.topobyte.swing.util.action.enums.EnumActions;

public class MapEditor
{

	private MapModel model;
	private MapView view;

	private ViewConfig viewConfig;

	private JFrame frame;

	private CControl control;
	private CGrid grid;

	private MapViewStatus mapViewStatus;
	private ScrollableAdvancedPanel map;
	private StatusBar statusBar;

	private StationPanel stationPanel;
	private DefaultSingleCDockable stationPanelDockable;

	private List<DataChangeListener> dataChangeListeners;

	public MapEditor(MapModel model)
	{
		init(model);

		dataChangeListeners = new ArrayList<>();
	}

	public void setModel(MapModel model)
	{
		init(model);
		map.setData(model.getData(), view.getLineNetwork());
		map.setViewConfig(viewConfig.getBbox(), viewConfig.getStartPosition(),
				Constants.DEFAULT_ZOOM);
	}

	private void init(MapModel model)
	{
		this.model = model;

		mapViewStatus = new MapViewStatus();

		if (model.getViews().isEmpty()) {
			LineNetworkBuilder builder = new LineNetworkBuilder(
					model.getData());
			LineNetwork lineNetwork = builder.getGraph();
			model.getViews().add(new MapView("Test", lineNetwork));
		}

		view = model.getViews().get(0);

		viewConfig = ModelUtil.viewConfig(model.getData());
	}

	public MapModel getModel()
	{
		return model;
	}

	public MapView getView()
	{
		return view;
	}

	public Window getFrame()
	{
		return frame;
	}

	public ScrollableAdvancedPanel getMap()
	{
		return map;
	}

	public StatusBar getStatusBar()
	{
		return statusBar;
	}

	public void addDataChangeListener(DataChangeListener listener)
	{
		dataChangeListeners.add(listener);
	}

	public void removeDataChangeListener(DataChangeListener listener)
	{
		dataChangeListeners.remove(listener);
	}

	void triggerDataChanged()
	{
		for (DataChangeListener listener : dataChangeListeners) {
			listener.dataChanged();
		}
	}

	public void show()
	{
		frame = new JFrame("Map Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);

		build();

		frame.setVisible(true);
	}

	private void build()
	{
		setupContent();
		setupMenu();

		MapEditorMouseEventProcessor mep = new MapEditorMouseEventProcessor(
				this);
		map.setMouseProcessor(mep);
	}

	private void setupMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);

		JMenu menuView = new JMenu("View");
		menuBar.add(menuView);

		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		setupMenuFile(menuFile);
		setupMenuView(menuView);
		setupMenuHelp(menuHelp);
	}

	private void setupMenuFile(JMenu menuFile)
	{
		menuFile.add(new OpenAction(this));
		menuFile.add(new SaveAction(this));
		menuFile.add(new SaveAsAction(this));
		menuFile.add(new ExitAction());
	}

	private void setupMenuView(JMenu menuView)
	{
		JMenus.addCheckbox(menuView, new ShowLabelsAction(this),
				KeyEvent.VK_F2);
		JMenu stationMode = submenu("Station mode");
		JMenu segmentMode = submenu("Segment mode");
		menuView.add(stationMode);
		menuView.add(segmentMode);
		JMenus.addCheckbox(menuView, new DebugTangentsAction(this),
				KeyEvent.VK_F3);
		JMenus.addCheckbox(menuView, new DebugRanksAction(this),
				KeyEvent.VK_F4);

		PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

		EnumActions.add(stationMode, StationMode.class, changeSupport,
				"station-mode", StationMode.CONVEX, x -> setStationMode(x),
				new DefaultAppearance<>());
		EnumActions.add(segmentMode, SegmentMode.class, changeSupport,
				"segment-mode", SegmentMode.CURVE, x -> setSegmentMode(x),
				new DefaultAppearance<>());
	}

	private void setupMenuHelp(JMenu menuHelp)
	{
		menuHelp.add(new AboutAction(frame));
		menuHelp.add(new LicenseAction(frame));
	}

	private void setStationMode(StationMode mode)
	{
		map.getPlanRenderer().setStationMode(mode);
		map.repaint();
	}

	private void setSegmentMode(SegmentMode mode)
	{
		map.getPlanRenderer().setSegmentMode(mode);
		map.repaint();
	}

	private JMenu submenu(String string)
	{
		JMenu menu = new JMenu(string);
		menu.setIcon(new EmptyIcon(24));
		return menu;
	}

	private void setupContent()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		frame.setContentPane(panel);

		map = new ScrollableAdvancedPanel(model.getData(),
				view.getLineNetwork(), mapViewStatus,
				PlanRenderer.StationMode.CONVEX, PlanRenderer.SegmentMode.CURVE,
				viewConfig.getStartPosition(), 10, 15, viewConfig.getBbox());

		statusBar = new StatusBar();

		control = new CControl(frame);

		GridBagConstraintsEditor c = new GridBagConstraintsEditor();
		c.weight(1, 1).fill(GridBagConstraints.BOTH);
		panel.add(control.getContentArea(), c.getConstraints());
		c.weight(1, 0).fill(GridBagConstraints.HORIZONTAL);
		c.gridPos(0, 1);
		panel.add(statusBar, c.getConstraints());

		grid = new CGrid(control);

		DefaultSingleCDockable mapDockable = new DefaultSingleCDockable("Map",
				"Map", map);
		grid.add(0, 0, 1, 1, mapDockable);
		mapDockable.setExternalizable(false);
		mapDockable.setCloseable(false);
		mapDockable.setMinimizable(false);

		setupStationPanel(true);

		control.getContentArea().deploy(grid);
	}

	protected void updateStatusBar(int x, int y)
	{
		double lon = map.getMapWindow().getPositionLon(x);
		double lat = map.getMapWindow().getPositionLat(y);

		Node node = mouseNode(x, y);

		String stationName = node == null ? "none" : node.station.getName();

		statusBar.setText(String.format(
				"Location: %d,%d, Coordinates: %.6f,%.6f, Station: %s", x, y,
				lon, lat, stationName));
	}

	protected Node mouseNode(int x, int y)
	{
		Node best = closestNode(x, y);

		double sx = map.getMapWindow().longitudeToX(best.location.lon);
		double sy = map.getMapWindow().latitudeToY(best.location.lat);

		double dx = Math.abs(sx - x);
		double dy = Math.abs(sy - y);
		double d = Math.sqrt(dx * dx + dy * dy);

		if (d < 5) {
			return best;
		}

		return null;
	}

	protected Node closestNode(int x, int y)
	{
		double lon = map.getMapWindow().getPositionLon(x);
		double lat = map.getMapWindow().getPositionLat(y);

		LineNetwork lineNetwork = map.getPlanRenderer().getLineNetwork();

		double bestDistance = Double.MAX_VALUE;
		Node best = null;

		// TODO: use an index to speed this up
		for (Node node : lineNetwork.nodes) {
			Coordinate location = node.location;
			double d = WGS84.haversineDistance(location.getLongitude(),
					location.getLatitude(), lon, lat);
			if (d < bestDistance) {
				bestDistance = d;
				best = node;
			}
		}

		return best;
	}

	void select(Node node)
	{
		mapViewStatus.selectNoNodes();
		mapViewStatus.selectNode(node);
		updateStationPanel();
	}

	void toggleSelected(Node node)
	{
		if (mapViewStatus.isNodeSelected(node)) {
			mapViewStatus.unselectNode(node);
		} else {
			mapViewStatus.selectNode(node);
		}
		updateStationPanel();
	}

	void selectNone()
	{
		mapViewStatus.selectNoNodes();
		updateStationPanel();
	}

	private void updateStationPanel()
	{
		if (mapViewStatus.getNumSelectedNodes() == 1) {
			Node node = mapViewStatus.getSelectedNodes().iterator().next();
			stationPanel.setNode(node);
		} else {
			stationPanel.setNode(null);
		}
	}

	void setupStationPanel(boolean show)
	{
		stationPanel = new StationPanel(this);

		stationPanelDockable = new DefaultSingleCDockable("station-panel",
				"Station Panel", stationPanel);

		grid.add(1, 0, 0.3, 1, stationPanelDockable);

		stationPanelDockable.setVisible(show);
		DockableHelper.setDefaultOptions(stationPanelDockable);
	}

}
