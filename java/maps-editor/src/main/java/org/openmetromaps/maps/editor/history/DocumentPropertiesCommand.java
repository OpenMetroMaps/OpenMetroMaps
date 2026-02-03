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

package org.openmetromaps.maps.editor.history;

import org.openmetromaps.maps.ViewConfig;
import org.openmetromaps.maps.editor.MapEditor;

import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

public class DocumentPropertiesCommand implements HistoryCommand
{

	private final String name;
	private final ViewConfig config;
	private final Rectangle beforeScene;
	private final Coordinate beforeStart;
	private final Rectangle afterScene;
	private final Coordinate afterStart;

	public static DocumentPropertiesCommand create(String name,
			ViewConfig config, Rectangle beforeScene, Coordinate beforeStart,
			Rectangle afterScene, Coordinate afterStart)
	{
		if (config == null || beforeScene == null || beforeStart == null
				|| afterScene == null || afterStart == null) {
			return null;
		}
		if (Double.compare(beforeScene.getX1(), afterScene.getX1()) == 0
				&& Double.compare(beforeScene.getY1(), afterScene.getY1()) == 0
				&& Double.compare(beforeScene.getX2(), afterScene.getX2()) == 0
				&& Double.compare(beforeScene.getY2(), afterScene.getY2()) == 0
				&& Double.compare(beforeStart.getX(), afterStart.getX()) == 0
				&& Double.compare(beforeStart.getY(), afterStart.getY()) == 0) {
			return null;
		}
		return new DocumentPropertiesCommand(name, config, beforeScene,
				beforeStart, afterScene, afterStart);
	}

	private DocumentPropertiesCommand(String name, ViewConfig config,
			Rectangle beforeScene, Coordinate beforeStart, Rectangle afterScene,
			Coordinate afterStart)
	{
		this.name = name;
		this.config = config;
		this.beforeScene = beforeScene;
		this.beforeStart = beforeStart;
		this.afterScene = afterScene;
		this.afterStart = afterStart;
	}

	@Override
	public void undo(MapEditor mapEditor)
	{
		apply(mapEditor, beforeScene, beforeStart);
	}

	@Override
	public void redo(MapEditor mapEditor)
	{
		apply(mapEditor, afterScene, afterStart);
	}

	@Override
	public String getName()
	{
		return name;
	}

	private void apply(MapEditor mapEditor, Rectangle sceneValue,
			Coordinate startValue)
	{
		Rectangle scene = config.getScene();
		Coordinate start = config.getStartPosition();

		scene.setX1(sceneValue.getX1());
		scene.setY1(sceneValue.getY1());
		scene.setX2(sceneValue.getX2());
		scene.setY2(sceneValue.getY2());
		start.setX(startValue.getX());
		start.setY(startValue.getY());

		mapEditor.triggerDataChanged();
		mapEditor.getMap().repaint();
	}

}
