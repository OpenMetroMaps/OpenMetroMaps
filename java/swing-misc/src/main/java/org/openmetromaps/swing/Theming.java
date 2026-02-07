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

package org.openmetromaps.swing;

import java.awt.Color;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class Theming
{

	final static Logger logger = LoggerFactory.getLogger(Theming.class);

	public static void setup()
	{
		setupLookAndFeel();
	}

	public static void setupLookAndFeel()
	{
		LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo info : lafs) {
			logger.debug(String.format("Found Look and Feel: %s (%s)",
					info.getName(), info.getClassName()));
		}

		// # Typical LookAndFeels on Linux
		//
		// Metal: javax.swing.plaf.metal.MetalLookAndFeel
		// Nimbus: javax.swing.plaf.nimbus.NimbusLookAndFeel
		// CDE/Motif: com.sun.java.swing.plaf.motif.MotifLookAndFeel
		// GTK+: com.sun.java.swing.plaf.gtk.GTKLookAndFeel

		Theme theme = Theme.FLATLAF_LIGHT;
		switch (theme) {
		default:
		case DEFAULT:
			String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			trySetLookAndFeelByClassName(lookAndFeel);
			break;
		case METAL:
			trySetLookAndFeelByClassName(
					"javax.swing.plaf.metal.MetalLookAndFeel");
			break;
		case FLATLAF_DARK: {
			boolean success = trySetLookAndFeelByInstance(new FlatDarculaLaf());
			if (success) {
				UIManager.put("MenuItem.checkBackground",
						new ColorUIResource(new Color(0, 0, 0, 0)));
			}
			break;
		}
		case FLATLAF_LIGHT: {
			boolean success = trySetLookAndFeelByInstance(new FlatLightLaf());
			if (success) {
				UIManager.put("MenuItem.checkBackground",
						new ColorUIResource(new Color(0, 0, 0, 0)));
			}
			break;
		}
		}
	}

	private static boolean trySetLookAndFeelByInstance(LookAndFeel lookAndFeel)
	{
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			return true;
		} catch (UnsupportedLookAndFeelException e) {
			logger.error("error while setting look and feel '" + lookAndFeel
					+ "': " + e.getClass().getSimpleName() + ", message: "
					+ e.getMessage());
		}
		return false;
	}

	private static boolean trySetLookAndFeelByClassName(String lookAndFeel)
	{
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			return true;
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			logger.error("error while setting look and feel '" + lookAndFeel
					+ "': " + e.getClass().getSimpleName() + ", message: "
					+ e.getMessage());
		}
		return false;
	}

}
