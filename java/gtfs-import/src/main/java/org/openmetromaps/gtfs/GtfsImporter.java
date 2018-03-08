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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipException;

import org.openmetromaps.gtfs4j.csv.GtfsZip;
import org.openmetromaps.gtfs4j.model.Agency;
import org.openmetromaps.gtfs4j.model.Route;
import org.openmetromaps.gtfs4j.model.Stop;
import org.openmetromaps.gtfs4j.model.StopTime;
import org.openmetromaps.gtfs4j.model.Trip;
import org.openmetromaps.misc.NameChanger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import de.topobyte.collections.util.ListUtil;

public class GtfsImporter
{

	private Path path;
	private NameChanger nameChanger;
	private boolean removeBoms;

	private GtfsZip zip;

	private Multimap<String, Route> nameToRoute = HashMultimap.create();
	private List<String> routeNames;
	private Multimap<String, Trip> routeIdToTrips = HashMultimap.create();
	private Multimap<String, StopRef> tripIdToStopRefs = HashMultimap.create();
	private Map<String, StopIdList> tripIdToStopList = Maps.newHashMap();
	private Map<String, Stop> stopIdToStop = Maps.newHashMap();
	private Map<String, StopIdList> selectedStopLists = Maps.newHashMap();

	private DraftModel model = new DraftModel();

	public GtfsImporter(Path path, NameChanger nameChanger, boolean removeBoms)
	{
		this.path = path;
		this.nameChanger = nameChanger;
		this.removeBoms = removeBoms;
	}

	public DraftModel getModel()
	{
		return model;
	}

	public void execute() throws ZipException, IOException
	{
		if (removeBoms) {
			Path tmp = Files.createTempFile("gtfs", ".zip");
			GtfsBomRemover bomRemover = new GtfsBomRemover(path, tmp);
			bomRemover.execute();

			zip = new GtfsZip(tmp);
			tmp.toFile().deleteOnExit();
		} else {
			zip = new GtfsZip(path);
		}

		printAgencyInfo();

		readRoutes();

		printRouteInfo();

		readStops();

		readTrips();

		readStopTimes();

		buildTripStopLists();

		analyzeRoutes();

		createModel();

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

	private void readTrips() throws IOException
	{
		List<Trip> trips = zip.readTrips();

		for (Trip trip : trips) {
			routeIdToTrips.put(trip.getRouteId(), trip);
		}
	}

	private void readStopTimes() throws IOException
	{
		List<StopTime> stopTimes = zip.readStopTimes();
		for (StopTime stopTime : stopTimes) {
			String tripId = stopTime.getTripId();
			String valSeq = stopTime.getStopSequence();
			int seq = Integer.parseInt(valSeq);
			String stopId = stopTime.getStopId();
			Stop stop = stopIdToStop.get(stopId);
			String parentStation = stop.getParentStation();
			String stationId = stop.getId();
			if (parentStation != null && !parentStation.isEmpty()) {
				stationId = parentStation;
			}
			StopRef stopRef = new StopRef(seq, stationId);
			tripIdToStopRefs.put(tripId, stopRef);
		}
	}

	private void buildTripStopLists()
	{
		Set<String> tripIds = tripIdToStopRefs.keySet();
		for (String tripId : tripIds) {
			List<StopRef> refs = Lists
					.newArrayList(tripIdToStopRefs.get(tripId));
			Collections.sort(refs);
			StopIdList stops = new StopIdList(refs.size());
			for (StopRef ref : refs) {
				stops.add(ref.getStopId());
			}
			tripIdToStopList.put(tripId, stops);
		}
		tripIdToStopRefs.clear();
	}

	private void readStops() throws IOException
	{
		List<Stop> stops = zip.readStops();

		for (Stop stop : stops) {
			stopIdToStop.put(stop.getId(), stop);
		}
	}

	private void analyzeRoutes()
	{
		for (String routeName : routeNames) {
			Collection<Route> versions = nameToRoute.get(routeName);
			List<Trip> trips = new ArrayList<>();
			for (Route route : versions) {
				trips.addAll(routeIdToTrips.get(route.getId()));
			}

			if (trips.isEmpty()) {
				System.out.println(
						String.format("%s: no trips found", routeName));
				continue;
			}

			System.out.println(
					String.format("%s: %d trips", routeName, trips.size()));
			Multiset<StopIdList> stopIdListSet = HashMultiset.create();
			for (Trip trip : trips) {
				StopIdList stopsIds = tripIdToStopList.get(trip.getId());
				stopIdListSet.add(stopsIds);
			}

			Multiset<StopIdList> histogram = Multisets
					.copyHighestCountFirst(stopIdListSet);

			StopIdList longest = null;
			int maxStops = -1;
			for (StopIdList stopIds : histogram.elementSet()) {
				if (stopIds.size() > maxStops) {
					maxStops = stopIds.size();
					longest = stopIds;
				}
			}

			selectedStopLists.put(routeName, longest);

			List<String> stopsLongest = getStopNameList(longest);
			List<String> stopsReverse = Lists.reverse(stopsLongest);
			System.out.println("longest: " + stopInfo(stopsLongest));

			for (StopIdList stopIds : histogram.elementSet()) {
				int count = stopIdListSet.count(stopIds);
				List<String> stops = getStopNameList(stopIds);

				if (isPart(stops, stopsLongest)) {
					continue;
				} else if (isPart(stops, stopsReverse)) {
					continue;
				}

				System.out.println(
						String.format("%dx: %s", count, stopInfo(stops)));
			}
		}
	}

	// Determine if 'stops' is a subsequence of 'reference'
	private boolean isPart(List<String> stops, List<String> reference)
	{
		String first = stops.get(0);
		int pos = reference.indexOf(first);
		if (pos < 0) {
			return false;
		}

		int nStops = stops.size();
		int nReference = reference.size();
		if (pos + nStops > nReference) {
			return false;
		}

		List<String> sub = reference.subList(pos, pos + nStops);
		return stops.equals(sub);
	}

	private String stopInfo(List<String> stops)
	{
		String first = stops.get(0);
		String last = ListUtil.last(stops);
		return String.format("%s to %s via %d stops", first, last,
				stops.size() - 2);
	}

	private List<String> getStopNameList(StopIdList stopIds)
	{
		List<String> stops = new ArrayList<>(stopIds.size());
		for (String id : stopIds) {
			Stop stop = stopIdToStop.get(id);
			String name = stop.getName();
			String fixed = nameChanger.applyNameFixes(name);
			stops.add(fixed);
		}
		return stops;
	}

	private void createModel()
	{
		List<DraftLine> lines = model.getLines();
		Map<String, DraftStation> idToStation = new HashMap<>();

		Set<String> allStopIds = new HashSet<>();
		for (String routeName : routeNames) {
			StopIdList stopIds = selectedStopLists.get(routeName);

			if (stopIds == null) {
				continue;
			}

			allStopIds.addAll(stopIds);

			List<DraftStation> stations = new ArrayList<>();

			for (String id : stopIds) {
				DraftStation station = idToStation.get(id);
				if (station == null) {
					Stop stop = stopIdToStop.get(id);
					String name = stop.getName();
					String fixed = nameChanger.applyNameFixes(name);
					double lat = Double.parseDouble(stop.getLat());
					double lon = Double.parseDouble(stop.getLon());
					station = new DraftStation(fixed, id, lon, lat);
					idToStation.put(id, station);
				}
				stations.add(station);
			}

			Collection<Route> routes = nameToRoute.get(routeName);
			Route route = routes.iterator().next();
			String color = route.getColor();
			System.out.println(String.format(
					"route '%s', number of stops: %d, color: '%s'", routeName,
					stopIds.size(), color));

			lines.add(new DraftLine(routeName, stations, color));
		}
		System.out.println("Total number of stations: " + allStopIds.size());
	}

}
