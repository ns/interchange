package org.uci.luci.interchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntersectionRegistry {
  private static HashMap<String, Intersection> intersectionHash = new HashMap<String, Intersection>();
  
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
	
	public static void generateIntersections() {
    List<Node> nodes = Global.openStreetMap.nodes();
    
    for (Node n : nodes) {
      if (n.connectedNodes.size() > 2) {
        if (n.connectedNodes.size() == 3) {
          IntersectionFactory.createThreeWayIntersectionForNode(n);
        }
        else if (n.connectedNodes.size() == 4) {
          IntersectionFactory.createFourWayIntersectionForNode(n);
        }
        else {
          System.out.println("Node "+ n.id + " has " + n.connectedNodes.size() + " connections!");
        }
      }
    }
  }
}