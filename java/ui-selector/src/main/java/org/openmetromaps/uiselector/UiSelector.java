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

package org.openmetromaps.uiselector;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.openmetromaps.maps.Edges;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ViewConfig;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.viewer.MapViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.melon.resources.Resources;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

public class UiSelector
{

	final static Logger logger = LoggerFactory.getLogger(UiSelector.class);

	private JFrame frame;

	private JButton buttonViewer;
	private JButton buttonEditor;

	public void show()
	{
		frame = new JFrame("OpenMetroMaps");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(800, 400);

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
		setupActions();
	}

	private void setupContent()
	{
		buttonViewer = new JButton("Viewer");
		buttonEditor = new JButton("Editor");

		JPanel main = new JPanel(new GridBagLayout());
		frame.setContentPane(main);

		GridBagConstraintsEditor ce = new GridBagConstraintsEditor();
		GridBagConstraints c = ce.getConstraints();

		ce.fill(GridBagConstraints.HORIZONTAL);
		ce.weight(0, 0);

		JLabel labelGreeting = new JLabel("Welcome to OpenMetroMaps");
		JLabel labelPleaseSelect = new JLabel("Select a task:");

		Font font = labelGreeting.getFont();
		labelGreeting.setFont(font.deriveFont(font.getSize() * 1.5f));

		int y = 0;

		ce.gridPos(0, y++);
		main.add(labelGreeting, c);
		ce.gridPos(0, y++);
		main.add(labelPleaseSelect, c);
		ce.gridPos(0, y++);
		main.add(buttonViewer, c);
		ce.gridPos(0, y++);
		main.add(buttonEditor, c);

		ce.fill(GridBagConstraints.BOTH);
		ce.weight(1, 1);
		ce.gridPos(0, y++);
		main.add(new JPanel(), c);
	}

	private void setupActions()
	{
		buttonViewer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Viewer");
				startViewer();
			}

		});
		buttonEditor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Editor");
				startEditor();
			}

		});
	}

	protected void startViewer()
	{
		frame.dispose();

		MapModel model = createEmptyModel();
		MapViewer viewer = new MapViewer(model, null);
		viewer.show();
	}

	protected void startEditor()
	{
		frame.dispose();

		MapModel model = createEmptyModel();
		MapEditor editor = new MapEditor(model, null);
		editor.show();
	}

	private MapModel createEmptyModel()
	{
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

		return model;
	}

}
