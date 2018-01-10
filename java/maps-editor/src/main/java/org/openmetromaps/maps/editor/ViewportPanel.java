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

package org.openmetromaps.maps.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openmetromaps.maps.ScrollableAdvancedPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.viewports.scrolling.ViewportListener;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class ViewportPanel extends JPanel
{

	final static Logger logger = LoggerFactory.getLogger(ViewportPanel.class);

	private static final long serialVersionUID = 1L;

	private MapEditor mapEditor;

	private JLabel displaySize;
	private JLabel displayPos;
	private JLabel displayCenter;
	private JLabel displayZoom;

	public ViewportPanel(MapEditor mapEditor)
	{
		super(new GridBagLayout());
		this.mapEditor = mapEditor;

		setupLayout();

		ScrollableAdvancedPanel map = mapEditor.getMap();
		map.addViewportListener(new ViewportListener() {

			@Override
			public void zoomChanged()
			{
				refresh();
			}

			@Override
			public void viewportChanged()
			{
				refresh();
			}

			@Override
			public void complexChange()
			{
				refresh();
			}

		});
	}

	private void setupLayout()
	{
		JLabel labelSize = new JLabel("size:");
		JLabel labelPos = new JLabel("pos:");
		JLabel labelCenter = new JLabel("center:");
		JLabel labelZoom = new JLabel("zoom:");

		displaySize = new JLabel();
		displayPos = new JLabel();
		displayCenter = new JLabel();
		displayZoom = new JLabel();

		GridBagConstraintsEditor ce = new GridBagConstraintsEditor();
		GridBagConstraints c = ce.getConstraints();

		ce.fill(GridBagConstraints.BOTH);
		ce.weight(0, 0);
		ce.gridPos(0, 0);
		add(labelSize, c);
		ce.gridPos(0, 1);
		add(labelPos, c);
		ce.gridPos(0, 2);
		add(labelCenter, c);
		ce.gridPos(0, 3);
		add(labelZoom, c);

		ce.weight(1, 0);
		ce.gridPos(1, 0);
		add(displaySize, c);
		ce.gridPos(1, 1);
		add(displayPos, c);
		ce.gridPos(1, 2);
		add(displayCenter, c);
		ce.gridPos(1, 3);
		add(displayZoom, c);

		ce.gridPos(0, 4);
		ce.weight(1, 1);
		ce.gridWidth(2);
		add(new JPanel(), c);
	}

	protected void refresh()
	{
		ScrollableAdvancedPanel map = mapEditor.getMap();

		double cx = ViewportUtil.getRealX(map, map.getWidth() / 2);
		double cy = ViewportUtil.getRealY(map, map.getHeight() / 2);

		displaySize.setText(
				String.format("%d x %d", map.getWidth(), map.getHeight()));
		displayPos.setText(String.format("%.1f,%.1f", map.getPositionX(),
				map.getPositionY()));
		displayCenter.setText(String.format("%.1f,%.1f", cx, cy));
		displayZoom.setText(String.format("%.2f", map.getZoom()));
	}

}
