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

package org.openmetromaps.model.osm.inspector;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.xml.output.OsmXmlSerializer;

public class ElementXmlDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public ElementXmlDialog(Window window, OsmEntity element)
	{
		super(window);

		OsmXmlSerializer osmSerializer = new OsmXmlSerializer(true);
		String prefix = null;
		String xml = null;
		if (element instanceof OsmNode) {
			prefix = "Node";
			xml = osmSerializer.write((OsmNode) element);
		} else if (element instanceof OsmWay) {
			prefix = "Way";
			xml = osmSerializer.write((OsmWay) element);
		} else if (element instanceof OsmRelation) {
			prefix = "Relation";
			xml = osmSerializer.write((OsmRelation) element);
		}

		setTitle(prefix + " " + element.getId());
		JTextArea textField = new JTextArea(xml);
		JScrollPane jsp = new JScrollPane(textField);
		setContentPane(jsp);
	}

}
