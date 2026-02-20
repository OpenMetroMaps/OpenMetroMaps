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
import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import de.topobyte.swing.util.ButtonPane;

public class ImportAdditionalStationsFromOsmDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	private JTextArea textArea;
	private boolean ok;

	public ImportAdditionalStationsFromOsmDialog(Window owner)
	{
		super(owner, "Import additional stations by OSM node IDs");

		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(panel);

		panel.add(new JLabel("Enter OSM node IDs (one per line):"),
				BorderLayout.NORTH);

		textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(400, 300));
		panel.add(scrollPane, BorderLayout.CENTER);

		String cancel = UIManager.getString("OptionPane.cancelButtonText");

		JButton buttonImport = new JButton("Import");
		JButton buttonCancel = new JButton(cancel);

		List<JButton> buttons = new ArrayList<>();
		buttons.add(buttonImport);
		buttons.add(buttonCancel);

		ButtonPane buttonPane = new ButtonPane(buttons);
		panel.add(buttonPane, BorderLayout.SOUTH);
		// ---

		buttonImport.addActionListener(e -> {
			if (parseNodeIds() == null) {
				return;
			}
			ok = true;
			setVisible(false);
		});

		buttonCancel.addActionListener(e -> setVisible(false));
	}

	public boolean isOk()
	{
		return ok;
	}

	// Returns the parsed IDs, or null if any line was invalid (warning already
	// shown).
	private List<Long> parseNodeIds()
	{
		List<Long> ids = new ArrayList<>();
		String text = textArea.getText();
		String[] lines = text.split("\\s+");
		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			try {
				ids.add(Long.parseLong(line));
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this,
						"Invalid OSM node ID: '" + line
								+ "'. Please enter one numeric ID per line.",
						"Invalid Input", JOptionPane.WARNING_MESSAGE);
				return null;
			}
		}
		return ids;
	}

	public List<Long> getNodeIds()
	{
		return parseNodeIds();
	}

}
