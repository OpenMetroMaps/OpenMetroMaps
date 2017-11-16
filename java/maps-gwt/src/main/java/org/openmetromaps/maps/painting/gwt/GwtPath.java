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

package org.openmetromaps.maps.painting.gwt;

import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.painting.core.geom.Path;
import org.openmetromaps.maps.painting.gwt.path.LineTo;
import org.openmetromaps.maps.painting.gwt.path.MoveTo;
import org.openmetromaps.maps.painting.gwt.path.PathOperation;

import com.google.gwt.canvas.dom.client.Context2d;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.lightgeom.lina.Vector2;

public class GwtPath implements Path
{

	private List<PathOperation> operations = new ArrayList<>();

	public void render(Context2d c)
	{
		for (PathOperation operation : operations) {
			operation.render(c);
		}
	}

	@Override
	public void reset()
	{
		operations.clear();
	}

	@Override
	public void moveTo(double x, double y)
	{
		operations.add(new MoveTo(x, y));
	}

	@Override
	public void lineTo(double x, double y)
	{
		operations.add(new LineTo(x, y));
	}

	@Override
	public void moveTo(Point p)
	{
		operations.add(new MoveTo(p.getX(), p.getY()));
	}

	@Override
	public void lineTo(Point p)
	{
		operations.add(new LineTo(p.getX(), p.getY()));
	}

	@Override
	public void moveTo(Vector2 p)
	{
		operations.add(new MoveTo(p.getX(), p.getY()));
	}

	@Override
	public void lineTo(Vector2 p)
	{
		operations.add(new LineTo(p.getX(), p.getY()));
	}

}
