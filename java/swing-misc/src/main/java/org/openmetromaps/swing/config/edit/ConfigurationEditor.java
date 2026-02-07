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

package org.openmetromaps.swing.config.edit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.openmetromaps.swing.config.Configuration;

public abstract class ConfigurationEditor extends JPanel
		implements ActionListener
{

	private static final long serialVersionUID = 1L;

	private final Configuration baseConfiguration;
	private final AppearancePane appearance;
	private final EditorOptionsPane editorOptions;
	private final ViewerOptionsPane viewerOptions;

	public ConfigurationEditor(Configuration configuration)
	{
		super(new GridBagLayout());
		this.baseConfiguration = new Configuration(configuration);
		GridBagConstraints c = new GridBagConstraints();

		JTabbedPane tabbed = new JTabbedPane(SwingConstants.LEFT);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		add(tabbed, c);

		appearance = new AppearancePane(configuration);
		JScrollPane jspAppearance = new JScrollPane();
		jspAppearance.setViewportView(appearance);

		editorOptions = new EditorOptionsPane(configuration);
		JScrollPane jspEditor = new JScrollPane();
		jspEditor.setViewportView(editorOptions);

		viewerOptions = new ViewerOptionsPane(configuration);
		JScrollPane jspViewer = new JScrollPane();
		jspViewer.setViewportView(viewerOptions);

		tabbed.add("Appearance", jspAppearance);
		tabbed.add("Editor", jspEditor);
		tabbed.add("Viewer", jspViewer);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

		JPanel buttonGrid = new JPanel();
		buttonGrid.setLayout(new GridLayout(1, 2));

		JButton buttonCancel = new JButton("Cancel");
		JButton buttonOk = new JButton("Ok");

		buttonGrid.add(buttonCancel);
		buttonGrid.add(buttonOk);

		buttons.add(Box.createHorizontalGlue());
		buttons.add(buttonGrid);

		c.gridy = 1;
		c.weighty = 0.0;
		add(buttons, c);

		buttonCancel.setActionCommand("cancel");
		buttonOk.setActionCommand("ok");
		buttonCancel.addActionListener(this);
		buttonOk.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("ok")) {
			ok();
		} else if (e.getActionCommand().equals("cancel")) {
			cancel();
		}
	}

	public abstract void ok();

	public abstract void cancel();

	public Configuration getConfiguration()
	{
		Configuration configuration = new Configuration(baseConfiguration);
		appearance.setValues(configuration);
		editorOptions.setValues(configuration);
		viewerOptions.setValues(configuration);
		return configuration;
	}

}
