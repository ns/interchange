package org.uci.luci.interchange.Factory;

import org.uci.luci.interchange.Intersections.*;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Registry.*;

import java.util.Random;
import java.util.List;

public class IntersectionFactory {
	public static FourWayIntersection createFourWayIntersectionForNode(Node n) {
		FourWayIntersection intersection = new FourWayIntersection(n.id);
		IntersectionRegistry.registerIntersection(intersection);
		return intersection;
	}

	public static ThreeWayIntersection createThreeWayIntersectionForNode(Node n) {
		ThreeWayIntersection intersection = new ThreeWayIntersection(n.id);
		IntersectionRegistry.registerIntersection(intersection);
		return intersection;
	}

	public static FourWayBiddingIntersection createFourWayBiddingIntersectionForNode(
			Node n) {
		FourWayBiddingIntersection intersection = new FourWayBiddingIntersection(
				n.id);
		IntersectionRegistry.registerIntersection(intersection);
		return intersection;
	}

	public static ThreeWayBiddingIntersection createThreeWayBiddingIntersectionForNode(
			Node n) {
		ThreeWayBiddingIntersection intersection = new ThreeWayBiddingIntersection(
				n.id);
		IntersectionRegistry.registerIntersection(intersection);
		return intersection;
	}

	public static HighwayRamp createHighwayRampForNode(Node n) {
		HighwayRamp intersection = new HighwayRamp(n.id);
		IntersectionRegistry.registerIntersection(intersection);
		return intersection;
	}
}