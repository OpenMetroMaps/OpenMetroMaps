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

package org.openmetromaps.maps.morpher.actions.file;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.openmetromaps.maps.morpher.MapMorpher;
import org.openmetromaps.maps.morpher.actions.MapMorpherAction;
import org.openmetromaps.swing.config.Configuration;
import org.openmetromaps.swing.config.ConfigurationStorage;
import org.openmetromaps.swing.config.edit.ConfigurationEditor;

import de.topobyte.bvg.icons.BvgIcon;
import de.topobyte.bvg.icons.IconResources;

public class SettingsAction extends MapMorpherAction
{

	private static final long serialVersionUID = 1L;

	private JDialog dialog;
	private ConfigurationEditor configurationEditor;

	public SettingsAction(MapMorpher mapMorpher)
	{
		super(mapMorpher, "Settings", "Edit the application settings");
		setIcon(new BvgIcon(IconResources.SETTINGS, 24));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Configuration configuration = new Configuration(
				mapMorpher.getConfiguration());
		showDialog(configuration);
	}

	private void showDialog(Configuration configuration)
	{
		configurationEditor = new ConfigurationEditor(configuration) {

			private static final long serialVersionUID = 1L;

			@Override
			public void ok()
			{
				SettingsAction.this.ok();
			}

			@Override
			public void cancel()
			{
				SettingsAction.this.cancel();
			}

		};

		JFrame frame = mapMorpher.getFrame();
		dialog = new JDialog(frame, "OpenMetroMaps Settings");
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.setContentPane(configurationEditor);
		dialog.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e)
			{
				closing();
			}
		});

		dialog.setModal(true);
		dialog.setSize(520, 520);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	private void ok()
	{
		Configuration configuration = configurationEditor.getConfiguration();
		try {
			ConfigurationStorage.store(configuration);
			mapMorpher.applyConfiguration(configuration);
			dialog.dispose();
		} catch (IOException e) {
			showError("Unable to store configuration: " + e.getMessage());
		}
	}

	private void showError(String message)
	{
		JOptionPane.showMessageDialog(dialog, message, "error",
				JOptionPane.ERROR_MESSAGE);
	}

	private void cancel()
	{
		dialog.dispose();
	}

	private void closing()
	{
		dialog.dispose();
	}

}
