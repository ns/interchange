package org.uci.luci.interchange;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
	public static double radians(double n) {
		return n * Math.PI / 180;
	}

	public static List<Node> findNodesWithAngleBetweenClosestTo180(Node centerNode, List<Node> nodes) {
		Node selectedNode1 = null, selectedNode2 = null;
		double angleBetweenSelectedNodes = Double.MAX_VALUE;

		for (int i = 0; i < nodes.size(); i++) {
			for (int ii = 0; ii < nodes.size(); ii++) {
				if (i == ii)
					continue;

				Node node1 = nodes.get(i);
				Node node2 = nodes.get(ii);

				double angle = Utils.angleBetween2Lines(centerNode.lon, centerNode.lat, node1.lon, node1.lat,
						centerNode.lon, centerNode.lat, node2.lon, node2.lat);

				// System.out.println("\tangle = " + Math.toDegrees(angle));

				if (angleBetweenSelectedNodes == Double.MAX_VALUE || Math.abs(180-Math.toDegrees(angleBetweenSelectedNodes))>Math.abs(180-Math.toDegrees(angle))) {
					selectedNode1 = node1;
					selectedNode2 = node2;
					angleBetweenSelectedNodes = angle;
					continue;
				}
			}
		}

		// System.out.println("picked angle = ");

		return Arrays.asList(selectedNode1, selectedNode2);
	}

	public static double angleBetween2Lines(double l1x1, double l1y1, double l1x2, double l1y2,
			double l2x1, double l2y1, double l2x2, double l2y2) {
		double angle1 = Math.atan2(l1y1 - l1y2,
				l1x1 - l1x2);
		double angle2 = Math.atan2(l2y1 - l2y2,
				l2x1 - l2x2);
		return angle1-angle2;
	}


	//Returns Distance between two lat/long coords
	// - lat1
	// - lng1
	// - lat2
	// - lng2
	// - conv - Unit Conversion
	// 		  - 1 = Miles
	// 		  - 2 = Meters
	// 		  - 3 = Kilometers

	public static float distFrom(double lat1, double lng1, double lat2, double lng2, int conv) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *	Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		if(conv == 2)
			dist *= 1609;
		else if(conv == 3)
			dist *= 1.609;
		return new Float(dist).floatValue();
	}
}