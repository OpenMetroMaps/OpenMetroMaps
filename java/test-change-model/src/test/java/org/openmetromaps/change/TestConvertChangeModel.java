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

package org.openmetromaps.change;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.rawchange.RawChangeModel;
import org.openmetromaps.rawchange.xml.DesktopXmlChangeReader;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class TestConvertChangeModel
{

	public static void main(String[] args)
			throws ParserConfigurationException, IOException, ParsingException
	{
		InputStream input = TestConvertChangeModel.class.getClassLoader()
				.getResourceAsStream("berlin-changes.xml");
		RawChangeModel rawModel = DesktopXmlChangeReader.read(input);
		ChangeModel model = ChangeModels.derive(rawModel);

		for (Change change : model.getChanges()) {
			String changeLine = null;

			Matcher matcher = change.getMatcher();
			if (matcher instanceof SimpleMatcher) {
				SimpleMatcher sm = (SimpleMatcher) matcher;
				changeLine = sm.getName();
			} else if (matcher instanceof RegexMatcher) {
				RegexMatcher rm = (RegexMatcher) matcher;
				changeLine = rm.getPattern();
			}

			System.out
					.println(String.format("line %s towards %s at %s to %s: %s",
							change.getLine(), change.getTowards(),
							change.getAt(), changeLine, change.getLocation()));
		}
	}

}
