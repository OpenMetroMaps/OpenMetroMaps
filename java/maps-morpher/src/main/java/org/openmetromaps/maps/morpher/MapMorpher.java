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

package org.openmetromaps.maps.morpher;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Window;
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
import javax.swing.JSlider;
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
import org.openmetromaps.maps.morpher.actions.file.ExitAction;
import org.openmetromaps.maps.morpher.actions.file.Open1Action;
import org.openmetromaps.maps.morpher.actions.file.Open2Action;
import org.openmetromaps.maps.morpher.actions.help.AboutAction;
import org.openmetromaps.maps.morpher.actions.help.LicenseAction;
import org.openmetromaps.maps.morpher.actions.view.ShowLabelsAction;
import org.openmetromaps.maps.morpher.actions.view.ShowStationCentersAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.melon.resources.Resources;
import de.topobyte.swing.util.EmptyIcon;
import de.topobyte.swing.util.JMenus;
import de.topobyte.swing.util.action.enums.BooleanValueHolder;
import de.topobyte.swing.util.action.enums.DefaultAppearance;
import de.topobyte.swing.util.action.enums.EnumActions;
import de.topobyte.swing.util.action.enums.EnumValueHolder;
import de.topobyte.viewports.scrolling.PanMouseAdapter;
import de.topobyte.viewports.scrolling.ScrollableView;

public class MapMorpher
{

	final static Logger logger = LoggerFactory.getLogger(MapMorpher.class);

	private MapModel model1;
	private MapModel model2;

	private MapModel model;
	private MapView view;
	private MapViewStatus mapViewStatus;
	private JSlider slider;

	private Path source;
	private float scale;

	private ViewConfig viewConfig;

	private JFrame frame;

	private ScrollableAdvancedPanel map;

	private List<DataChangeListener> dataChangeListeners;

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	private BooleanValueHolder showLabels = new BooleanValueHolder(
			changeSupport, "show-labels", x -> setShowLabelsInternal(), true);

	private BooleanValueHolder showStationCenters = new BooleanValueHolder(
			changeSupport, "show-station-centers",
			x -> setShowStationCentersInternal(), false);

	private EnumValueHolder<StationMode> stationMode = new EnumValueHolder<>(
			changeSupport, "station-mode", x -> setStationModeInternal(),
			StationMode.CONVEX);

	private EnumValueHolder<SegmentMode> segmentMode = new EnumValueHolder<>(
			changeSupport, "segment-mode", x -> setSegmentModeInternal(),
			SegmentMode.CURVE);

	private int sliderMin = 0;
	private int sliderMax = 100;
	private int sliderCurrent = sliderMin;

	public MapMorpher(MapModel model1, MapModel model2, Path source,
			float scale)
	{
		this.source = source;
		this.scale = scale;

		slider = new JSlider(sliderMin, sliderMax, sliderCurrent);
		slider.addChangeListener(e -> {
			adjustValue();
		});

		init(model1, model2);
		updateModel();

		dataChangeListeners = new ArrayList<>();
	}

	private void adjustValue()
	{
		if (slider.getValue() == sliderCurrent) {
			return;
		}
		sliderCurrent = slider.getValue();
		updateModel();
		map.repaint();
	}

	private void updateModel()
	{
		double relative = sliderCurrent / (double) (sliderMax - sliderMin);
		System.out.println(String.format("adjust slider to %.2f", relative));
		deriveModel(relative);
	}

	private void deriveModel(double relative)
	{
		model = MapMorphing.deriveModel(model1, model2, relative);

		view = model.getViews().get(0);

		if (map == null) {
			return;
		}

		LineNetwork network = view.getLineNetwork();
		map.setData(model.getData(), network, mapViewStatus);
		PlanRenderer planRenderer = map.getPlanRenderer();
		planRenderer.setRenderLabels(isShowLabels());
		planRenderer.setRenderStationCenters(isShowStationCenters());
		planRenderer.setSegmentMode(segmentMode.getValue());
		planRenderer.setStationMode(stationMode.getValue());

		viewConfig = view.getConfig();
	}

	public void setSource(Path source)
	{
		this.source = source;
	}

	public Path getSource()
	{
		return source;
	}

	public void setModel1(MapModel model1)
	{
		init(model1, model2);
		updateModel();
		map.setViewConfig(viewConfig, Constants.DEFAULT_ZOOM);
		syncMapState();
	}

	public void setModel2(MapModel model2)
	{
		init(model1, model2);
		updateModel();
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

	private void init(MapModel model1, MapModel model2)
	{
		this.model1 = model1;
		this.model2 = model2;

		mapViewStatus = new MapViewStatus();

		ModelUtil.ensureView(model1, CoordinateConversionType.WGS84);
		ModelUtil.ensureView(model2, CoordinateConversionType.WGS84);
	}

	public MapModel getModel1()
	{
		return model1;
	}

	public MapModel getModel2()
	{
		return model2;
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
		frame = new JFrame("Map Morpher");
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

		new InitialViewportSetupListener(map,
				model1.getViews().get(0).getConfig().getStartPosition());
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
		JMenus.addItem(menuFile, new Open1Action(this), KeyEvent.CTRL_DOWN_MASK,
				KeyEvent.VK_O);
		JMenus.addItem(menuFile, new Open2Action(this), KeyEvent.CTRL_DOWN_MASK,
				KeyEvent.VK_P);
		JMenus.addItem(menuFile, new ExitAction(this), KeyEvent.CTRL_DOWN_MASK,
				KeyEvent.VK_Q);
	}

	private void setupMenuView(JMenu menuView)
	{
		JMenus.addCheckbox(menuView, new ShowLabelsAction(this),
				KeyEvent.VK_F2);
		JMenus.addCheckbox(menuView, new ShowStationCentersAction(this),
				KeyEvent.VK_F3);
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
				10, 15, scale);

		ScrollableView<ScrollableAdvancedPanel> scrollableView = new ScrollableView<>(
				map);

		PanMouseAdapter<ScrollableAdvancedPanel> panAdapter = new PanMouseAdapter<>(
				map);
		map.addMouseListener(panAdapter);
		map.addMouseMotionListener(panAdapter);

		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		c.gridPos(0, 0);
		c.fill(GridBagConstraints.HORIZONTAL).weight(1, 0);
		panel.add(slider, c.getConstraints());

		c.gridPos(0, 1);
		c.fill(GridBagConstraints.BOTH).weight(1, 1);
		panel.add(scrollableView, c.getConstraints());
	}

	public void showReallyExitDialog()
	{
		String ok = UIManager.getString("OptionPane.okButtonText");
		String cancel = UIManager.getString("OptionPane.cancelButtonText");

		Object[] options = { ok, cancel };

		int status = JOptionPane.showOptionDialog(frame, "Exit Map Morpher?",
				"Confirm Exit", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, cancel);
		if (status == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

}
