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

package org.openmetromaps.maps.editor.model;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import de.topobyte.bvg.icons.BvgIcon;
import de.topobyte.bvg.icons.IconResources;

public class StationsPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	private StationsListModel listModel;
	private JList<Stop> list;
	private ModelData data;

	public StationsPanel(ModelData data, Line line)
	{
		super(new BorderLayout());
		this.data = data;

		listModel = new StationsListModel(line.getStops());
		list = new JList<>(listModel);
		list.setCellRenderer(new StationsCellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane jsp = new JScrollPane(list);
		add(jsp, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 2, 2));

		JButton buttonMoveUp = new JButton("Move Up",
				new BvgIcon(IconResources.UP, 24));
		JButton buttonMoveDown = new JButton("Move Down",
				new BvgIcon(IconResources.DOWN, 24));
		JButton buttonAddStart = new JButton("Add to Start",
				new BvgIcon(IconResources.ADD, 24));
		JButton buttonAddEnd = new JButton("Add to End",
				new BvgIcon(IconResources.ADD, 24));
		JButton buttonRemove = new JButton("Remove",
				new BvgIcon(IconResources.DELETE, 24));

		buttonPanel.add(buttonMoveUp);
		buttonPanel.add(buttonMoveDown);
		buttonPanel.add(buttonAddStart);
		buttonPanel.add(buttonAddEnd);
		buttonPanel.add(buttonRemove);

		add(buttonPanel, BorderLayout.EAST);

		buttonMoveUp.addActionListener(e -> {
			int index = list.getSelectedIndex();
			if (index > 0) {
				listModel.moveUp(index);
				list.setSelectedIndex(index - 1);
			}
		});

		buttonMoveDown.addActionListener(e -> {
			int index = list.getSelectedIndex();
			if (index >= 0 && index < listModel.getSize() - 1) {
				listModel.moveDown(index);
				list.setSelectedIndex(index + 1);
			}
		});

		buttonAddStart.addActionListener(e -> {
			Station station = chooseStation();
			if (station != null) {
				Stop stop = new Stop(station, line);
				listModel.addFirst(stop);
				list.setSelectedIndex(0);
			}
		});

		buttonAddEnd.addActionListener(e -> {
			Station station = chooseStation();
			if (station != null) {
				Stop stop = new Stop(station, line);
				listModel.addLast(stop);
				list.setSelectedIndex(listModel.getSize() - 1);
			}
		});

		buttonRemove.addActionListener(e -> {
			int index = list.getSelectedIndex();
			if (index >= 0) {
				listModel.remove(index);
				if (listModel.getSize() > 0) {
					list.setSelectedIndex(
							Math.min(index, listModel.getSize() - 1));
				}
			}
		});
	}

	private Station chooseStation()
	{
		Set<String> present = new HashSet<>();
		for (Stop stop : listModel.getStops()) {
			present.add(stop.getStation().getName());
		}

		List<Station> available = new ArrayList<>();
		for (Station station : data.stations) {
			if (!present.contains(station.getName())) {
				available.add(station);
			}
		}

		if (available.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"All stations are already on this line.",
					"No Stations Available", JOptionPane.INFORMATION_MESSAGE);
			return null;
		}

		available.sort((a, b) -> a.getName().compareTo(b.getName()));

		Window window = SwingUtilities.getWindowAncestor(this);
		StationChooserDialog chooser = new StationChooserDialog(window,
				available);
		chooser.setSize(400, 400);
		chooser.setLocationRelativeTo(window);
		chooser.setVisible(true);

		return chooser.getSelectedStation();
	}

	public List<Stop> getStops()
	{
		return listModel.getStops();
	}

}
