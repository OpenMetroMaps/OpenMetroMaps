// Copyright 2018 Sebastian Kuerten
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

package org.openmetromaps.maps.viewer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.openmetromaps.maps.Constants;
import org.openmetromaps.maps.CoordinateConversionType;
import org.openmetromaps.maps.DataChangeListener;
import org.openmetromaps.maps.InitialViewportSetupListener;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.MapViewStatus;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.PlanRenderer;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.ScrollableAdvancedPanel;
import org.openmetromaps.maps.ViewConfig;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.viewer.actions.file.ExitAction;
import org.openmetromaps.maps.viewer.actions.file.OpenAction;
import org.openmetromaps.maps.viewer.actions.help.AboutAction;
import org.openmetromaps.maps.viewer.actions.help.LicenseAction;
import org.openmetromaps.maps.viewer.actions.view.ShowLabelsAction;
import org.openmetromaps.maps.viewer.actions.view.ShowMapAction;
import org.openmetromaps.maps.viewer.actions.view.ShowStationCentersAction;
import org.openmetromaps.maps.viewer.jeography.JeographyZoomAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.jeography.viewer.config.TileConfigUrl;
import de.topobyte.jeography.viewer.core.Viewer;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;
import de.topobyte.melon.resources.Resources;
import de.topobyte.swing.util.EmptyIcon;
import de.topobyte.swing.util.JMenus;
import de.topobyte.swing.util.action.enums.BooleanValueHolder;
import de.topobyte.swing.util.action.enums.DefaultAppearance;
import de.topobyte.swing.util.action.enums.EnumActions;
import de.topobyte.swing.util.action.enums.EnumValueHolder;
import de.topobyte.viewports.scrolling.PanMouseAdapter;
import de.topobyte.viewports.scrolling.ScrollableView;
import de.topobyte.viewports.scrolling.ViewportUtil;
import de.topobyte.viewports.scrolling.ZoomAction;

public class MapViewer
{

	final static Logger logger = LoggerFactory.getLogger(MapViewer.class);

	private MapModel model;
	private MapView view;
	private MapViewStatus mapViewStatus;

	private Path source;

	private ViewConfig viewConfig;

	private JFrame frame;

	private CControl control;
	private CGrid grid;

	private ScrollableAdvancedPanel map;
	private StatusBar statusBar;

	private JFrame frameMap = null;
	private Viewer viewer = null;

	private List<DataChangeListener> dataChangeListeners;

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	private BooleanValueHolder showLabels = new BooleanValueHolder(
			changeSupport, "show-labels", x -> setShowLabelsInternal(), true);

	private BooleanValueHolder showStationCenters = new BooleanValueHolder(
			changeSupport, "show-station-centers",
			x -> setShowStationCentersInternal(), false);

	private BooleanValueHolder showMap = new BooleanValueHolder(changeSupport,
			"show-map", x -> setShowMapInternal(x), false);

	private EnumValueHolder<StationMode> stationMode = new EnumValueHolder<>(
			changeSupport, "station-mode", x -> setStationModeInternal(),
			StationMode.CONVEX);

	private EnumValueHolder<SegmentMode> segmentMode = new EnumValueHolder<>(
			changeSupport, "segment-mode", x -> setSegmentModeInternal(),
			SegmentMode.CURVE);

	private ShowMapAction showMapAction;

	public MapViewer(MapModel model, Path source)
	{
		this.source = source;

		init(model);

		dataChangeListeners = new ArrayList<>();
	}

	public void setSource(Path source)
	{
		this.source = source;
	}

	public Path getSource()
	{
		return source;
	}

	public void setModel(MapModel model)
	{
		init(model);
		map.setData(model.getData(), view.getLineNetwork(), mapViewStatus);
		map.setViewConfig(viewConfig, Constants.DEFAULT_ZOOM);
		syncMapState();
	}

	public boolean isShowLabels()
	{
		return showLabels.getValue();
	}

	public void setShowLabels(boolean showLabels)
	{
		this.showLabels.setValue(showLabels);
	}

	public void setShowLabelsInternal()
	{
		map.getPlanRenderer().setRenderLabels(showLabels.getValue());
		map.repaint();
	}

	public boolean isShowStationCenters()
	{
		return showStationCenters.getValue();
	}

	public void setShowStationCenters(boolean showStationCenters)
	{
		this.showStationCenters.setValue(showStationCenters);
	}

	public void setShowStationCentersInternal()
	{
		map.getPlanRenderer()
				.setRenderStationCenters(showStationCenters.getValue());
		map.repaint();
	}

	public boolean isShowMap()
	{
		return showMap.getValue();
	}

	public void setShowMap(boolean showMap)
	{
		this.showMap.setValue(showMap);
	}

	public void setShowMapInternal(boolean visible)
	{
		// TODO: this is kind of sub-optimal, but works. Ideally, the
		// ValueHolder would support any number of additional listeners and the
		// action could subscribe to change events as a listener instead of
		// being called from here manually
		showMapAction.notifyChanged();

		if (!visible) {
			if (frameMap != null) {
				frameMap.setVisible(false);
			}
			return;
		}

		if (frameMap != null) {
			frameMap.setVisible(true);
			return;
		}

		TileConfigUrl tiles = new TileConfigUrl(1, "osm",
				"http://tile.openstreetmap.org/%d/%d/%d.png");
		tiles.setUserAgent("OpenMetroMaps MapViewer");
		viewer = new Viewer(tiles, null);
		viewer.setMouseActive(true);
		viewer.setDrawCrosshair(false);
		viewer.setDrawBorder(false);
		viewer.setDrawTileNumbers(false);

		InputMap inputMap = viewer
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,
				InputEvent.CTRL_DOWN_MASK), "Ctrl++");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
				InputEvent.CTRL_DOWN_MASK), "Ctrl+-");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				InputEvent.CTRL_DOWN_MASK), "Ctrl+1");

		ActionMap actionMap = viewer.getActionMap();
		actionMap.put("Ctrl++",
				new JeographyZoomAction(viewer, ZoomAction.Type.IN));
		actionMap.put("Ctrl+-",
				new JeographyZoomAction(viewer, ZoomAction.Type.OUT));
		actionMap.put("Ctrl+1",
				new JeographyZoomAction(viewer, ZoomAction.Type.IDENTITY));

		frameMap = new JFrame("Map");
		frameMap.add(viewer);
		frameMap.setSize(600, 500);
		frameMap.setVisible(true);

		frameMap.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e)
			{
				showMap.setValue(false);
			}

		});
	}

	public StationMode getStationMode()
	{
		return stationMode.getValue();
	}

	private void syncMapState()
	{
		PlanRenderer planRenderer = map.getPlanRenderer();
		planRenderer.setRenderLabels(showLabels.getValue());
		planRenderer.setRenderStationCenters(showStationCenters.getValue());
		planRenderer.setStationMode(stationMode.getValue());
		planRenderer.setSegmentMode(segmentMode.getValue());
	}

	private void init(MapModel model)
	{
		this.model = model;

		mapViewStatus = new MapViewStatus();

		ModelUtil.ensureView(model, CoordinateConversionType.WGS84);

		view = model.getViews().get(0);
		viewConfig = view.getConfig();
	}

	public MapModel getModel()
	{
		return model;
	}

	public MapView getView()
	{
		return view;
	}

	public MapViewStatus getMapViewStatus()
	{
		return mapViewStatus;
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

	public Viewer getMapViewer()
	{
		return viewer;
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
		frame = new JFrame("Map Viewer");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setSize(1000, 800);

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e)
			{
				showReallyExitDialog();
			}

		});

		build();

		frame.setVisible(true);

		List<Image> images = new ArrayList<>();
		for (int size : new int[] { 16, 20, 22, 24, 32, 48, 64, 72, 96, 144 }) {
			String filename = String.format("res/images/icon/icon-%d.png",
					size);
			try (InputStream is = Resources.stream(filename)) {
				images.add(ImageIO.read(is));
			} catch (Exception e) {
				logger.debug(
						String.format("unable to load image: '%s'", filename),
						e);
			}
		}
		frame.setIconImages(images);
	}

	private void build()
	{
		setupContent();
		setupMenu();
		syncMapState();

		MapViewerMouseEventProcessor mep = new MapViewerMouseEventProcessor(
				this);
		map.setMouseProcessor(mep);

		new InitialViewportSetupListener(map,
				model.getViews().get(0).getConfig().getStartPosition());
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
		JMenus.addItem(menuFile, new OpenAction(this), KeyEvent.CTRL_DOWN_MASK,
				KeyEvent.VK_O);
		JMenus.addItem(menuFile, new ExitAction(this), KeyEvent.CTRL_DOWN_MASK,
				KeyEvent.VK_Q);
	}

	private void setupMenuView(JMenu menuView)
	{
		JMenus.addCheckbox(menuView, new ShowLabelsAction(this),
				KeyEvent.VK_F2);
		JMenus.addCheckbox(menuView, new ShowStationCentersAction(this),
				KeyEvent.VK_F3);
		showMapAction = new ShowMapAction(this);
		JMenus.addCheckbox(menuView, showMapAction, KeyEvent.VK_F4);
		JMenu stationMode = submenu("Station mode");
		JMenu segmentMode = submenu("Segment mode");
		menuView.add(stationMode);
		menuView.add(segmentMode);

		EnumActions.add(stationMode, StationMode.class, this.stationMode,
				x -> setStationMode(x), new DefaultAppearance<>());
		EnumActions.add(segmentMode, SegmentMode.class, this.segmentMode,
				x -> setSegmentMode(x), new DefaultAppearance<>());
	}

	private void setupMenuHelp(JMenu menuHelp)
	{
		menuHelp.add(new AboutAction(frame));
		menuHelp.add(new LicenseAction(frame));
	}

	private void setStationMode(StationMode mode)
	{
		stationMode.setValue(mode);
	}

	private void setStationModeInternal()
	{
		map.getPlanRenderer().setStationMode(stationMode.getValue());
		map.repaint();
	}

	private void setSegmentMode(SegmentMode mode)
	{
		segmentMode.setValue(mode);
	}

	private void setSegmentModeInternal()
	{
		map.getPlanRenderer().setSegmentMode(segmentMode.getValue());
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

		map = new ScrollableAdvancedPanel(model.getData(), view, mapViewStatus,
				PlanRenderer.StationMode.CONVEX, PlanRenderer.SegmentMode.CURVE,
				10, 15, 1);

		ScrollableView<ScrollableAdvancedPanel> scrollableView = new ScrollableView<>(
				map);

		PanMouseAdapter<ScrollableAdvancedPanel> panAdapter = new PanMouseAdapter<>(
				map);
		map.addMouseListener(panAdapter);
		map.addMouseMotionListener(panAdapter);

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
				"Map", scrollableView);
		grid.add(0, 0, 10, 10, mapDockable);
		mapDockable.setExternalizable(false);
		mapDockable.setCloseable(false);
		mapDockable.setMinimizable(false);

		control.getContentArea().deploy(grid);
	}

	protected void updateStatusBar(int x, int y)
	{
		Node node = mouseNode(x, y);

		String stationName = node == null ? "none" : node.station.getName();

		statusBar.setText(String.format("Location: %d,%d, Station: %s", x, y,
				stationName));
	}

	protected Node mouseNode(int x, int y)
	{
		Node best = closestNode(x, y);

		if (best == null) {
			return null;
		}

		double sx = ViewportUtil.getViewX(map, best.location.x);
		double sy = ViewportUtil.getViewY(map, best.location.y);

		double dx = Math.abs(sx - x);
		double dy = Math.abs(sy - y);
		double d = Math.sqrt(dx * dx + dy * dy);

		if (d < 8) {
			return best;
		}

		return null;
	}

	protected Node closestNode(int vx, int vy)
	{
		double x = ViewportUtil.getRealX(map, vx);
		double y = ViewportUtil.getRealY(map, vy);

		LineNetwork lineNetwork = map.getPlanRenderer().getLineNetwork();

		double bestDistance = Double.MAX_VALUE;
		Node best = null;

		// TODO: use an index to speed this up
		for (Node node : lineNetwork.nodes) {
			Point location = node.location;
			Vector2 v1 = new Vector2(location);
			Vector2 v2 = new Vector2(x, y);
			double d = v2.sub(v1).length2();
			if (d < bestDistance) {
				bestDistance = d;
				best = node;
			}
		}

		return best;
	}

	public void showReallyExitDialog()
	{
		String ok = UIManager.getString("OptionPane.okButtonText");
		String cancel = UIManager.getString("OptionPane.cancelButtonText");

		Object[] options = { ok, cancel };

		int status = JOptionPane.showOptionDialog(frame, "Exit Map Viewer?",
				"Confirm Exit", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, cancel);
		if (status == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

}
