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

package org.openmetromaps.mobidig.demo;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.melon.resources.Resources;

public class Start
{

	final static Logger logger = LoggerFactory.getLogger(Start.class);

	private JFrame frame;

	private JButton buttonGeographisch;
	private JButton buttonSchematisch;
	private JButton buttonMorph;
	private JButton buttonPR;
	private JButton buttonAufzug;

	public static void main(String[] args)
	{
		new Start().show();
	}

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
		buttonGeographisch = new JButton("S-Bahn-Netz geograpisch");
		buttonSchematisch = new JButton("S-Bahn-Netz schematisch");
		buttonMorph = new JButton("Morph");
		buttonPR = new JButton("Park & Ride");
		buttonAufzug = new JButton("Fahrstuhlinformation");

		JPanel main = new JPanel(new GridBagLayout());
		frame.setContentPane(main);

		GridBagConstraintsEditor ce = new GridBagConstraintsEditor();
		GridBagConstraints c = ce.getConstraints();

		ce.fill(GridBagConstraints.HORIZONTAL);
		ce.weight(0, 0);

		JLabel labelGreeting = new JLabel("Welcome to OpenMetroMaps");

		Font font = labelGreeting.getFont();
		labelGreeting.setFont(font.deriveFont(font.getSize() * 1.5f));

		int y = 0;

		ce.gridPos(0, y++);
		main.add(labelGreeting, c);
		ce.gridPos(0, y++);
		main.add(buttonGeographisch, c);
		ce.gridPos(0, y++);
		main.add(buttonSchematisch, c);
		ce.gridPos(0, y++);
		main.add(buttonMorph, c);
		ce.gridPos(0, y++);
		main.add(buttonAufzug, c);
		ce.gridPos(0, y++);
		main.add(buttonPR, c);

		ce.fill(GridBagConstraints.BOTH);
		ce.weight(1, 1);
		ce.gridPos(0, y++);
		main.add(new JPanel(), c);
	}

	private void setupActions()
	{
		buttonGeographisch.addActionListener(new Listener() {
			@Override
			public void performed() throws Exception
			{
				SimpleGeographic.main(new String[0]);
			}
		});
		buttonSchematisch.addActionListener(new Listener() {
			@Override
			public void performed() throws Exception
			{
				SimpleSchematic.main(new String[0]);
			}
		});
		buttonMorph.addActionListener(new Listener() {
			@Override
			public void performed() throws Exception
			{
				StuttgartMapMorpher.main(new String[0]);
			}
		});
		buttonPR.addActionListener(new Listener() {
			@Override
			public void performed() throws Exception
			{
				PundRViewer.main(new String[0]);
			}
		});
		buttonAufzug.addActionListener(new Listener() {
			@Override
			public void performed() throws Exception
			{
				AufzugViewer.main(new String[0]);
			}
		});
	}

	private abstract static class Listener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent event)
		{
			try {
				performed();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public abstract void performed() throws Exception;

	}

}
