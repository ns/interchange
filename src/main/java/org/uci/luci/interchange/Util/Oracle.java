package org.uci.luci.interchange.Util;

import org.uci.luci.interchange.Graph.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Oracle {
	private static HashMap<String, ArrayList<String>> originNodesToVehicles = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, Way> twoNodesToWay = new HashMap<String, Way>();
	private static HashMap<String, Double> cachedDistances = new HashMap<String, Double>();

	public static void resetVehicles() {
		originNodesToVehicles.clear();
	}

	public static void deregisterVehicleOrigin(Integer vin, String nodeId) {
		if (originNodesToVehicles.get(nodeId) == null)
			originNodesToVehicles.put(nodeId, new ArrayList<String>());
		ArrayList<String> vehicles = originNodesToVehicles.get(nodeId);
		vehicles.remove(vin + "");
	}

	public static void registerVehicleOrigin(Integer vin, String nodeId) {
		if (originNodesToVehicles.get(nodeId) == null)
			originNodesToVehicles.put(nodeId, new ArrayList<String>());

		ArrayList<String> vehicles = originNodesToVehicles.get(nodeId);
		vehicles.add(vin + "");
	}

	public static List<String> vehiclesWithNodeAsOrigin(String nodeId) {
		return originNodesToVehicles.get(nodeId);
	}

	public static void registerWay(String fromNodeId, String toNodeId, Way way) {
		twoNodesToWay.put(fromNodeId + "-" + toNodeId, way);
	}

	public static Way wayBetweenNodes(String node1, String node2) {
		Way w = twoNodesToWay.get(node1 + "-" + node2);
		if (w == null)
			w = twoNodesToWay.get(node2 + "-" + node1);
		return w;
	}
	
	public static double getDistanceBetweenNodes(String node1Id, String node2Id) {
	  String key = node1Id + "-" + node2Id;
	  
	  if (node1Id.charAt(0) < node2Id.charAt(0))
	    key = node2Id + "-" + node1Id;
	  
	  if (cachedDistances.containsKey(key)) {
	    return cachedDistances.get(key);
	  }
	  else {
  		Node node1 = Global.openStreetMap.getNode(node1Id);
  		Node node2 = Global.openStreetMap.getNode(node2Id);
	    double d = Utils.distance(node1.lat,node1.lon,node2.lat,node2.lon,'K');
	    cachedDistances.put(key, d);
	    return d;
	  }
	}
}
