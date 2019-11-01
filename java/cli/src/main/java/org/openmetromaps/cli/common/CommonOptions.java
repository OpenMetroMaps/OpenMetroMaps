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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.melon.enums.EnumLookup;
import de.topobyte.melon.enums.EnumLookups;
import de.topobyte.melon.enums.EnumUtil;
import de.topobyte.melon.enums.naming.SimpleEnumNamer;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.parsing.ArgumentParseException;

public class CommonOptions
{

	final static Logger logger = LoggerFactory.getLogger(CommonOptions.class);

	private static final String OPTION_STATION_MODE = "station-mode";
	private static final String OPTION_SEGMENT_MODE = "segment-mode";

	private static String POSSIBLE_VALUES_STATION_MODE = EnumUtil
			.buildNameList(StationMode.values(), new SimpleEnumNamer<>());

	private static String POSSIBLE_VALUES_SEGMENT_MODE = EnumUtil
			.buildNameList(SegmentMode.values(), new SimpleEnumNamer<>());

	private static EnumLookup<StationMode> lookupStationMode = EnumLookups
			.build(StationMode.class, new SimpleEnumNamer<>());

	private static EnumLookup<SegmentMode> lookupSegmentMode = EnumLookups
			.build(SegmentMode.class, new SimpleEnumNamer<>());

	public static void addRenderingOptions(Options options)
	{
		// @formatter:off
		OptionHelper.addL(options, OPTION_STATION_MODE, true, false, POSSIBLE_VALUES_STATION_MODE);
		OptionHelper.addL(options, OPTION_SEGMENT_MODE, true, false, POSSIBLE_VALUES_SEGMENT_MODE);
		// @formatter:on
	}

	public static RenderingConfig parseRenderingOptions(CommandLine line)
			throws ArgumentParseException
	{
		RenderingConfig config = new RenderingConfig();

		if (line.hasOption(OPTION_STATION_MODE)) {
			String value = line.getOptionValue(OPTION_STATION_MODE);
			StationMode stationMode = lookupStationMode.get(value);
			if (stationMode == null) {
				throw new ArgumentParseException(String.format(
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
				throw new ArgumentParseException(String.format(
						"Invalid value for option '%s'. Possible values: '%s'",
						OPTION_SEGMENT_MODE, POSSIBLE_VALUES_SEGMENT_MODE));
			} else {
				config.setSegmentMode(segmentMode);
			}
		}

		return config;
	}

}
