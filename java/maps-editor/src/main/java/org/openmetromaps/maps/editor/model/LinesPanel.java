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

package org.openmetromaps.maps.editor.model;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;

public class LinesPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	private LinesListModel listModel;
	private JList<Line> list;

	public LinesPanel(ModelData data)
	{
		super(new BorderLayout());

		listModel = new LinesListModel(data);
		list = new JList<>(listModel);
		list.setCellRenderer(new LinesCellRenderer());

		JScrollPane jsp = new JScrollPane(list);
		add(jsp, BorderLayout.CENTER);
	}

	public List<Line> getSelectedLines()
	{
		return list.getSelectedValuesList();
	}

}
