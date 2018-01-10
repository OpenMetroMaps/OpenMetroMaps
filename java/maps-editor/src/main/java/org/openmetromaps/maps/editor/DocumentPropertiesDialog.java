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

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.openmetromaps.maps.MapView;

import de.topobyte.swing.util.ButtonPane;

public class DocumentPropertiesDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public static interface DialogListener
	{

		public void done(DocumentPropertiesDialog dialog, boolean positive);

	}

	private DocumentPropertiesPanel documentPropertiesPanel;

	public DocumentPropertiesDialog(MapView view, DialogListener listener)
	{
		init(view, listener);
	}

	public DocumentPropertiesDialog(Window owner, MapView view,
			DialogListener listener)
	{
		super(owner);
		init(view, listener);
	}

	private void init(MapView view, DialogListener listener)
	{
		JPanel panel = new JPanel(new BorderLayout());
		add(panel);

		setTitle("Document Properties");
		documentPropertiesPanel = new DocumentPropertiesPanel(view);
		panel.add(documentPropertiesPanel, BorderLayout.CENTER);

		String ok = UIManager.getString("OptionPane.okButtonText");
		String cancel = UIManager.getString("OptionPane.cancelButtonText");

		JButton buttonOkay = new JButton(ok);
		JButton buttonCancel = new JButton(cancel);

		List<JButton> buttons = new ArrayList<>();
		buttons.add(buttonOkay);
		buttons.add(buttonCancel);

		ButtonPane buttonPane = new ButtonPane(buttons);
		panel.add(buttonPane, BorderLayout.SOUTH);

		buttonOkay.addActionListener(x -> listener.done(this, true));
		buttonCancel.addActionListener(x -> listener.done(this, false));

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e)
			{
				listener.done(DocumentPropertiesDialog.this, false);
			}

		});
	}

	public DocumentPropertiesPanel getDocumentPropertiesPanel()
	{
		return documentPropertiesPanel;
	}

}
