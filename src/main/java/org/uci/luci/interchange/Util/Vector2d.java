package org.uci.luci.interchange.Util;

public class Vector2d {
	public double x, y;

	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double mag() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
}