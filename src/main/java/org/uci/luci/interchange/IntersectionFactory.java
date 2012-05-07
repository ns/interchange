package org.uci.luci.interchange;

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
 	
 	public static FourWayBiddingIntersection createFourWayBiddingIntersectionForNode(Node n) {
    FourWayBiddingIntersection intersection = new FourWayBiddingIntersection(n.id);
    IntersectionRegistry.registerIntersection(intersection);
    return intersection;
 	}
 	
 	public static ThreeWayBiddingIntersection createThreeWayBiddingIntersectionForNode(Node n) {
    ThreeWayBiddingIntersection intersection = new ThreeWayBiddingIntersection(n.id);
    IntersectionRegistry.registerIntersection(intersection);
    return intersection;
 	}
}