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

package org.openmetromaps.ci;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.imports.config.ImportConfig;
import org.openmetromaps.imports.config.Processing;
import org.openmetromaps.imports.config.osm.OsmSource;
import org.openmetromaps.imports.config.reader.DesktopImportConfigReader;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.maps.xml.XmlModelWriter;
import org.openmetromaps.model.osm.Fix;
import org.openmetromaps.model.osm.filter.RouteFilter;
import org.openmetromaps.osm.OsmImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;
import de.topobyte.system.utils.SystemPaths;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class CiTools
{

	final static Logger logger = LoggerFactory.getLogger(CiTools.class);

	static Path determineProjetRoot()
	{
		Path cwd = SystemPaths.CWD;
		if (cwd.endsWith(Paths.get("java/ci"))) {
			return cwd.getParent().getParent();
		}
		return cwd;
	}

	static ImportConfig loadConfig(Path pathConfig)
			throws IOException, ParsingException
	{
		try (InputStream isConfig = Files.newInputStream(pathConfig)) {
			return DesktopImportConfigReader.read(isConfig);
		}
	}

	static ModelData loadOsmData(ImportConfig config, InMemoryMapDataSet data)
	{
		if (!(config.getSource() instanceof OsmSource)) {
			throw new IllegalArgumentException(
					"Config is not an OSM configuration");
		}

		OsmSource source = (OsmSource) config.getSource();
		Processing processing = config.getProcessing();

		RouteFilter routeFilter = new OsmSourceRouteFilter(source);
		List<Fix> fixes = new ArrayList<>();

		System.out.println("Importing from OSM data set...");

		return OsmImporter.execute(data, routeFilter, processing.getPrefixes(),
				processing.getSuffixes(), fixes);
	}

	static ModelData loadReferenceData(Path pathReferenceOmm)
			throws IOException, ParsingException
	{
		System.out.println("Loading reference OMM file...");
		try (InputStream is = Files.newInputStream(pathReferenceOmm)) {
			XmlModel xmlModel = DesktopXmlModelReader.read(is);
			return new XmlModelConverter().convert(xmlModel).getData();
		}
	}

	static List<String> compare(ModelData osmData, ModelData referenceData)
	{
		List<String> diffs = new ArrayList<>();
		List<String> missingLines = new ArrayList<>();
		List<String> contentDiffs = new ArrayList<>();

		// 1. Group Lines by Name
		Map<String, Line> osmLines = mapLines(osmData.lines);
		Map<String, Line> refLines = mapLines(referenceData.lines);

		Set<String> allNames = new HashSet<>();
		allNames.addAll(osmLines.keySet());
		allNames.addAll(refLines.keySet());

		List<String> sortedNames = new ArrayList<>(allNames);
		Collections.sort(sortedNames);

		for (String name : sortedNames) {
			boolean inOsm = osmLines.containsKey(name);
			boolean inRef = refLines.containsKey(name);

			if (inOsm && !inRef) {
				missingLines
						.add("Line present in OSM but missing in Ref: " + name);
			} else if (!inOsm && inRef) {
				missingLines
						.add("Line present in Ref but missing in OSM: " + name);
			} else {
				// In both, compare stations
				Line osmLine = osmLines.get(name);
				Line refLine = refLines.get(name);
				List<String> lineDiffs = compareLineStations(osmLine, refLine);
				if (!lineDiffs.isEmpty()) {
					contentDiffs.add("Differences in Line " + name + ":");
					contentDiffs.addAll(lineDiffs);
				}
			}
		}

		diffs.addAll(missingLines);
		if (!missingLines.isEmpty() && !contentDiffs.isEmpty()) {
			diffs.add("");
		}
		diffs.addAll(contentDiffs);

		return diffs;
	}

	private static Map<String, Line> mapLines(List<Line> lines)
	{
		Map<String, Line> map = new HashMap<>();
		for (Line line : lines) {
			map.put(line.getName(), line);
		}
		return map;
	}

	private static List<String> compareLineStations(Line osmLine, Line refLine)
	{
		List<String> diffs = new ArrayList<>();
		List<String> osmStations = getStationNames(osmLine);
		List<String> refStations = getStationNames(refLine);

		if (osmStations.equals(refStations)) {
			return diffs;
		}

		// Accept reversed lines as the same, does not really matter
		ArrayList<String> osmReversed = new ArrayList<>(osmStations);
		Collections.reverse(osmReversed);
		if (osmReversed.equals(refStations)) {
			return diffs;
		}

		// Report simple diff of lists for now
		if (osmStations.size() != refStations.size()) {
			diffs.add(String.format("  Station count mismatch: OSM=%d, Ref=%d",
					osmStations.size(), refStations.size()));
		}

		// Find stations in OSM not in Ref
		List<String> inOsmOnly = new ArrayList<>(osmStations);
		inOsmOnly.removeAll(refStations);
		if (!inOsmOnly.isEmpty()) {
			diffs.add("  Stations in OSM only: " + inOsmOnly);
		}

		// Find stations in Ref not in OSM
		List<String> inRefOnly = new ArrayList<>(refStations);
		inRefOnly.removeAll(osmStations);
		if (!inRefOnly.isEmpty()) {
			diffs.add("  Stations in Ref only: " + inRefOnly);
		}

		// If sequence is different but sets are same (or similar), report
		// sequence
		if (inOsmOnly.isEmpty() && inRefOnly.isEmpty()
				&& !osmStations.equals(refStations)) {
			diffs.add("  Station sequence mismatch:");
			diffs.add("    OSM: " + osmStations);
			diffs.add("    Ref: " + refStations);
		}

		return diffs;
	}

	private static List<String> getStationNames(Line line)
	{
		List<String> names = new ArrayList<>();
		for (Stop stop : line.getStops()) {
			names.add(stop.getStation().getName());
		}
		return names;
	}

	static void compareAndExit(ModelData osmData, ModelData referenceData)
	{
		List<String> diffs = CiTools.compare(osmData, referenceData);

		if (diffs.isEmpty()) {
			System.out.println("SUCCESS: No differences found.");
			System.exit(0);
		} else {
			System.out.println("FAILURE: Differences found:");
			for (String diff : diffs) {
				System.out.println(diff);
			}
			System.exit(1);
		}
	}

	public static void save(ModelData data, Path pathOutput)
			throws IOException, ParserConfigurationException
	{
		try (OutputStream os = Files.newOutputStream(pathOutput)) {
			new XmlModelWriter().write(os, data, new ArrayList<>());
		}
	}

}
