package org.uci.luci.interchange.UI;

public class WindowProjector {
	double top = -1;
	double bottom = -1;
	double left = -1;
	double right = -1;
	int scale = 500;
	int offsetX = -1;
	int offsetY = -1;
	double width = -1;
	double height = -1;

	public WindowProjector(double top, double bottom, double left, double right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.width = Math.abs(right - left);
		this.height = Math.abs(bottom - top);
	}

	public NodePoint scaledXY(double x, double y) {
		// y = bottom-y;

		x = ((x - left) / width);
		y = ((y - top) / height);

		x = (x * scale) + offsetX;
		y = (y * scale) - offsetY;

		y = scale - y;

		return new NodePoint(x, y);
	}

	public NodePoint unscaleXY(int x, int y) {
		return unscaleXY((double) x, (double) y);
	}

	public NodePoint unscaleXY(double x, double y) {
		x = (x - offsetX) / scale;
		y = (-(y - scale) + offsetY) / scale;

		x = (x * width) + left;
		y = (y * height) + top;

		// y = -(y - bottom);

		return new NodePoint(x, y);

		// return new NodePoint(0,0);
		// double pX = (x - offsetX) / (double) scale;
		// double pY = (y - offsetY) / (double) scale;
		//
		// double lat = (pY * (bottom - top)) + top;
		// double lon = (pX * (right - left)) + left;
		//
		// // lat = lat + bottom;
		//
		// return new NodePoint((double) lon, (double) lat);
	}
}
