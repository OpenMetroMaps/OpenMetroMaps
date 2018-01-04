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

package org.openmetromaps.change.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.change.Change;
import org.openmetromaps.change.ChangeModel;
import org.openmetromaps.change.Exit;
import org.openmetromaps.change.Location;
import org.openmetromaps.change.Matcher;
import org.openmetromaps.change.RegexMatcher;
import org.openmetromaps.change.SimpleMatcher;

import de.topobyte.xml.domabstraction.iface.IDocument;
import de.topobyte.xml.domabstraction.iface.IDocumentFactory;
import de.topobyte.xml.domabstraction.iface.IElement;
import de.topobyte.xml.domabstraction.iface.INodeList;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class XmlChangeReader
{

	public static ChangeModel read(IDocumentFactory factory, InputStream is)
			throws ParsingException
	{
		XmlChangeReader reader = new XmlChangeReader();
		return reader.readModel(factory, is);
	}

	public static ChangeModel read(IDocument document) throws ParsingException
	{
		XmlChangeReader reader = new XmlChangeReader();
		return reader.readModel(document);
	}

	private List<Change> changes = new ArrayList<>();
	private List<Exit> exits = new ArrayList<>();

	private XmlChangeReader()
	{
		// private constructor
	}

	private ChangeModel readModel(IDocumentFactory factory, InputStream is)
			throws ParsingException
	{
		IDocument doc = factory.parse(is);
		return readModel(doc);
	}

	private ChangeModel readModel(IDocument doc) throws ParsingException
	{
		parse(doc);

		return new ChangeModel(changes, exits);
	}

	private static final String ELEM_CHANGES = "changes";
	private static final String ELEM_CHANGE = "change";
	private static final String ELEM_BATCH = "batch";

	private static final String ATTR_LINE = "line";
	private static final String ATTR_TOWARDS = "towards";
	private static final String ATTR_AT = "at";
	private static final String ATTR_LOCATION = "location";
	private static final String ATTR_CHANGE_LINE = "change-line";
	private static final String ATTR_CHANGE_LINE_REGEX = "change-line-regex";
	private static final String ATTR_DERIVE_REVERSE = "derive-reverse";

	private void parse(IDocument doc)
	{
		INodeList allChanges = doc.getElementsByTagName(ELEM_CHANGES);
		IElement firstChanges = allChanges.element(0);
		INodeList changeList = firstChanges
				.getChildElementsByTagName(ELEM_CHANGE);
		INodeList batchList = firstChanges
				.getChildElementsByTagName(ELEM_BATCH);

		for (int i = 0; i < changeList.getLength(); i++) {
			IElement eChange = changeList.element(i);
			readChange(null, null, eChange);
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

		INodeList changeList = eBatch.getChildElementsByTagName(ELEM_CHANGE);

		for (int i = 0; i < changeList.getLength(); i++) {
			IElement eChange = changeList.element(i);
			readChange(line, towards, eChange);
		}
	}

	private void readChange(String line, String towards, IElement eChange)
	{
		if (line == null) {
			line = eChange.getAttribute(ATTR_LINE);
		}
		if (towards == null) {
			towards = eChange.getAttribute(ATTR_TOWARDS);
		}

		String at = eChange.getAttribute(ATTR_AT);
		String valLocation = eChange.getAttribute(ATTR_LOCATION);
		String changeLine = eChange.getAttribute(ATTR_CHANGE_LINE);
		String changeLineRegex = eChange.getAttribute(ATTR_CHANGE_LINE_REGEX);
		String valDeriveReverse = eChange.getAttribute(ATTR_DERIVE_REVERSE);

		Location location = parseLocation(valLocation);

		Matcher matcher = null;
		if (changeLine != null) {
			matcher = new SimpleMatcher(changeLine);
		} else if (changeLineRegex != null) {
			matcher = new RegexMatcher(changeLineRegex);
		}

		boolean deriveReverse = valDeriveReverse.equals("true");

		changes.add(new Change(line, towards, at, location, matcher));
		if (deriveReverse) {
			// TODO: we need the map model to determine the reverse direction
			// (towards)
			changes.add(
					new Change(line, towards, at, reverse(location), matcher));
		}
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

	private Location reverse(Location location)
	{
		if (location == null) {
			return null;
		}
		switch (location) {
		case FRONT:
			return Location.BACK;
		case ALMOST_FRONT:
			return Location.ALMOST_BACK;
		case MIDDLE_MIDDLE_FRONT:
			return Location.MIDDLE_MIDDLE_BACK;
		case MIDDLE:
			return Location.MIDDLE;
		case MIDDLE_MIDDLE_BACK:
			return Location.MIDDLE_MIDDLE_FRONT;
		case ALMOST_BACK:
			return Location.ALMOST_FRONT;
		case BACK:
			return Location.FRONT;
		}
		return null;
	}

}
