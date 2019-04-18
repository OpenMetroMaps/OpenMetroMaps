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

import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.scrolling.PanMouseAdapter;
import de.topobyte.viewports.scrolling.ScrollableView;

public class TestScrollableSimplePanel
{

	public static void main(String[] args) throws Exception
	{
		XmlModel xmlModel = TestData.berlinXml();

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		ModelUtil.ensureView(model, CoordinateConversionType.WGS84);

		ScrollableSimplePanel panel = new ScrollableSimplePanel(model.getData(),
				model.getViews().get(0), 10, 15);

		Coordinate start = model.getViews().get(0).getConfig()
				.getStartPosition();
		new InitialViewportSetupListener(panel, start);

		ScrollableView<ScrollableSimplePanel> scrollableView = new ScrollableView<>(
				panel);

		PanMouseAdapter<ScrollableSimplePanel> panAdapter = new PanMouseAdapter<>(
				panel);
		panel.addMouseListener(panAdapter);
		panel.addMouseMotionListener(panAdapter);

		JFrame frame = new JFrame("SimplePanel");

		frame.add(scrollableView);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		frame.setVisible(true);
	}

}
