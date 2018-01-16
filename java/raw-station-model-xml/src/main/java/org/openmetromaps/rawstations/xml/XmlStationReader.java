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

package org.openmetromaps.rawstations.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.rawstations.Change;
import org.openmetromaps.rawstations.Exit;
import org.openmetromaps.rawstations.Location;
import org.openmetromaps.rawstations.RawStationModel;

import de.topobyte.xml.domabstraction.iface.IDocument;
import de.topobyte.xml.domabstraction.iface.IDocumentFactory;
import de.topobyte.xml.domabstraction.iface.IElement;
import de.topobyte.xml.domabstraction.iface.INodeList;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class XmlStationReader
{

	public static RawStationModel read(IDocumentFactory factory, InputStream is)
			throws ParsingException
	{
		XmlStationReader reader = new XmlStationReader();
		return reader.readModel(factory, is);
	}

	public static RawStationModel read(IDocument document)
			throws ParsingException
	{
		XmlStationReader reader = new XmlStationReader();
		return reader.readModel(document);
	}

	private String version;
	private List<Change> changes = new ArrayList<>();
	private List<Exit> exits = new ArrayList<>();

	private XmlStationReader()
	{
		// private constructor
	}

	private RawStationModel readModel(IDocumentFactory factory, InputStream is)
			throws ParsingException
	{
		IDocument doc = factory.parse(is);
		return readModel(doc);
	}

	private RawStationModel readModel(IDocument doc) throws ParsingException
	{
		parse(doc);

		return new RawStationModel(version, changes, exits);
	}

	private static final String ELEM_STATIONS = "omm-stations";
	private static final String ELEM_CHANGE = "change";
	private static final String ELEM_BATCH = "batch";

	private static final String ATTR_VERSION = "version";
	private static final String ATTR_LINE = "line";
	private static final String ATTR_TOWARDS = "towards";
	private static final String ATTR_REVERSE_LINE = "reverse-line";
	private static final String ATTR_REVERSE_TOWARDS = "reverse-towards";
	private static final String ATTR_AT = "at";
	private static final String ATTR_LOCATION = "location";
	private static final String ATTR_CHANGE_LINE = "change-line";
	private static final String ATTR_CHANGE_TOWARDS = "change-towards";
	private static final String ATTR_CHANGE_LINE_REGEX = "change-line-regex";
	private static final String ATTR_DERIVE_REVERSE_FROM = "derive-reverse-from";

	private void parse(IDocument doc)
	{
		INodeList allStations = doc.getElementsByTagName(ELEM_STATIONS);
		IElement firstStations = allStations.element(0);
		version = firstStations.getAttribute(ATTR_VERSION);

		INodeList changeList = firstStations
				.getChildElementsByTagName(ELEM_CHANGE);
		INodeList batchList = firstStations
				.getChildElementsByTagName(ELEM_BATCH);

		for (int i = 0; i < changeList.getLength(); i++) {
			IElement eChange = changeList.element(i);
			readChange(null, null, null, null, eChange);
		}

		for (int i = 0; i < batchList.getLength(); i++) {
			IElement eBatch = batchList.element(i);
			readBatch(eBatch);
		}
	}

	private void readBatch(IElement eBatch)
	{
		String line = eBatch.getAttribute(ATTR_LINE);
		String towards = eBatch.getAttribute(ATTR_TOWARDS);
		String reverseLine = getAttributeOrNull(eBatch, ATTR_REVERSE_LINE);
		String reverseTowards = getAttributeOrNull(eBatch,
				ATTR_REVERSE_TOWARDS);

		INodeList changeList = eBatch.getChildElementsByTagName(ELEM_CHANGE);

		for (int i = 0; i < changeList.getLength(); i++) {
			IElement eChange = changeList.element(i);
			readChange(line, towards, reverseLine, reverseTowards, eChange);
		}
	}

	private void readChange(String line, String towards, String reverseLine,
			String reverseTowards, IElement eChange)
	{
		if (line == null) {
			line = eChange.getAttribute(ATTR_LINE);
		}
		if (towards == null) {
			towards = eChange.getAttribute(ATTR_TOWARDS);
		}
		if (reverseLine == null) {
			reverseLine = getAttributeOrNull(eChange, ATTR_REVERSE_LINE);
		}
		if (reverseTowards == null) {
			reverseTowards = getAttributeOrNull(eChange, ATTR_REVERSE_TOWARDS);
		}

		String at = eChange.getAttribute(ATTR_AT);
		String valLocation = eChange.getAttribute(ATTR_LOCATION);
		String changeLine = getAttributeOrNull(eChange, ATTR_CHANGE_LINE);
		String changeTowards = getAttributeOrNull(eChange, ATTR_CHANGE_TOWARDS);
		String changeLineRegex = getAttributeOrNull(eChange,
				ATTR_CHANGE_LINE_REGEX);
		String valDeriveReverseFrom = eChange
				.getAttribute(ATTR_DERIVE_REVERSE_FROM);

		Location location = parseLocation(valLocation);

		boolean deriveReverseFrom = valDeriveReverseFrom.equals("true");

		changes.add(new Change(line, towards, reverseLine, reverseTowards, at,
				location, changeLine, changeTowards, changeLineRegex,
				deriveReverseFrom));
	}

	private String getAttributeOrNull(IElement element, String attribute)
	{
		if (!element.hasAttribute(attribute)) {
			return null;
		}
		return element.getAttribute(attribute);
	}

	private Location parseLocation(String value)
	{
		switch (value) {
		case "front":
			return Location.FRONT;
		case "almost front":
			return Location.ALMOST_FRONT;
		case "middle/middle front":
			return Location.MIDDLE_MIDDLE_FRONT;
		case "middle":
			return Location.MIDDLE;
		case "middle/middle back":
			return Location.MIDDLE_MIDDLE_BACK;
		case "almost back":
			return Location.ALMOST_BACK;
		case "back":
			return Location.BACK;
		}
		return null;
	}

}
