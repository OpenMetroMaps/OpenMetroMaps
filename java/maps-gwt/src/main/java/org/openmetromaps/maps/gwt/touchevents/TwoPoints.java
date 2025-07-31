package org.openmetromaps.maps.gwt.touchevents;

public class TwoPoints
{

	private final Point p1;
	private final Point p2;

	public TwoPoints(Point p1, Point p2)
	{
		this.p1 = p1;
		this.p2 = p2;
	}

	public Point getMidpoint()
	{
		float x = (p1.x + p2.x) / 2;
		float y = (p1.y + p2.y) / 2;
		return new Point(x, y);
	}

	public float distance()
	{
		return new Vector2(p1, p2).length();
	}

}
