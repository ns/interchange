package org.uci.luci.interchange.Util;

import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Exception.NoPathToDestinationException;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Registry.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.Random;


public class Oracle {
	private static HashMap<String, ArrayList<String>> originNodesToVehicles = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, Way> twoNodesToWay = new HashMap<String, Way>();
	private static HashMap<String, Double> cachedDistances = new HashMap<String, Double>();
	private static HashMap<String, ArrayList<String>> cachedRoutes = new HashMap<String, ArrayList<String>>();

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
	
  public static boolean hasRoomForCarAtNode(Node n) {
    List<String> vehicles = vehiclesWithNodeAsOrigin(n.id);
    if (vehicles == null)
      return true;
    for (String vin : vehicles) {
      Vehicle v = VehicleRegistry.getVehicle(vin);
      if (Utils.distance(n.lat, n.lon, v.lat, v.lon, 'K') < Vehicle.DISTANCE_TO_CONSIDER_AS_SAME)
        return false;
    }
	  return true;
	}
	
	public static List<String> vehiclesWithNodeAsOrigin(String nodeId) {
	  if (originNodesToVehicles.containsKey(nodeId))
  		return (List<String>)originNodesToVehicles.get(nodeId).clone();
		else
  		return null;
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
	
	public static void generateRoutes(int total) {
	  cachedRoutes.clear();
	  
		List<Node> nodes = Global.openStreetMap.nodes();
		Random generator = Utils.randomNumberGenerator();
				
	  while (cachedRoutes.size() != total) {
	    Node startNode = null;
	    Node endNode = null;
	    try {
    		startNode = nodes.get(generator.nextInt(nodes.size()));
    		endNode = nodes.get(generator.nextInt(nodes.size()));
    		if (startNode == endNode)
    		  continue;
  	    LinkedList<Node> path = generatePathBetweenNodes(startNode, endNode);
  	    ArrayList<String> cachedPath = new ArrayList<String>();
  	    for (Node n : path) {cachedPath.add(n.id);}
  	    cachedRoutes.put(startNode.id+"-"+endNode.id, cachedPath);
  	    System.out.println("generated route " + cachedRoutes.size());
	    }
	    catch (NoPathToDestinationException e) {
  	    System.out.println("no dest..");
  	    return;
	    }
	  }
	}
	
	public static List<String> routeFromTo(Node from, Node to) {
	  if (cachedRoutes.containsKey(from.id+"-"+to.id)) {
	    return cachedRoutes.get(from.id+"-"+to.id);
	  }
	  else {
	    System.out.println("FATAL");
	    return null;
	  }
	}
	
	public static List<String> randomRoute() {
	  Random generator = Utils.randomNumberGenerator();
	  ArrayList<ArrayList<String>> allRoutes = new ArrayList<ArrayList<String>>(cachedRoutes.values());
	  List<String> route = allRoutes.get(generator.nextInt(allRoutes.size()));
	  return route;
    // System.out.println("route = " + route);
    // return new String[] {route.get(0), route.get(route.size()-1)};
	}
	
	private static LinkedList<Node> generatePathBetweenNodes(Node startNode, Node endNode) throws NoPathToDestinationException {
		LinkedList<Node> aStarResult = (LinkedList<Node>)Global.openStreetMap.AStar2.findPath(startNode, endNode);

		if (aStarResult == null) {
			throw new NoPathToDestinationException();
		} else {
			aStarResult.addFirst(startNode);
			return aStarResult;
		}
	}
}
