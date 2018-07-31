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

package org.openmetromaps.maps.viewer;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.TestData;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

public class TestMapViewerWithScaleDown
{

	public static void main(String[] args) throws Exception
	{
		double scale = 0.7;

		XmlModel xmlModel = TestData.berlinXml();

		XmlModelConverter modelConverter = new XmlModelConverter();
		MapModel model = modelConverter.convert(xmlModel);

		MapView view = model.getViews().get(0);
		MapView scaled = ModelUtil.getScaledInstance(view, scale);
		model.getViews().set(0, scaled);

		MapViewer mapViewer = new MapViewer(model, null);

		mapViewer.show();
		mapViewer.getMap().getPlanRenderer().setScale((float) scale);
		mapViewer.getMap().getPlanRenderer().zoomChanged();
	}

}
