package org.uci.luci.interchange.UI;

public class NodePoint {
	double x;
	double y;
	double xc; // constrained on Canvas
	double yc; // constrained on Canvas

	NodePoint(double _x, double _y) {
		x = _x;
		y = _y;
	}

	NodePoint sub(double _x, double _y) {
		return new NodePoint(x - _x, y - _y);
	}

	NodePoint sub(NodePoint n) {
		return new NodePoint(x - n.x, y - n.y);
	}
}