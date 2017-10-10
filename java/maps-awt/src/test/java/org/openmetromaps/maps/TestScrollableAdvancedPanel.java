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

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

public class TestScrollableAdvancedPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws Exception
	{
		XmlModel xmlModel = TestData.berlinXml();

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		if (model.getViews().isEmpty()) {
			LineNetworkBuilder builder = new LineNetworkBuilder(
					model.getData());
			LineNetwork lineNetwork = builder.getGraph();
			ViewConfig viewConfig = ModelUtil.viewConfig(model.getData());
			model.getViews().add(new MapView("Test", lineNetwork, viewConfig));
		}

		LineNetwork lineNetwork = model.getViews().get(0).getLineNetwork();
		MapViewStatus mapViewStatus = new MapViewStatus();

		ViewConfig viewConfig = ModelUtil.viewConfig(model.getData());

		ScrollableAdvancedPanel panel = new ScrollableAdvancedPanel(
				model.getData(), lineNetwork, mapViewStatus,
				PlanRenderer.StationMode.CONVEX, PlanRenderer.SegmentMode.CURVE,
				viewConfig.getStartPosition(), 10, 15, viewConfig.getBbox());

		final JFrame frame = new JFrame("AdvancedPanel");

		frame.add(panel);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		frame.setVisible(true);
	}

}
