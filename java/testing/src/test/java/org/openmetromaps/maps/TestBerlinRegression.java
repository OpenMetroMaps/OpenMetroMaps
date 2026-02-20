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

package org.openmetromaps.maps;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmetromaps.heavyutil.regression.AffineTransformEstimator;
import org.openmetromaps.heavyutil.regression.AffineTransformEstimator.ControlPoint;
import org.openmetromaps.heavyutil.regression.LinaUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import de.topobyte.geomath.WGS84;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lina.Matrix;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class TestBerlinRegression
{

	@Test
	public void testBerlinRegression() throws ParsingException
	{
		XmlModel xmlModel = TestData.berlinGeographicXml();
		XmlModelConverter converter = new XmlModelConverter();
		MapModel model = converter.convert(xmlModel);

		Assert.assertFalse(model.getViews().isEmpty());
		MapView view = model.getViews().get(0);

		List<Node> allNodes = new ArrayList<>(view.getLineNetwork().getNodes());
		Assert.assertTrue(allNodes.size() > 20);

		// Pick 10 stations for testing
		List<Node> testNodes = new ArrayList<>();
		List<Node> trainingNodes = new ArrayList<>();

		for (int i = 0; i < allNodes.size(); i++) {
			if (i < 10) {
				testNodes.add(allNodes.get(i));
			} else {
				trainingNodes.add(allNodes.get(i));
			}
		}

		List<ControlPoint> controlPoints = new ArrayList<>();
		for (Node node : trainingNodes) {
			if (node.station != null && node.station.getLocation() != null) {
				Point source = new Point(
						WGS84.lon2merc(
								node.station.getLocation().getLongitude(), 1.0),
						WGS84.lat2merc(node.station.getLocation().getLatitude(),
								1.0));
				Point target = new Point(node.location.x, node.location.y);
				controlPoints.add(new ControlPoint(source, target));
			}
		}

		Matrix transform = AffineTransformEstimator.estimate(controlPoints);

		for (Node node : testNodes) {
			Point source = new Point(
					WGS84.lon2merc(node.station.getLocation().getLongitude(),
							1.0),
					WGS84.lat2merc(node.station.getLocation().getLatitude(),
							1.0));
			Point predicted = LinaUtil.transform(transform, source);
			Point actual = new Point(node.location.x, node.location.y);

			double dist = Math
					.sqrt(Math.pow(predicted.getX() - actual.getX(), 2)
							+ Math.pow(predicted.getY() - actual.getY(), 2));

			System.out.println(String.format("Prediction distance for '%s': %f",
					node.station.getName(), dist));
			System.out.println(String.format("Actual: %f, %f", actual.getX(),
					actual.getY()));
			System.out.println(String.format("Predicted: %f, %f",
					predicted.getX(), predicted.getY()));

			// Since this is geographic data converted to mercator,
			// the affine transform should be almost perfect (Identity +
			// scaling/offset).
			// We expect very low error.
			Assert.assertEquals(
					"Prediction for " + node.station.getName()
							+ " is too far off",
					actual.getX(), predicted.getX(), 1e-1);
			Assert.assertEquals(
					"Prediction for " + node.station.getName()
							+ " is too far off",
					actual.getY(), predicted.getY(), 1e-1);
		}
	}

}
