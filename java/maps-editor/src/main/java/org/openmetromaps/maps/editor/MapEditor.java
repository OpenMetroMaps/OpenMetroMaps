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

package org.openmetromaps.maps.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
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
import org.openmetromaps.maps.editor.actions.algorithms.DummyOptimizationAction;
import org.openmetromaps.maps.editor.actions.algorithms.HeavyComputationAction;
import org.openmetromaps.maps.editor.actions.algorithms.StraightenAxisParallelLinesAction;
import org.openmetromaps.maps.editor.actions.edit.AlignHorizontallyAction;
import org.openmetromaps.maps.editor.actions.edit.AlignVerticallyAction;
import org.openmetromaps.maps.editor.actions.edit.DistributeEvenlyAction;
import org.openmetromaps.maps.editor.actions.edit.DocumentPropertiesAction;
import org.openmetromaps.maps.editor.actions.edit.EditLineStationsAction;
import org.openmetromaps.maps.editor.actions.edit.RedoAction;
import org.openmetromaps.maps.editor.actions.edit.SelectAllAction;
import org.openmetromaps.maps.editor.actions.edit.SelectLinesAction;
import org.openmetromaps.maps.editor.actions.edit.SelectNodesInBetweenAction;
import org.openmetromaps.maps.editor.actions.edit.UndoAction;
import org.openmetromaps.maps.editor.actions.file.ExitAction;
import org.openmetromaps.maps.editor.actions.file.NewAction;
import org.openmetromaps.maps.editor.actions.file.OpenAction;
import org.openmetromaps.maps.editor.actions.file.SaveAction;
import org.openmetromaps.maps.editor.actions.file.SaveAsAction;
import org.openmetromaps.maps.editor.actions.file.SettingsAction;
import org.openmetromaps.maps.editor.actions.help.AboutAction;
import org.openmetromaps.maps.editor.actions.help.LicenseAction;
import org.openmetromaps.maps.editor.dockables.DockableHelper;
import org.openmetromaps.maps.editor.history.Capture;
import org.openmetromaps.maps.editor.history.MapEditorHistory;
import org.openmetromaps.maps.editor.history.MapEditorSnapshot;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.swing.Theming;
import org.openmetromaps.swing.actions.ActionHelper;
import org.openmetromaps.swing.config.Configuration;
import org.openmetromaps.swing.config.ConfigurationStorage;
import org.openmetromaps.swing.config.VolatileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;
import de.topobyte.melon.resources.Resources;
import de.topobyte.swing.util.ActionStateButton;
import de.topobyte.swing.util.EmptyIcon;
import de.topobyte.swing.util.JMenus;
import de.topobyte.swing.util.ToggleAction;
import de.topobyte.swing.util.action.enums.BooleanValueHolder;
import de.topobyte.swing.util.action.enums.DefaultAppearance;
import de.topobyte.swing.util.action.enums.EnumActions;
import de.topobyte.swing.util.action.enums.EnumValueHolder;
import de.topobyte.viewports.scrolling.PanMouseAdapter;
import de.topobyte.viewports.scrolling.ScrollableView;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class MapEditor
{

	final static Logger logger = LoggerFactory.getLogger(MapEditor.class);

	private Configuration configuration;
	private VolatileConfiguration volatileConfiguration;

	private MapModel model;
	private MapView view;
	private MapViewStatus mapViewStatus;

	private Path source;

	private ViewConfig viewConfig;

	private JFrame frame;
	private JToolBar toolbar;

	private CControl control;
	private CGrid grid;

	private ScrollableAdvancedPanel map;
	private StatusBar statusBar;

	private StationPanel stationPanel;
	private DefaultSingleCDockable stationPanelDockable;

	private ViewportPanel viewportPanel;
	private DefaultSingleCDockable viewportPanelDockable;

	private List<DataChangeListener> dataChangeListeners;

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	private BooleanValueHolder antialiasing = new BooleanValueHolder(
			changeSupport, "antialiasing", x -> setAntialiasingInternal(),
			true);

	private BooleanValueHolder showLabels = new BooleanValueHolder(
			changeSupport, "show-labels", x -> setShowLabelsInternal(), true);

	private BooleanValueHolder showStationCenters = new BooleanValueHolder(
			changeSupport, "show-station-centers",
			x -> setShowStationCentersInternal(), false);

	private BooleanValueHolder debugTangents = new BooleanValueHolder(
			changeSupport, "debug-tangents", x -> setDebugTangentsInternal(),
			false);

	private BooleanValueHolder debugRanks = new BooleanValueHolder(
			changeSupport, "debug-ranks", x -> setDebugRanksInternal(), false);

	private EnumValueHolder<StationMode> stationMode = new EnumValueHolder<>(
			changeSupport, "station-mode", x -> setStationModeInternal(),
			StationMode.CONVEX);

	private EnumValueHolder<SegmentMode> segmentMode = new EnumValueHolder<>(
			changeSupport, "segment-mode", x -> setSegmentModeInternal(),
			SegmentMode.CURVE);

	private MapEditorHistory history = new MapEditorHistory();

	public MapEditor(MapModel model, Path source)
	{
		this.source = source;
		configuration = ConfigurationStorage.loadConfiguration();
		volatileConfiguration = ConfigurationStorage
				.loadVolatileConfiguration();

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

	public Configuration getConfiguration()
	{
		return configuration;
	}

	public VolatileConfiguration getVolatileConfiguration()
	{
		return volatileConfiguration;
	}

	public void applyConfiguration(Configuration configuration)
	{
		this.configuration = configuration;
		Theming.applyTheme(configuration.getTheme());
		if (frame != null) {
			SwingUtilities.updateComponentTreeUI(frame);
		}
		if (control != null) {
			control.setTheme(configuration.getDockingFramesTheme());
		}
	}

	public void setModel(MapModel model)
	{
		init(model);
		history.clear();
		map.setData(model.getData(), view.getLineNetwork(), mapViewStatus);
		map.setViewConfig(viewConfig, Constants.DEFAULT_ZOOM);
		selectNone();
		syncMapState();
	}

	public StationPanel getStationPanel()
	{
		return stationPanel;
	}

	public void setStationPanel(StationPanel stationPanel)
	{
		this.stationPanel = stationPanel;
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

	public boolean isAntialiasing()
	{
		return antialiasing.getValue();
	}

	public void setAntialiasing(boolean antialiasing)
	{
		this.antialiasing.setValue(antialiasing);
	}

	public void setAntialiasingInternal()
	{
		map.setAntialiasing(antialiasing.getValue());
		map.repaint();
	}

	public boolean isDebugTangents()
	{
		return debugTangents.getValue();
	}

	public void setDebugTangents(boolean debugTangents)
	{
		this.debugTangents.setValue(debugTangents);
	}

	public void setDebugTangentsInternal()
	{
		map.getPlanRenderer().setDebugTangents(debugTangents.getValue());
		map.repaint();
	}

	public boolean isDebugRanks()
	{
		return debugRanks.getValue();
	}

	public void setDebugRanks(boolean debugRanks)
	{
		this.debugRanks.setValue(debugRanks);
	}

	public void setDebugRanksInternal()
	{
		map.getPlanRenderer().setDebugRanks(debugRanks.getValue());
		map.repaint();
	}

	public StationMode getStationMode()
	{
		return stationMode.getValue();
	}

	private void syncMapState()
	{
		map.setAntialiasing(antialiasing.getValue());
		PlanRenderer planRenderer = map.getPlanRenderer();
		planRenderer.setRenderLabels(showLabels.getValue());
		planRenderer.setRenderStationCenters(showStationCenters.getValue());
		planRenderer.setStationMode(stationMode.getValue());
		planRenderer.setSegmentMode(segmentMode.getValue());
		planRenderer.setDebugTangents(debugTangents.getValue());
		planRenderer.setDebugRanks(debugRanks.getValue());
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

	public JFrame getFrame()
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

	public MapEditorHistory getHistory()
	{
		return history;
	}

	public MapEditorSnapshot captureSnapshot()
	{
		return MapEditorSnapshot.capture(this);
	}

	public Capture beginHistory(String name)
	{
		return history.begin(name, captureSnapshot());
	}

	public void endHistory(Capture capture)
	{
		history.end(capture, captureSnapshot());
	}

	public void recordHistory(String name, MapEditorSnapshot before)
	{
		history.record(name, before, captureSnapshot());
	}

	public void addDataChangeListener(DataChangeListener listener)
	{
		dataChangeListeners.add(listener);
	}

	public void removeDataChangeListener(DataChangeListener listener)
	{
		dataChangeListeners.remove(listener);
	}

	public void triggerDataChanged()
	{
		for (DataChangeListener listener : dataChangeListeners) {
			listener.dataChanged();
		}
	}

	public void show()
	{
		frame = new JFrame("Map Editor");
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

		MapEditorMouseEventProcessor mep = new MapEditorMouseEventProcessor(
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

		JMenu menuEdit = new JMenu("Edit");
		menuBar.add(menuEdit);

		JMenu menuView = new JMenu("View");
		menuBar.add(menuView);

		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		setupMenuFile(menuFile);
		setupMenuEdit(menuEdit);
		setupMenuView(menuView);
		setupMenuHelp(menuHelp);
	}

	private void setupMenuFile(JMenu menuFile)
	{
		SaveAction save = new SaveAction(this);

		JMenus.addItem(menuFile, new NewAction(this), KeyEvent.CTRL_DOWN_MASK,
				KeyEvent.VK_N);
		JMenus.addItem(menuFile, new OpenAction(this), KeyEvent.CTRL_DOWN_MASK,
				KeyEvent.VK_O);
		JMenus.addItem(menuFile, save, KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_S);
		JMenus.addItem(menuFile, new SaveAsAction(this),
				KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK,
				KeyEvent.VK_S);
		JMenus.addItem(menuFile, new SettingsAction(this),
				KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_P);
		JMenus.addItem(menuFile, new ExitAction(this), KeyEvent.CTRL_DOWN_MASK,
				KeyEvent.VK_Q);

		toolbar.add(save);
	}

	private void setupMenuEdit(JMenu menuEdit)
	{
		UndoAction undo = new UndoAction(this);
		RedoAction redo = new RedoAction(this);
		AlignHorizontallyAction alignHorizontally = new AlignHorizontallyAction(
				this);
		AlignVerticallyAction alignVertically = new AlignVerticallyAction(this);

		JMenus.addItem(menuEdit, undo, KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_Z);
		JMenus.addItem(menuEdit, redo,
				KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK,
				KeyEvent.VK_Z);
		JMenus.addItem(menuEdit, new DocumentPropertiesAction(this),
				KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK,
				KeyEvent.VK_P);
		JMenus.addItem(menuEdit, new SelectAllAction(this),
				KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_A);
		JMenus.addItem(menuEdit, new SelectLinesAction(this),
				KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_L);
		JMenus.addItem(menuEdit, new EditLineStationsAction(this));
		JMenus.addItem(menuEdit, new SelectNodesInBetweenAction(this),
				KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_B);
		JMenus.addItem(menuEdit, alignHorizontally, KeyEvent.CTRL_DOWN_MASK,
				KeyEvent.VK_H);
		JMenus.addItem(menuEdit, alignVertically, KeyEvent.CTRL_DOWN_MASK,
				KeyEvent.VK_V);
		JMenus.addItem(menuEdit, new DistributeEvenlyAction(this),
				KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_D);

		JMenu menuAlgorithms = new JMenu("Algorithms");
		menuEdit.add(menuAlgorithms);
		menuAlgorithms.setIcon(new EmptyIcon(24));

		JMenus.addItem(menuAlgorithms, new DummyOptimizationAction(this));
		JMenus.addItem(menuAlgorithms,
				new StraightenAxisParallelLinesAction(this));
		JMenus.addItem(menuAlgorithms, new HeavyComputationAction(this));

		toolbar.addSeparator();
		toolbar.add(undo);
		toolbar.add(redo);
		toolbar.add(alignHorizontally);
		toolbar.add(alignVertically);
	}

	private void setupMenuView(JMenu menuView)
	{
		ToggleAction toggleAntialiasing = ActionHelper
				.createToggleAntialiasingAction(antialiasing);
		ToggleAction toggleShowLabels = ActionHelper
				.createShowLabelsAction(showLabels);

		JMenus.addCheckbox(menuView, toggleAntialiasing, KeyEvent.VK_F6);
		JMenus.addCheckbox(menuView, toggleShowLabels, KeyEvent.VK_F2);
		JMenus.addCheckbox(menuView,
				ActionHelper.createShowStationCentersAction(showStationCenters),
				KeyEvent.VK_F3);
		JMenu stationMode = submenu("Station mode");
		JMenu segmentMode = submenu("Segment mode");
		menuView.add(stationMode);
		menuView.add(segmentMode);
		JMenus.addCheckbox(menuView,
				ActionHelper.createDebugTangentsAction(debugTangents),
				KeyEvent.VK_F4);
		JMenus.addCheckbox(menuView,
				ActionHelper.createDebugRanksAction(debugRanks),
				KeyEvent.VK_F5);

		EnumActions.add(stationMode, StationMode.class, this.stationMode,
				x -> setStationMode(x), new DefaultAppearance<>());
		EnumActions.add(segmentMode, SegmentMode.class, this.segmentMode,
				x -> setSegmentMode(x), new DefaultAppearance<>());

		toolbar.addSeparator();
		toolbar.add(new ActionStateButton(toggleAntialiasing));
		toolbar.add(new ActionStateButton(toggleShowLabels));
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

		toolbar = new JToolBar();
		toolbar.setFloatable(false);

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
		control.setTheme(configuration.getDockingFramesTheme());

		GridBagConstraintsEditor c = new GridBagConstraintsEditor();
		c.gridPos(0, 0).weight(1, 0).fill(GridBagConstraints.HORIZONTAL);
		panel.add(toolbar, c.getConstraints());
		c.gridPos(0, 1).weight(1, 1).fill(GridBagConstraints.BOTH);
		panel.add(control.getContentArea(), c.getConstraints());
		c.gridPos(0, 2).weight(1, 0).fill(GridBagConstraints.HORIZONTAL);
		panel.add(statusBar, c.getConstraints());

		grid = new CGrid(control);

		DefaultSingleCDockable mapDockable = new DefaultSingleCDockable("Map",
				"Map", scrollableView);
		grid.add(0, 0, 10, 10, mapDockable);
		mapDockable.setExternalizable(false);
		mapDockable.setCloseable(false);
		mapDockable.setMinimizable(false);

		setupStationPanel(true);
		setupViewportPanel(true);

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

	public void updateStationPanel()
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

		grid.add(10, 0, 3, 1, stationPanelDockable);

		stationPanelDockable.setVisible(show);
		DockableHelper.setDefaultOptions(stationPanelDockable);
	}

	void setupViewportPanel(boolean show)
	{
		viewportPanel = new ViewportPanel(this);

		viewportPanelDockable = new DefaultSingleCDockable("viewport-panel",
				"Viewport Panel", viewportPanel);

		grid.add(10, 1, 3, 1, viewportPanelDockable);

		viewportPanelDockable.setVisible(show);
		DockableHelper.setDefaultOptions(viewportPanelDockable);
	}

	public void showReallyExitDialog()
	{
		String ok = UIManager.getString("OptionPane.okButtonText");
		String cancel = UIManager.getString("OptionPane.cancelButtonText");

		Object[] options = { ok, cancel };

		int status = JOptionPane.showOptionDialog(frame, "Exit Map Editor?",
				"Confirm Exit", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, cancel);
		if (status == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

}
