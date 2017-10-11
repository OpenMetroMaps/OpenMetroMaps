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

package org.openmetromaps.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.geo.BBox;
import de.topobyte.geomath.WGS84;
import de.topobyte.interactiveview.ZoomChangedListener;
import de.topobyte.jeography.core.OverlayPoint;
import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.core.mapwindow.MapWindowChangeListener;
import de.topobyte.jeography.core.mapwindow.MapWindowWorldScaleListener;
import de.topobyte.jeography.core.viewbounds.BoundsInfo;
import de.topobyte.jeography.core.viewbounds.NopViewBounds;
import de.topobyte.jeography.core.viewbounds.ViewBounds;

public class SteplessMapWindow
{

	final static Logger logger = LoggerFactory
			.getLogger(SteplessMapWindow.class);

	private static final int DEFAULT_ZOOM_MIN = 1;
	private static final int DEFAULT_ZOOM_MAX = 18;

	private int zoomMin = DEFAULT_ZOOM_MIN;
	private int zoomMax = DEFAULT_ZOOM_MAX;

	// Center longitude and latitude
	double lon;
	double lat;

	// The current zoomlevel of the world image
	double zoom;

	// The size of the map window
	int width, height;

	// The whole world is mapped to a huge image of worldsize * scale pixels
	double worldsize;
	int worldscale = Tile.SIZE;
	double worldsizePixels;
	// These coordinates are the pixel coordinates of the top left corner of the
	// map window, i.e.portion of the map that we are looking at through this
	// window.
	int px, py;

	private ViewBounds bounds = new NopViewBounds();

	private void worldsize()
	{
		worldsize = Math.pow(2, zoom);
		worldsizePixels = worldsize * worldscale;
	}

	/**
	 * @param width
	 *            the window's width.
	 * @param height
	 *            the window's height.
	 * @param zoom
	 *            the zoom level.
	 * @param px
	 *            the x coordinate.
	 * @param py
	 *            the y coordinate.
	 */
	public SteplessMapWindow(int width, int height, double zoom, int px, int py)
	{
		this.width = width;
		this.height = height;
		this.zoom = zoom;
		worldsize();
		this.px = px;
		this.py = py;
		geoFromTiles();
	}

	/**
	 * @param width
	 *            the window's width.
	 * @param height
	 *            the window's height.
	 * @param zoom
	 *            the zoom level.
	 * @param lon
	 *            the center longitude.
	 * @param lat
	 *            the center latitude.
	 */
	public SteplessMapWindow(int width, int height, double zoom, double lon,
			double lat)
	{
		this.width = width;
		this.height = height;
		this.zoom = zoom;
		worldsize();
		this.lon = lon;
		this.lat = lat;
		tilesFromGeo();
	}

	/**
	 * @param bbox
	 *            a bounding box to cover.
	 * @param zoom
	 *            the zoomlevel to use.
	 */
	public SteplessMapWindow(BBox bbox, double zoom)
	{
		this.zoom = zoom;
		worldsize();
		// compute tx, ty, xoff, yoff, width, height
		double tileX1 = WGS84.lon2merc(bbox.getLon1(), worldsize);
		double tileY1 = WGS84.lat2merc(bbox.getLat1(), worldsize);
		double tileX2 = WGS84.lon2merc(bbox.getLon2(), worldsize);
		double tileY2 = WGS84.lat2merc(bbox.getLat2(), worldsize);
		logger.debug(
				String.format("%f,%f %f,%f", tileX1, tileY1, tileX2, tileY2));

		px = (int) Math.round(tileX1 * worldscale);
		py = (int) Math.round(tileY1 * worldscale);

		width = (int) Math.ceil((tileX2 - tileX1) * worldscale);
		height = (int) Math.ceil((tileY2 - tileY1) * worldscale);
		logger.debug(String.format("%d,%d", width, height));

		geoFromTiles();
	}

	public void setViewBounds(ViewBounds bounds)
	{
		this.bounds = bounds;
	}

	private void geoFromTiles()
	{
		// compute lon/lat
		lon = getCenterLon();
		lat = getCenterLat();
		if (fixViewBounds()) {
			tilesFromGeo();
		}
	}

	private boolean fixViewBounds()
	{
		BoundsInfo info = bounds.checkBounds(lon, lat);
		switch (info) {
		default:
		case OK:
			return false;
		case LON_OUT_OF_BOUNDS:
			lon = bounds.fixLon(lon);
			return true;
		case LAT_OUT_OF_BOUNDS:
			lat = bounds.fixLat(lat);
			return true;
		case LON_LAT_OUT_OF_BOUNDS:
			lon = bounds.fixLon(lon);
			lat = bounds.fixLat(lat);
			return true;
		}
	}

	private void tilesFromGeo()
	{
		// compute tx, ty, xoff, yoff
		double tileX = WGS84.lon2merc(lon, worldsize);
		double tileY = WGS84.lat2merc(lat, worldsize);

		// NOTE: floor is important here. A simple cast to int
		// won't do it in the case of negative values
		px = (int) Math.floor(tileX * worldscale - width / 2.0);
		py = (int) Math.floor(tileY * worldscale - height / 2.0);

		logger.debug(String.format("%d,%d", px, py));
	}

	/**
	 * Get the window's center's tile coordinate.
	 * 
	 * @return the center's tile coordinate.
	 */
	public double getCenterX()
	{
		double x = px;
		x += width / 2.0;
		return x / worldscale;
	}

	/**
	 * Get the window's center's tile coordinate.
	 * 
	 * @return the center's tile coordinate.
	 */
	public double getCenterY()
	{
		double y = py;
		y += height / 2.0;
		return y / worldscale;
	}

	/**
	 * @return the MapWindow's center's longitude
	 */
	public double getCenterLon()
	{
		return WGS84.merc2lon(getCenterX(), worldsize);
	}

	/**
	 * @return the MapWindow's center's latitude
	 */
	public double getCenterLat()
	{
		return WGS84.merc2lat(getCenterY(), worldsize);
	}

	/**
	 * Get the tile coordinate at the given x in view space.
	 * 
	 * @param px
	 *            the position in view space.
	 * @return the position in tile space.
	 */
	public double getPositionX(int px)
	{
		double x = this.px;
		x += px;
		return x / worldscale;
	}

	/**
	 * Get the tile coordinate at the given y in view space.
	 * 
	 * @param py
	 *            the position in view space.
	 * @return the position in tile space.
	 */
	public double getPositionY(int py)
	{
		double y = this.py;
		y += py;
		return y / worldscale;
	}

	/**
	 * Get the longitude of the given x in view space.
	 * 
	 * @param x
	 *            the position in view space.
	 * @return the longitude of this position.
	 */
	public double getPositionLon(int x)
	{
		return WGS84.merc2lon(getPositionX(x), worldsize);
	}

	/**
	 * Get the latitude of the given y in view space.
	 * 
	 * @param y
	 *            the position in view space.
	 * @return the latitude of this position.
	 */
	public double getPositionLat(int y)
	{
		return WGS84.merc2lat(getPositionY(y), worldsize);
	}

	/**
	 * Get the covered area as a bounding box.
	 * 
	 * @return the bounding box.
	 */
	public BBox getBoundingBox()
	{
		double lon1 = getPositionLon(0);
		double lon2 = getPositionLon(width);
		double lat1 = getPositionLat(0);
		double lat2 = getPositionLat(height);
		return new BBox(lon1, lat1, lon2, lat2);
	}

	/**
	 * @return the current zoom level.
	 */
	public double getZoom()
	{
		return zoom;
	}

	/**
	 * @return the current width.
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * @return the current height.
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * Adjust the size of the window.
	 * 
	 * @param newWidth
	 *            the new width value.
	 * @param newHeight
	 *            the new height value.
	 */
	public void resize(int newWidth, int newHeight)
	{
		width = newWidth;
		height = newHeight;
		tilesFromGeo();
		fireChangeListeners();
	}

	/**
	 * Reposition the map window with the given values. Positive values move the
	 * window up and left. You may supply negative values here.
	 * 
	 * @param dx
	 *            the amount of pixels to move to the left.
	 * @param dy
	 *            the amount of pixels to move to the top.
	 */
	public void move(int dx, int dy)
	{
		// to move down right, supply negative values here.
		px += dx;
		py += dy;

		geoFromTiles();
		fireChangeListeners();
	}

	/**
	 * Set the maximum allowed zoomlevel.
	 * 
	 * @param zoomMax
	 *            the maximum zoomlevel to allow
	 */
	public void setMaxZoom(int zoomMax)
	{
		this.zoomMax = zoomMax;
	}

	/**
	 * Set the minimum allowed zoomlevel.
	 * 
	 * @param zoomMin
	 *            the minimum zoomlevel to allow
	 */
	public void setMinZoom(int zoomMin)
	{
		this.zoomMin = zoomMin;
	}

	/**
	 * @return the maximal zoom level allowed.
	 */
	public int getMaxZoom()
	{
		return zoomMax;
	}

	/**
	 * @return the minimal zoom level allowed.
	 */
	public int getMinZoom()
	{
		return zoomMin;
	}

	/**
	 * zoom in to center if possible.
	 * 
	 * @return whether this operation changed the zoom level.
	 */
	public boolean zoomIn(double d)
	{
		double oldZoom = zoom;
		if (zoom < zoomMax) {
			zoom = Math.min(zoom + d, zoomMax);
			worldsize();
		}
		tilesFromGeo();
		fireChangeListeners();
		fireZoomListeners();
		return zoom != oldZoom;
	}

	/**
	 * zoom out from center if possible.
	 * 
	 * @return whether this operation changed the zoom level.
	 */
	public boolean zoomOut(double d)
	{
		double oldZoom = zoom;
		if (zoom > zoomMin) {
			zoom = Math.max(zoom - d, zoomMin);
			worldsize();
		}
		tilesFromGeo();
		fireChangeListeners();
		fireZoomListeners();
		return zoom != oldZoom;
	}

	/**
	 * zoom in to the denoted zoomlevel if possible.
	 * 
	 * @param zoomlevel
	 *            the new zoomlevel to set.
	 * 
	 * @return whether this operation changed the zoom level.
	 */
	public boolean zoom(double zoomlevel)
	{
		if (zoomlevel > zoomMax || zoomlevel < zoomMin) {
			return false;
		}
		if (zoomlevel == zoom) {
			return false;
		}
		zoom = zoomlevel;
		worldsize();
		tilesFromGeo();
		fireChangeListeners();
		fireZoomListeners();
		return true;
	}

	public void zoomInToPosition(int x, int y, double d)
	{
		this.lon = bounds.fixLon(getPositionLon(x));
		this.lat = bounds.fixLat(getPositionLat(y));
		zoomIn(d);
	}

	public void zoomOutToPosition(int x, int y, double d)
	{
		this.lon = bounds.fixLon(getPositionLon(x));
		this.lat = bounds.fixLat(getPositionLat(y));
		zoomOut(d);
	}

	Set<MapWindowChangeListener> listenersChangeGeneral = new HashSet<>();
	Set<ZoomChangedListener> listenersChangeZoom = new HashSet<>();
	Set<MapWindowWorldScaleListener> listenersChangeWorldScale = new HashSet<>();

	/**
	 * Add the given listener to the set of general change listeners. The
	 * listener will be notified on all events.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addChangeListener(MapWindowChangeListener listener)
	{
		listenersChangeGeneral.add(listener);
	}

	/**
	 * Add the given listener to the set of zoom change listeners. The listener
	 * will be notified on zoom events only.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addZoomListener(ZoomChangedListener listener)
	{
		listenersChangeZoom.add(listener);
	}

	/**
	 * Add the given listener to the set of tile size change listeners. The
	 * listener will be notified on tile size events only.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addWorldScaleListener(MapWindowWorldScaleListener listener)
	{
		listenersChangeWorldScale.add(listener);
	}

	private void fireChangeListeners()
	{
		for (MapWindowChangeListener listener : listenersChangeGeneral) {
			listener.changed();
		}
	}

	private void fireZoomListeners()
	{
		for (ZoomChangedListener listener : listenersChangeZoom) {
			listener.zoomChanged();
		}
	}

	private void fireWorldScaleChangeListeners()
	{
		for (MapWindowWorldScaleListener listener : listenersChangeWorldScale) {
			listener.worldScaleChanged();
		}
	}

	/**
	 * Calculate the current x position of the given longitude.
	 * 
	 * @param ilon
	 *            the longitude.
	 * @return the longitude's position on this window.
	 */
	public double longitudeToX(double ilon)
	{
		double tileX = WGS84.lon2merc(ilon, worldsize) * worldscale;
		return tileX - px;
	}

	/**
	 * Calculate the current y position of the given latitude.
	 * 
	 * @param ilat
	 *            the latitude.
	 * @return the latitude's position on this window.
	 */
	public double latitudeToY(double ilat)
	{
		double tileY = WGS84.lat2merc(ilat, worldsize) * worldscale;
		return tileY - py;
	}

	/**
	 * Calculate the current x position of the given mercator x.
	 * 
	 * @param mx
	 *            the mercator value in the window's zoom level dimension.
	 * @return the longitude's position on this window.
	 */
	public double mercatorToX(double mx)
	{
		if (worldscale == Tile.SIZE) {
			double pos = mx - px;
			return pos;
		} else {
			double ratio = (worldscale / (double) Tile.SIZE);
			double bx = px / ratio;
			double pos = mx - bx;
			return pos * ratio;
		}
	}

	/**
	 * Calculate the current y position of the given mercator y.
	 * 
	 * @param my
	 *            the mercator value in the window's zoom level dimension.
	 * @return the latitude's position on this window.
	 */
	public double mercatorToY(double my)
	{
		if (worldscale == Tile.SIZE) {
			double pos = my - py;
			return pos;
		} else {
			double ratio = (worldscale / (double) Tile.SIZE);
			double by = py / ratio;
			double pos = my - by;
			return pos * ratio;
		}
	}

	/**
	 * Center the map on this position.
	 * 
	 * @param longitude
	 *            the longitude.
	 * @param latitude
	 *            the latitude.
	 */
	public void gotoLonLat(double longitude, double latitude)
	{
		lon = longitude;
		lat = latitude;
		tilesFromGeo();
		fireChangeListeners();
	}

	/**
	 * Move the viewport to show all points.
	 * 
	 * @param points
	 *            the points to show
	 */
	public void gotoPoints(Collection<OverlayPoint> points)
	{
		OverlayPoint mean = OverlayPoint.mean(points);
		gotoLonLat(mean.getLongitude(), mean.getLatitude());
		OverlayPoint minimum = OverlayPoint.minimum(points);
		OverlayPoint maximum = OverlayPoint.maximum(points);
		zoom = zoomMax;
		worldsize();
		tilesFromGeo();
		while (!containsPoint(minimum) || !containsPoint(maximum)) {
			if (!zoomOut(1)) {
				break;
			}
		}
		fireChangeListeners();
	}

	/**
	 * @param lon1
	 *            minimum longitude.
	 * @param lon2
	 *            maximum longitude.
	 * @param lat1
	 *            minimum latitude.
	 * @param lat2
	 *            maximum latitude.
	 */
	public void gotoLonLat(double lon1, double lon2, double lat1, double lat2)
	{
		List<OverlayPoint> points = new ArrayList<>();
		points.add(new OverlayPoint(lon1, lat1));
		points.add(new OverlayPoint(lon2, lat2));
		gotoPoints(points);
	}

	/**
	 * Test whether this point is within this window.
	 * 
	 * @param point
	 *            the point to test for.
	 * @return whether this point is currently within this window.
	 */
	public boolean containsPoint(OverlayPoint point)
	{
		double tileX = WGS84.lon2merc(point.getLongitude(), worldsize);
		double tileY = WGS84.lat2merc(point.getLatitude(), worldsize);
		double minX = px / (double) worldscale;
		double maxX = (px + width) / (double) worldscale;
		double minY = py / (double) worldscale;
		double maxY = (py + height) / (double) worldscale;
		return tileX >= minX && tileX <= maxX && tileY >= minY && tileY <= maxY;
	}

	public double getX(double x)
	{
		return longitudeToX(x);
	}

	public double getY(double y)
	{
		return latitudeToY(y);
	}

	public boolean setWorldScale(int worldScale)
	{
		if (this.worldscale == worldScale) {
			return false;
		}
		this.worldscale = worldScale;
		worldsize();
		tilesFromGeo();
		fireChangeListeners();
		fireWorldScaleChangeListeners();
		return true;
	}

	public int getWorldScale()
	{
		return worldscale;
	}

	public boolean zoomIn()
	{
		return zoomIn(0.5);
	}

	public boolean zoomOut()
	{
		return zoomOut(0.5);
	}

	public boolean zoom(int zoomlevel)
	{
		if (zoomlevel > zoomMax || zoomlevel < zoomMin) {
			return false;
		}
		if (zoomlevel == zoom) {
			return false;
		}
		zoom = zoomlevel;
		worldsize();
		tilesFromGeo();
		fireChangeListeners();
		fireZoomListeners();
		return true;
	}

	public void zoomInToPosition(int x, int y)
	{
		zoomInToPosition(x, y, 0.5);
	}

	public void zoomOutToPosition(int x, int y)
	{
		zoomOutToPosition(x, y, 0.5);
	}

	public double getWorldsizePixels()
	{
		return worldsizePixels;
	}

}
