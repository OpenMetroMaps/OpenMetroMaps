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

package org.openmetromaps.gtfs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipException;

import org.openmetromaps.gtfs4j.csv.GtfsZip;
import org.openmetromaps.gtfs4j.model.Agency;
import org.openmetromaps.gtfs4j.model.Route;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class GtfsImporter
{

	private Path path;
	private GtfsZip zip;
	private Multimap<String, Route> nameToRoute;
	private List<String> routeNames;

	public GtfsImporter(Path path)
	{
		this.path = path;
	}

	public void execute() throws ZipException, IOException
	{
		zip = new GtfsZip(path);

		printAgencyInfo();

		readRoutes();

		printRouteInfo();

		zip.close();
	}

	private void printAgencyInfo() throws IOException
	{
		List<Agency> agencies = zip.readAgency();
		for (Agency agency : agencies) {
			System.out.println(String.format("agency: %s, %s", agency.getId(),
					agency.getName()));
		}
	}

	private void readRoutes() throws IOException
	{
		nameToRoute = HashMultimap.create();

		List<Route> routes = zip.readRoutes();
		for (Route route : routes) {
			String name = getName(route);
			nameToRoute.put(name, route);
		}

		routeNames = new ArrayList<>(nameToRoute.keySet());
		Collections.sort(routeNames);
	}

	private String getName(Route route)
	{
		if (!route.getShortName().isEmpty()) {
			return route.getShortName();
		}
		return route.getLongName();
	}

	private void printRouteInfo()
	{
		System.out.println("route: <name> (<versions>)");
		for (String name : routeNames) {
			Collection<Route> versions = nameToRoute.get(name);
			System.out.println(
					String.format("route: %s (%d)", name, versions.size()));
		}
	}

}
