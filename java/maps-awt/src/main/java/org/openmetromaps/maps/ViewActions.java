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

package org.openmetromaps.maps;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

public class ViewActions
{

	public static void setupMovementActions(InputMap inputMap,
			ActionMap actionMap, BaseMapWindowPanel map)
	{
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
				InputEvent.SHIFT_DOWN_MASK), "up much");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
				InputEvent.SHIFT_DOWN_MASK), "down much");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
				InputEvent.SHIFT_DOWN_MASK), "left much");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
				InputEvent.SHIFT_DOWN_MASK), "right much");

		int little = 8;
		int much = 256;
		MoveAction moveLittleUp = new MoveAction(map, "up",
				"move the viewers viewport up", null, 0, -little);
		MoveAction moveLittleDown = new MoveAction(map, "down",
				"move the viewers viewport down", null, 0, little);
		MoveAction moveLittleLeft = new MoveAction(map, "left",
				"move the viewers viewport left", null, -little, 0);
		MoveAction moveLittleRight = new MoveAction(map, "right",
				"move the viewers viewport right", null, little, 0);
		MoveAction moveMuchUp = new MoveAction(map, "up",
				"move the viewers viewport up", null, 0, -much);
		MoveAction moveMuchDown = new MoveAction(map, "down",
				"move the viewers viewport down", null, 0, much);
		MoveAction moveMuchLeft = new MoveAction(map, "left",
				"move the viewers viewport left", null, -much, 0);
		MoveAction moveMuchRight = new MoveAction(map, "right",
				"move the viewers viewport right", null, much, 0);

		actionMap.put("up", moveLittleUp);
		actionMap.put("down", moveLittleDown);
		actionMap.put("left", moveLittleLeft);
		actionMap.put("right", moveLittleRight);

		actionMap.put("up much", moveMuchUp);
		actionMap.put("down much", moveMuchDown);
		actionMap.put("left much", moveMuchLeft);
		actionMap.put("right much", moveMuchRight);
	}

}
