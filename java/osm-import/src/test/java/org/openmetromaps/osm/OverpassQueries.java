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

package org.openmetromaps.osm;

public class OverpassQueries
{

	public static String Q_VBB_REGIO = "(" //
			+ "  relation" //
			+ "    [route=train]" //
			+ "    [network=\"Verkehrsverbund Berlin-Brandenburg\"]" //
			+ "    (50.9030328,11.008300,53.917281,15.9301757);" //
			+ "  >;" //
			+ ");" //
			+ "out;";

	public static String Q_VBB_RE1_BY_ID = "(" //
			+ " relation(188380); >;" //
			+ ");" //
			+ "out;";

	public static String Q_VBB_RE1_BY_TAGS = "(" //
			+ "relation[ref=RE1][network=\"Verkehrsverbund Berlin-Brandenburg\"];" //
			+ ">;" //
			+ ");" //
			+ "out;";

	public static String Q_BERLIN_SU = "(" //
			+ "(" //
			+ "  relation" //
			+ "    [route=train]" //
			+ "    [line=light_rail]" //
			+ "    [network=\"Verkehrsverbund Berlin-Brandenburg\"]" //
			+ "    (50.9030328,11.008300,53.917281,15.9301757);" //
			+ "  relation" //
			+ "    [route=subway]" //
			+ "    [network=\"Verkehrsverbund Berlin-Brandenburg\"]" //
			+ "    (50.9030328,11.008300,53.917281,15.9301757);" //
			+ ");" //
			+ ">;" //
			+ ");" //
			+ "out;";

}
