package org.uci.luci.interchange.Registry;

import org.uci.luci.interchange.Intersections.*;
import org.uci.luci.interchange.Factory.*;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Exception.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class IntersectionRegistry {
	private static Hashtable<String, Intersection> intersectionHash = new Hashtable<String, Intersection>();

	public static void registerIntersection(Intersection i) {
		String id = "i-" + i.getRootNodeId();
		i.id = id;
		intersectionHash.put(id, i);
	}

	public static Intersection getIntersection(String id) {
		return intersectionHash.get(id);
	}

	public static Intersection getIntersectionAtNode(Node node) {
		return intersectionHash.get("i-" + node.id);
	}

	public static List<Intersection> allRegisteredIntersections() {
		return new ArrayList<Intersection>(intersectionHash.values());
	}

	public static void generateTraditionalIntersections()
			throws UnknownIntersectionTypeException {
		List<Node> nodes = Global.openStreetMap.nodes();

		for (Node n : nodes) {
			if (n.connectedNodes.size() > 2) {
				if (n.isHighwayNode()) {
					IntersectionFactory.createHighwayRampForNode(n);
					continue;
				}

				if (n.connectedNodes.size() == 3) {
					IntersectionFactory.createThreeWayIntersectionForNode(n);
				} else if (n.connectedNodes.size() == 4) {
					IntersectionFactory.createFourWayIntersectionForNode(n);
				} else if (n.connectedNodes.size() == 5) {
					IntersectionFactory.createFiveWayIntersectionForNode(n);
				} else {
					System.out.println("Node " + n.id + " (" + n.lat + ", "
							+ n.lon + ") has " + n.connectedNodes.size()
							+ " connections!");
					throw new UnknownIntersectionTypeException();
				}
			}
		}
	}

	public static void generateBiddingIntersections()
			throws UnknownIntersectionTypeException {
		List<Node> nodes = Global.openStreetMap.nodes();

		for (Node n : nodes) {
			if (n.connectedNodes.size() > 2) {

				if (n.isHighwayNode()) {
					IntersectionFactory.createHighwayRampForNode(n);
					continue;
				}

				if (n.connectedNodes.size() == 3) {
					IntersectionFactory
							.createThreeWayBiddingIntersectionForNode(n);
				} else if (n.connectedNodes.size() == 4) {
					IntersectionFactory
							.createFourWayBiddingIntersectionForNode(n);
				} else {
					System.out.println("Node " + n.id + " (" + n.lat + ", "
							+ n.lon + ") has " + n.connectedNodes.size()
							+ " connections!");
					throw new UnknownIntersectionTypeException();
				}
			}
		}
	}

	public static void generateLoopSensorsIntersections()
			throws UnknownIntersectionTypeException {
		List<Node> nodes = Global.openStreetMap.nodes();

		for (Node n : nodes) {
			if (n.connectedNodes.size() > 2) {

				if (n.isHighwayNode()) {
					IntersectionFactory.createHighwayRampForNode(n);
					continue;
				}

				if (n.connectedNodes.size() == 3) {
					IntersectionFactory
							.createThreeWayLoopSensorsIntersectionForNode(n);
				} else if (n.connectedNodes.size() == 4) {
					IntersectionFactory
							.createFourWayLoopSensorsIntersectionForNode(n);
				} else {
					System.out.println("Node " + n.id + " (" + n.lat + ", "
							+ n.lon + ") has " + n.connectedNodes.size()
							+ " connections!");
					throw new UnknownIntersectionTypeException();
				}
			}
		}
	}
}