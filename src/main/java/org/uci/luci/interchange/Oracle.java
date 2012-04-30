package org.uci.luci.interchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Oracle {
  private static HashMap<String, ArrayList<Integer>> originNodesToVehicles = new HashMap<String, ArrayList<Integer>>();
  
  public static void deregisterVehicleOrigin(Integer vin, String nodeId) {
    // System.out.println("deregistering vehicle " + vin + " at " + nodeId);
    if (originNodesToVehicles.get(nodeId) == null)
      originNodesToVehicles.put(nodeId, new ArrayList<Integer>());
    
    ArrayList<Integer> vehicles = originNodesToVehicles.get(nodeId);
    vehicles.remove(vin);
  }
  
  public static void registerVehicleOrigin(Integer vin, String nodeId) {
    // System.out.println("registering vehicle " + vin + " at " + nodeId);
    
    if (originNodesToVehicles.get(nodeId) == null)
      originNodesToVehicles.put(nodeId, new ArrayList<Integer>());
    
    ArrayList<Integer> vehicles = originNodesToVehicles.get(nodeId);
    vehicles.add(vin);
  }
  
  public static List<Integer> vehiclesWithNodeAsOrigin(String nodeId) {
    return originNodesToVehicles.get(nodeId);
  }
}
