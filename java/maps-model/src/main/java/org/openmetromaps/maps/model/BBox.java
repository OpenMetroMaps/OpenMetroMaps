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

package org.openmetromaps.maps.model;

public class BBox
{

	private double lon1, lon2, lat1, lat2;

	/**
	 * @param lon1
	 *            the first longitude.
	 * @param lat1
	 *            the first latitude.
	 * @param lon2
	 *            the second longitude.
	 * @param lat2
	 *            the second latitude.
	 */
	public BBox(double lon1, double lat1, double lon2, double lat2)
	{
		this.lon1 = lon1;
		this.lat1 = lat1;
		this.lon2 = lon2;
		this.lat2 = lat2;
		validate();
	}

	public void set(double lon1, double lat1, double lon2, double lat2)
	{
		this.lon1 = lon1;
		this.lat1 = lat1;
		this.lon2 = lon2;
		this.lat2 = lat2;
		validate();
	}

	public BBox(BBox other)
	{
		this.lon1 = other.lon1;
		this.lon2 = other.lon2;
		this.lat1 = other.lat1;
		this.lat2 = other.lat2;
	}

	/**
	 * @return the first longitude
	 */
	public double getLon1()
	{
		return lon1;
	}

	/**
	 * @return the second longitude
	 */
	public double getLon2()
	{
		return lon2;
	}

	/**
	 * @return the first latitude
	 */
	public double getLat1()
	{
		return lat1;
	}

	/**
	 * @return the second latitude
	 */
	public double getLat2()
	{
		return lat2;
	}

	/**
	 * @param lon1
	 *            the first longitude
	 */
	public void setLon1(double lon1)
	{
		this.lon1 = lon1;
		validate();
	}

	/**
	 * @param lon2
	 *            the second longitude
	 */
	public void setLon2(double lon2)
	{
		this.lon2 = lon2;
		validate();
	}

	/**
	 * @param lat1
	 *            the first latitude
	 */
	public void setLat1(double lat1)
	{
		this.lat1 = lat1;
		validate();
	}

	/**
	 * @param lat2
	 *            the second latitude
	 */
	public void setLat2(double lat2)
	{
		this.lat2 = lat2;
		validate();
	}

	/*
	 * make sure that the bounding box is valid, i.e. lon1 <= lon2 and lat1 >=
	 * lat2
	 */
	private void validate()
	{
		if (lon1 > lon2) {
			double tmp = lon1;
			lon1 = lon2;
			lon2 = tmp;
		}
		if (lat1 < lat2) {
			double tmp = lat1;
			lat1 = lat2;
			lat2 = tmp;
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof BBox)) {
			return false;
		}
		BBox otherBox = (BBox) other;
		return otherBox.lon1 == lon1 && otherBox.lon2 == lon2
				&& otherBox.lat1 == lat1 && otherBox.lat2 == lat2;
	}

}
