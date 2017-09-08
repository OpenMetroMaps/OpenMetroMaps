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

package org.openmetromaps.model.inspector;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openmetromaps.model.DraftLine;
import org.openmetromaps.model.DraftModel;
import org.openmetromaps.model.DraftStation;
import org.openmetromaps.model.inspector.actions.AboutAction;
import org.openmetromaps.model.inspector.actions.AnalyzeStopsAction;
import org.openmetromaps.model.inspector.actions.ExitAction;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public class ModelInspector
{

	private DraftModel model;

	private JFrame frame;

	private LinesListModel linesModel;

	private JList<DraftLine> listLines;
	private LinePanel linePanel;

	public ModelInspector(DraftModel model)
	{
		this.model = model;
	}

	public void show()
	{
		frame = new JFrame("Model Inspector");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);

		build();

		frame.setVisible(true);
	}

	private void build()
	{
		setupMenu();
		setupContent();
		setupListActions();
	}

	private void setupMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);

		JMenu menuAnalyze = new JMenu("Analyze");
		menuBar.add(menuAnalyze);

		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		menuFile.add(new ExitAction());
		menuAnalyze.add(new AnalyzeStopsAction());
		menuHelp.add(new AboutAction());
	}

	private void setupContent()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		frame.setContentPane(panel);

		linesModel = new LinesListModel(model);
		listLines = new JList<>(linesModel);
		JScrollPane jspLines = new JScrollPane(listLines);
		listLines.setCellRenderer(new LinesCellRenderer());

		linePanel = new LinePanel();

		GridBagConstraintsEditor c = new GridBagConstraintsEditor();
		c.weight(1, 1).fill(GridBagConstraints.BOTH);
		panel.add(jspLines, c.getConstraints());

		c.weight(0, 1);
		panel.add(linePanel, c.getConstraints());
		linePanel.setPreferredSize(new Dimension(250, 0));
	}

	private void setupListActions()
	{
		listLines.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				JList<?> list = (JList<?>) e.getSource();
				if (e.getClickCount() == 2) {
					int index = list.locationToIndex(e.getPoint());
					activated(index);
				}
			}

		});

		listLines.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting()) {
					selectionChanged();
				}
			}

		});

		String actionEnter = "Enter";
		listLines.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
				actionEnter);
		listLines.getActionMap().put(actionEnter, new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				activated(listLines.getSelectedIndex());
			}

		});
	}

	protected void selectionChanged()
	{
		DraftLine line = listLines.getSelectedValue();
		if (line == null) {
			return;
		}
		linePanel.setLine(line);
	}

	protected void activated(int index)
	{
		DraftLine line = linesModel.getElementAt(index);
		Map<String, String> tags = OsmModelUtil.getTagsAsMap(line.getSource());
		String name = tags.get("ref");
		System.out.println(String.format(
				"Line: %s, Source: http://www.openstreetmap.org/relation/%d",
				name, line.getSource().getId()));
		for (DraftStation station : line.getStations()) {
			System.out.println(station.getName());
		}
	}

}
