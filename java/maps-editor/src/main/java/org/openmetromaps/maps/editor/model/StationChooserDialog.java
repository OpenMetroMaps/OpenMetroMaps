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
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openmetromaps.maps.model.Station;

import de.topobyte.swing.util.ButtonPane;

public class StationChooserDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	private List<Station> allStations;
	private List<Station> filtered;

	private JTextField filterField;
	private JList<Station> list;
	private FilteredStationListModel listModel;

	private Station selectedStation = null;

	public StationChooserDialog(Window owner, List<Station> stations)
	{
		super(owner);
		this.allStations = stations;
		this.filtered = new ArrayList<>(stations);

		setTitle("Add Station");
		setModal(true);

		JPanel panel = new JPanel(new BorderLayout(4, 4));
		add(panel);

		JPanel filterPanel = new JPanel(new BorderLayout(4, 0));
		filterPanel.add(new JLabel("Filter:"), BorderLayout.WEST);
		filterField = new JTextField();
		filterPanel.add(filterField, BorderLayout.CENTER);
		panel.add(filterPanel, BorderLayout.NORTH);

		listModel = new FilteredStationListModel();
		list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new StationNameCellRenderer());

		JScrollPane jsp = new JScrollPane(list);
		panel.add(jsp, BorderLayout.CENTER);

		String ok = UIManager.getString("OptionPane.okButtonText");
		String cancel = UIManager.getString("OptionPane.cancelButtonText");

		JButton buttonOkay = new JButton(ok);
		JButton buttonCancel = new JButton(cancel);

		List<JButton> buttons = new ArrayList<>();
		buttons.add(buttonOkay);
		buttons.add(buttonCancel);

		ButtonPane buttonPane = new ButtonPane(buttons);
		panel.add(buttonPane, BorderLayout.SOUTH);

		filterField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				applyFilter();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				applyFilter();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				applyFilter();
			}

		});

		buttonOkay.addActionListener(e -> {
			Station station = list.getSelectedValue();
			if (station != null) {
				selectedStation = station;
				dispose();
			}
		});

		buttonCancel.addActionListener(e -> {
			selectedStation = null;
			dispose();
		});

		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2) {
					Station station = list.getSelectedValue();
					if (station != null) {
						selectedStation = station;
						dispose();
					}
				}
			}

		});

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e)
			{
				selectedStation = null;
				dispose();
			}

		});
	}

	private void applyFilter()
	{
		String text = filterField.getText().toLowerCase();
		filtered.clear();
		for (Station station : allStations) {
			if (text.isEmpty()
					|| station.getName().toLowerCase().contains(text)) {
				filtered.add(station);
			}
		}
		listModel.fireChanged();
		if (!filtered.isEmpty()) {
			list.setSelectedIndex(0);
		}
	}

	public Station getSelectedStation()
	{
		return selectedStation;
	}

	private class FilteredStationListModel extends AbstractListModel<Station>
	{

		private static final long serialVersionUID = 1L;

		@Override
		public int getSize()
		{
			return filtered.size();
		}

		@Override
		public Station getElementAt(int index)
		{
			return filtered.get(index);
		}

		public void fireChanged()
		{
			fireContentsChanged(this, 0, Math.max(0, filtered.size() - 1));
		}

	}

}
