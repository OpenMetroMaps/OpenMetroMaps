// Copyright 2019 Sebastian Kuerten
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

package org.openmetromaps.cli.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class CommonOptions
{

	final static Logger logger = LoggerFactory.getLogger(CommonOptions.class);

	private static final String OPTION_STATION_MODE = "station-mode";
	private static final String OPTION_SEGMENT_MODE = "segment-mode";

	private static String POSSIBLE_VALUES_STATION_MODE;
	static {
		List<String> options = new ArrayList<>();
		for (StationMode mode : StationMode.values()) {
			options.add(mode.toString().toLowerCase());
		}
		POSSIBLE_VALUES_STATION_MODE = Joiner.on(", ").join(options);
	}

	private static String POSSIBLE_VALUES_SEGMENT_MODE;
	static {
		List<String> options = new ArrayList<>();
		for (SegmentMode mode : SegmentMode.values()) {
			options.add(mode.toString().toLowerCase());
		}
		POSSIBLE_VALUES_SEGMENT_MODE = Joiner.on(", ").join(options);
	}

	private static EnumLookup<StationMode> lookupStationMode = EnumLookup
			.build(StationMode.values());

	private static EnumLookup<SegmentMode> lookupSegmentMode = EnumLookup
			.build(SegmentMode.values());

	public static void addRenderingOptions(Options options)
	{
		// @formatter:off
		OptionHelper.addL(options, OPTION_STATION_MODE, true, false, POSSIBLE_VALUES_STATION_MODE);
		OptionHelper.addL(options, OPTION_SEGMENT_MODE, true, false, POSSIBLE_VALUES_SEGMENT_MODE);
		// @formatter:on
	}

	public static RenderingConfig parseRenderingOptions(CommandLine line)
	{
		RenderingConfig config = new RenderingConfig();

		if (line.hasOption(OPTION_STATION_MODE)) {
			String value = line.getOptionValue(OPTION_STATION_MODE);
			StationMode stationMode = lookupStationMode.get(value);
			if (stationMode == null) {
				logger.error(String.format(
						"Invalid value for option '%s'. Possible values: '%s'",
						OPTION_STATION_MODE, POSSIBLE_VALUES_STATION_MODE));
			} else {
				config.setStationMode(stationMode);
			}
		}
		if (line.hasOption(OPTION_SEGMENT_MODE)) {
			String value = line.getOptionValue(OPTION_SEGMENT_MODE);
			SegmentMode segmentMode = lookupSegmentMode.get(value);
			if (segmentMode == null) {
				logger.error(String.format(
						"Invalid value for option '%s'. Possible values: '%s'",
						OPTION_SEGMENT_MODE, POSSIBLE_VALUES_SEGMENT_MODE));
			} else {
				config.setSegmentMode(segmentMode);
			}
		}

		return config;
	}

}
