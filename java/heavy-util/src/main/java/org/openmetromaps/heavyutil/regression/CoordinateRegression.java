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

package org.openmetromaps.heavyutil.regression;

import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.heavyutil.regression.AffineTransformEstimator.ControlPoint;
import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Coordinate;

import de.topobyte.geomath.WGS84;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lina.Matrix;
import de.topobyte.viewports.geometry.Rectangle;

public class CoordinateRegression
{

	public static RegressionCoordinateConverter createRegressionConverter(
			MapModel model)
	{
		List<ControlPoint> controlPoints = new ArrayList<>();
		for (MapView view : model.getViews()) {
			for (Node node : view.getLineNetwork().getNodes()) {
				if (node.station != null
						&& node.station.getLocation() != null) {
					Coordinate location = node.station.getLocation();
					Point source = new Point(
							WGS84.lon2merc(location.getLongitude(), 1.0),
							WGS84.lat2merc(location.getLatitude(), 1.0));
					Point target = new Point(node.location.x, node.location.y);
					controlPoints.add(new ControlPoint(source, target));
				}
			}
		}

		if (controlPoints.size() < 3) {
			throw new IllegalStateException(
					"Need at least 3 stations with WGS84 coordinates to create regression converter");
		}

		Matrix transform = AffineTransformEstimator.estimate(controlPoints);

		// We assume the model has at least one view and we use its dimensions
		// if available, otherwise we use some defaults.
		double width = 1000;
		double height = 1000;
		if (!model.getViews().isEmpty()) {
			MapView view = model.getViews().get(0);
			Rectangle scene = view.getConfig().getScene();
			width = Math.abs(scene.getX2() - scene.getX1());
			height = Math.abs(scene.getY2() - scene.getY1());
		}

		return new RegressionCoordinateConverter(transform, width, height);
	}

}
