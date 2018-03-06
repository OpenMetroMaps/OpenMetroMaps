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

package org.openmetromaps.imports.config.reader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.imports.config.ImportConfig;
import org.openmetromaps.imports.config.Processing;
import org.openmetromaps.imports.config.Source;
import org.openmetromaps.imports.config.osm.OsmSource;
import org.openmetromaps.imports.config.osm.Routes;
import org.openmetromaps.imports.config.osm.Tag;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.BBoxString;
import de.topobyte.xml.domabstraction.iface.IDocument;
import de.topobyte.xml.domabstraction.iface.IDocumentFactory;
import de.topobyte.xml.domabstraction.iface.IElement;
import de.topobyte.xml.domabstraction.iface.INodeList;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class ImportConfigReader
{

	public static ImportConfig read(IDocumentFactory factory, InputStream is)
			throws ParsingException
	{
		ImportConfigReader reader = new ImportConfigReader();
		return reader.readModel(factory, is);
	}

	public static ImportConfig read(IDocument document) throws ParsingException
	{
		ImportConfigReader reader = new ImportConfigReader();
		return reader.readModel(document);
	}

	private String version;
	private Source source = null;
	private Processing processing = new Processing();

	private ImportConfigReader()
	{
		// private constructor
	}

	private ImportConfig readModel(IDocumentFactory factory, InputStream is)
			throws ParsingException
	{
		IDocument doc = factory.parse(is);
		return readModel(doc);
	}

	private ImportConfig readModel(IDocument doc) throws ParsingException
	{
		parseFile(doc);

		return new ImportConfig(version, source, processing);
	}

	private void parseFile(IDocument doc)
	{
		INodeList allOmmConfigs = doc.getElementsByTagName("omm-import-config");
		IElement firstOmmFile = allOmmConfigs.element(0);

		version = firstOmmFile.getAttribute("version");

		INodeList listSources = firstOmmFile
				.getChildElementsByTagName("source");
		IElement firstSource = listSources.element(0);

		String type = firstSource.getAttribute("type");
		if ("osm".equals(type)) {
			List<Routes> routes = parseRoutes(firstSource);
			source = new OsmSource(routes);
		}

		INodeList listProcessing = firstOmmFile
				.getChildElementsByTagName("processing");
		if (listProcessing.getLength() != 0) {
			IElement firstProcessing = listProcessing.element(0);
			parseProcessing(firstProcessing);
		}
	}

	private List<Routes> parseRoutes(IElement source)
	{
		INodeList listRoutes = source.getChildElementsByTagName("routes");

		List<Routes> routes = new ArrayList<>();

		for (int i = 0; i < listRoutes.getLength(); i++) {
			IElement eRoute = listRoutes.element(i);

			INodeList listBboxes = eRoute.getChildElementsByTagName("bbox");
			INodeList listTags = eRoute.getChildElementsByTagName("tag");

			BBox bbox = null;
			List<Tag> tags = new ArrayList<>();

			for (int k = 0; k < listBboxes.getLength(); k++) {
				IElement eBbox = listBboxes.element(k);
				String compactBbox = eBbox.getAttribute("compact");
				bbox = BBoxString.parse(compactBbox).toBbox();
				// BBoxString parsing expects lon,lat,lon,lat while config is
				// lat,lon,lat,lon. Fix this by swapping lon and lat values.
				bbox.set(bbox.getLat1(), bbox.getLon1(), bbox.getLat2(),
						bbox.getLon2());
			}

			for (int k = 0; k < listTags.getLength(); k++) {
				IElement eTag = listTags.element(k);
				String key = eTag.getAttribute("key");
				String value = eTag.getAttribute("value");
				tags.add(new Tag(key, value));
			}

			routes.add(new Routes(bbox, tags));
		}

		return routes;
	}

	private void parseProcessing(IElement processing)
	{
		INodeList listStations = processing
				.getChildElementsByTagName("stations");
		if (listStations.getLength() != 0) {
			IElement stations = listStations.element(0);
			parseStations(stations);
		}
	}

	private void parseStations(IElement stations)
	{
		INodeList listPrefixRemoval = stations
				.getChildElementsByTagName("prefix-removal");
		for (int i = 0; i < listPrefixRemoval.getLength(); i++) {
			IElement prefixRemoval = listPrefixRemoval.element(i);
			INodeList listPrefixes = prefixRemoval
					.getChildElementsByTagName("prefix");
			for (int k = 0; k < listPrefixes.getLength(); k++) {
				IElement prefix = listPrefixes.element(k);
				processing.getPrefixes().add(prefix.getAttribute("value"));
			}
		}

		INodeList listSuffixRemoval = stations
				.getChildElementsByTagName("suffix-removal");
		for (int i = 0; i < listSuffixRemoval.getLength(); i++) {
			IElement suffixRemoval = listSuffixRemoval.element(i);
			INodeList listSuffixes = suffixRemoval
					.getChildElementsByTagName("suffix");
			for (int k = 0; k < listSuffixes.getLength(); k++) {
				IElement suffix = listSuffixes.element(k);
				processing.getSuffixes().add(suffix.getAttribute("value"));
			}
		}
	}

}
