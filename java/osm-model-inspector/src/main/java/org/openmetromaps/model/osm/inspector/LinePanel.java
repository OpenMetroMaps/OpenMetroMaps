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

package org.openmetromaps.model.osm.inspector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openmetromaps.model.osm.DraftLine;
import org.openmetromaps.model.osm.DraftStation;
import org.openmetromaps.swing.Util;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.osm4j.core.model.iface.OsmRelation;

public class LinePanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	private JLabel labelName = new JLabel("Name:");
	private JLabel displayName = new JLabel();
	private JLabel labelSource = new JLabel("Source:");
	private JButton displaySource = new JButton();
	private JLabel labelNumStations = new JLabel("# Stations:");
	private JLabel displayNumStations = new JLabel();

	private DraftLine line;

	public LinePanel()
	{
		super(new GridBagLayout());
		init();
	}

	private void init()
	{
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		displaySource.setHorizontalAlignment(SwingConstants.LEFT);
		displaySource.setMargin(new Insets(0, 0, 0, 0));
		displaySource.setVisible(false);

		c.fill(GridBagConstraints.HORIZONTAL).weight(1, 0);
		c.gridX(1);
		add(labelName, c.getConstraints());
		c.gridX(2);
		add(displayName, c.getConstraints());

		c.gridY(1);
		c.gridX(1);
		add(labelSource, c.getConstraints());
		c.gridX(2);
		add(displaySource, c.getConstraints());

		c.gridY(2);
		c.gridX(1);
		add(labelNumStations, c.getConstraints());
		c.gridX(2);
		add(displayNumStations, c.getConstraints());

		c.gridY(3);
		c.fill(GridBagConstraints.BOTH).weight(1, 1);
		add(new JPanel(), c.getConstraints());

		displaySource.addActionListener(x -> showContextMenu(displaySource));
	}

	public void setLine(DraftLine line)
	{
		this.line = line;

		final OsmRelation source = line.getSource();
		String name = line.getName();

		displayName.setText(name);
		displaySource.setText(String.format("Relation %d", source.getId()));
		displayNumStations
				.setText(String.format("%d", line.getStations().size()));

		displaySource.setVisible(true);
	}

	protected void showContextMenu(JButton button)
	{
		JPopupMenu menu = new JPopupMenu();
		OsmRelation source = line.getSource();

		JMenuItem itemOpenInBrowser = new JMenuItem();
		itemOpenInBrowser.setText("Open in browser");
		itemOpenInBrowser.addActionListener(x -> openRelationInBrowser(source));

		JMenuItem itemShowXml = new JMenuItem();
		itemShowXml.setText("Show raw data");
		itemShowXml.addActionListener(x -> showRawData(source));

		JMenuItem itemShowStops = new JMenuItem();
		itemShowStops.setText("Show stations");
		itemShowStops.addActionListener(x -> showStations());

		menu.add(itemOpenInBrowser);
		menu.add(itemShowXml);
		menu.add(itemShowStops);

		menu.show(button, 0, button.getHeight());
	}

	protected void openRelationInBrowser(OsmRelation source)
	{
		Util.browse(String.format("http://www.openstreetmap.org/relation/%d",
				source.getId()));
	}

	private void showRawData(OsmRelation source)
	{
		Window window = SwingUtilities.windowForComponent(this);
		ElementXmlDialog dialog = new ElementXmlDialog(window, source);
		Util.showRelativeToOwner(dialog, 600, 400);
	}

	private void showStations()
	{
		List<String> output = new ArrayList<>();
		for (DraftStation station : line.getStations()) {
			output.add(station.getName());
		}

		String name = line.getName();

		Window window = SwingUtilities.windowForComponent(this);
		TextDialog dialog = new TextDialog(window, "Line " + name, output);
		Util.showRelativeToOwner(dialog, 500, 400);
	}

}
