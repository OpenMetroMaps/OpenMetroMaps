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

package org.openmetromaps.swing.actions;

import de.topobyte.bvg.icons.BvgIcon;
import de.topobyte.bvg.icons.IconResources;
import de.topobyte.swing.util.ToggleAction;
import de.topobyte.swing.util.action.enums.BooleanValueHolder;

public class ActionHelper
{

	public static ToggleAction createShowLabelsAction(
			BooleanValueHolder valueHolder)
	{
		return new ToggleAction("Show labels", "Toggle map label visibility",
				valueHolder, //
				new BvgIcon(IconResources.LABELS_OFF, 24),
				new BvgIcon(IconResources.LABELS_ON, 24));
	}

	public static ToggleAction createToggleAntialiasingAction(
			BooleanValueHolder valueHolder)
	{
		return new ToggleAction("Antialiasing",
				"Toggle antialiasing on the map", valueHolder,
				new BvgIcon(IconResources.ANTIALIASING_OFF, 24),
				new BvgIcon(IconResources.ANTIALIASING_ON, 24));
	}

	public static ToggleAction createShowStationCentersAction(
			BooleanValueHolder valueHolder)
	{
		return new ToggleAction("Show Station Centers",
				"Toggle visibility of station centers", valueHolder);
	}

	public static ToggleAction createDebugTangentsAction(
			BooleanValueHolder valueHolder)
	{
		return new ToggleAction("Debug tangents",
				"Toggle edge tangent visibility", valueHolder);
	}

	public static ToggleAction createDebugRanksAction(
			BooleanValueHolder valueHolder)
	{
		return new ToggleAction("Debug ranks", "Toggle station rank visibility",
				valueHolder);
	}

	public static ToggleAction createShowMapAction(
			BooleanValueHolder valueHolder)
	{
		return new ToggleAction("Show Map", "Toggle visibility of map window",
				valueHolder);
	}

}
