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

import java.util.List;

import org.openmetromaps.maps.Interval;
import org.openmetromaps.maps.editor.MapEditor;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Station;

import de.topobyte.lightgeom.lina.Point;

public class StationPropertiesCommand implements HistoryCommand
{

	public static class IntervalChange
	{
		public final Interval interval;
		public final String beforeFrom;
		public final String beforeTo;
		public final String afterFrom;
		public final String afterTo;

		public IntervalChange(Interval interval, String beforeFrom,
				String beforeTo, String afterFrom, String afterTo)
		{
			this.interval = interval;
			this.beforeFrom = beforeFrom;
			this.beforeTo = beforeTo;
			this.afterFrom = afterFrom;
			this.afterTo = afterTo;
		}
	}

	private final String name;
	private final Station station;
	private final String beforeName;
	private final String afterName;
	private final Point beforeLocation;
	private final Point afterLocation;
	private final List<IntervalChange> intervalChanges;

	public static StationPropertiesCommand create(String name, Station station,
			String beforeName, Point beforeLocation, String afterName,
			Point afterLocation, List<IntervalChange> intervalChanges)
	{
		if (station == null || beforeName == null || afterName == null
				|| beforeLocation == null || afterLocation == null) {
			return null;
		}
		if (beforeName.equals(afterName)
				&& Double.compare(beforeLocation.getX(),
						afterLocation.getX()) == 0
				&& Double.compare(beforeLocation.getY(),
						afterLocation.getY()) == 0
				&& intervalChanges.isEmpty()) {
			return null;
		}
		return new StationPropertiesCommand(name, station, beforeName,
				beforeLocation, afterName, afterLocation, intervalChanges);
	}

	private StationPropertiesCommand(String name, Station station,
			String beforeName, Point beforeLocation, String afterName,
			Point afterLocation, List<IntervalChange> intervalChanges)
	{
		this.name = name;
		this.station = station;
		this.beforeName = beforeName;
		this.beforeLocation = beforeLocation;
		this.afterName = afterName;
		this.afterLocation = afterLocation;
		this.intervalChanges = intervalChanges;
	}

	@Override
	public void undo(MapEditor mapEditor)
	{
		for (IntervalChange ic : intervalChanges) {
			if (ic.afterFrom != null) {
				ic.interval.setFrom(ic.beforeFrom);
			}
			if (ic.afterTo != null) {
				ic.interval.setTo(ic.beforeTo);
			}
		}
		apply(mapEditor, beforeName, beforeLocation);
	}

	@Override
	public void redo(MapEditor mapEditor)
	{
		for (IntervalChange ic : intervalChanges) {
			if (ic.afterFrom != null) {
				ic.interval.setFrom(ic.afterFrom);
			}
			if (ic.afterTo != null) {
				ic.interval.setTo(ic.afterTo);
			}
		}
		apply(mapEditor, afterName, afterLocation);
	}

	@Override
	public String getName()
	{
		return name;
	}

	private void apply(MapEditor mapEditor, String nameValue, Point location)
	{
		station.setName(nameValue);
		Node node = mapEditor.getView().getLineNetwork().getStationToNode()
				.get(station);
		if (node != null) {
			node.location = new Point(location.getX(), location.getY());
			LineNetworkUtil.updateEdges(node);
		}

		mapEditor.triggerDataChanged();
		mapEditor.getMap().repaint();
		mapEditor.updateStationPanel();
	}

}
