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

package org.openmetromaps.swing;

import java.awt.Desktop;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.swing.JDialog;

import de.topobyte.system.utils.SystemProperties;

public class Util
{

	public static void browse(String url)
	{
		String osName = System.getProperty(SystemProperties.OS_NAME);
		if (osName.contains("inux")) {
			ProcessBuilder pb = new ProcessBuilder(
					Arrays.asList("xdg-open", url));
			try {
				pb.start();
			} catch (IOException e) {
				// ignore
			}
		} else {
			try {
				URI uri = new URI(url);
				Desktop.getDesktop().browse(uri);
			} catch (IOException | URISyntaxException e1) {
				// ignore
			}
		}
	}

	public static void showRelativeToOwner(JDialog dialog, int width,
			int height)
	{
		dialog.setSize(width, height);
		dialog.setLocationRelativeTo(dialog.getOwner());
		dialog.setVisible(true);
	}

	public static boolean isControlPressed(MouseEvent e)
	{
		int modifiers = e.getModifiersEx();
		if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
			return true;
		}
		return false;
	}

	public static boolean isShiftPressed(MouseEvent e)
	{
		int modifiers = e.getModifiersEx();
		if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
			return true;
		}
		return false;
	}

	public static boolean isAltPressed(MouseEvent e)
	{
		int modifiers = e.getModifiersEx();
		if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
			return true;
		}
		return false;
	}

	public static boolean isMetaPressed(MouseEvent e)
	{
		int modifiers = e.getModifiersEx();
		if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
			return true;
		}
		return false;
	}

}
