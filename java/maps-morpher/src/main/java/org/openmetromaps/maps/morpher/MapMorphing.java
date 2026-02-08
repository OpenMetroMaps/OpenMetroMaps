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

package org.openmetromaps.maps.morpher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapMorphing
{

	final static Logger logger = LoggerFactory.getLogger(MapMorphing.class);

	public static MapModel deriveModel(MapModel model1, MapModel model2,
			double relative)
	{
		MapMorhCalculator calculator = new MapMorhCalculator(model1, model2);
		MapModel model = calculator.deriveModel(relative);
		return model;
	}

	/**
	 * Find out if two map models are a valid pair for performing a morph. This
	 * function checks if all the stations of {@code model1} have a matching
	 * equivalent in {@code model2}.
	 * 
	 * @return true if {@link #deriveModel} can safely be called for the models.
	 */
	public static boolean doModelsMatch(MapModel model1, MapModel model2)
	{
		MapView view1 = model1.getViews().get(0);
		MapView view2 = model2.getViews().get(0);
		LineNetwork network1 = view1.getLineNetwork();
		LineNetwork network2 = view2.getLineNetwork();

		List<Station> stations1 = model1.getData().stations;
		List<Station> stations2 = model2.getData().stations;

		if (stations1.size() != stations2.size()) {
			logger.warn("Number of stations does not match: {} vs {}",
					stations1.size(), stations2.size());
		}

		Map<String, Station> nameToStation2 = new HashMap<>();
		for (Station station : network2.getStationToNode().keySet()) {
			nameToStation2.put(station.getName(), station);
		}

		List<String> missingStations = new ArrayList<>();

		for (Node node : network1.getNodes()) {
			String stationName = node.station.getName();
			Station station2 = nameToStation2.get(stationName);
			if (station2 == null) {
				logger.warn("Station from model1 missing in model2: '{}'",
						stationName);
				missingStations.add(stationName);
			}
		}

		return missingStations.isEmpty();
	}

}
