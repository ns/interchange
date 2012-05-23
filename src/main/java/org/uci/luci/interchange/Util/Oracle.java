package org.uci.luci.interchange.Util;

import org.uci.luci.interchange.Graph.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Oracle {
  private static HashMap<String, ArrayList<String>> originNodesToVehicles = new HashMap<String, ArrayList<String>>();
  private static HashMap<String, Way> twoNodesToWay = new HashMap<String, Way>();
  
  public static void resetVehicles()
  {
	  originNodesToVehicles.clear();
  }
  
  // public static void deregisterVehicleEverywhere(Integer vin) {
  // }
  
  public static void deregisterVehicleOrigin(Integer vin, String nodeId) {
    if (originNodesToVehicles.get(nodeId) == null)
      originNodesToVehicles.put(nodeId, new ArrayList<String>());
    ArrayList<String> vehicles = originNodesToVehicles.get(nodeId);
    vehicles.remove(vin+"");
  }
  
  public static void registerVehicleOrigin(Integer vin, String nodeId) {
    if (originNodesToVehicles.get(nodeId) == null)
      originNodesToVehicles.put(nodeId, new ArrayList<String>());
    
    ArrayList<String> vehicles = originNodesToVehicles.get(nodeId);
    vehicles.add(vin+"");
  }
  
  public static List<String> vehiclesWithNodeAsOrigin(String nodeId) {
    return originNodesToVehicles.get(nodeId);
  }
  
  public static void registerWay(String fromNodeId, String toNodeId, Way way) {
    twoNodesToWay.put(fromNodeId+"-"+toNodeId, way);
  }
  
  public static Way wayBetweenNodes(String node1, String node2) {
    Way w = twoNodesToWay.get(node1+"-"+node2);
    if (w == null)
      w = twoNodesToWay.get(node2+"-"+node1);
    return w;
  }
}
