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

import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.morpher.MapMorpher;
import org.openmetromaps.mobidig.Util;

public class StuttgartMapMorpher
{

	public static void main(String[] args) throws Exception
	{
		MapModel geographic = Util.stuttgartGeographic();
		MapModel schematic = Util.stuttgartSchematic();

		MapMorpher mapMorpher = new MapMorpher(geographic, schematic, null,
				DemoOptions.SCALE);
		mapMorpher.show();

		JFrame frame = (JFrame) mapMorpher.getFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		for (WindowListener windowListener : frame.getWindowListeners()) {
			frame.removeWindowListener(windowListener);
		}
	}

}
