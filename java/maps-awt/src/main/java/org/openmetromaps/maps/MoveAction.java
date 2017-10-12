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

import java.awt.event.ActionEvent;

import de.topobyte.swing.util.action.SimpleAction;

/**
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public class MoveAction extends SimpleAction
{

	private static final long serialVersionUID = 1L;

	private BaseMapWindowPanel map;

	/**
	 * Create an action for moving the viewport of the map.
	 * 
	 * @param map
	 *            the map to work with.
	 * @param name
	 *            the name of the action.
	 * @param description
	 *            the description of the action.
	 * @param filename
	 *            the file of the icon.
	 * @param dx
	 *            the amount to move in the direction of the x-axis.
	 * @param dy
	 *            the amount to move in the direction of the y-axis.
	 */
	public MoveAction(BaseMapWindowPanel map, String name, String description,
			String filename, int dx, int dy)
	{
		super(name, description);
		if (filename != null) {
			setIcon(filename);
		}
		this.map = map;
		this.dx = dx;
		this.dy = dy;
	}

	private int dx, dy = 0;

	@Override
	public void actionPerformed(ActionEvent e)
	{
		map.setPositionX(map.getPositionX() - dx);
		map.setPositionY(map.getPositionY() - dy);
		map.checkBounds();
		map.repaint();
	}

}
