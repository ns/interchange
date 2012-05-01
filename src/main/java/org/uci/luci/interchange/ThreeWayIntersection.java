package org.uci.luci.interchange;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class ThreeWayIntersection extends Intersection {
  String eastNodeId, westNodeId;
  String northNodeId;
  
  boolean ewGreen = false;
  boolean nsGreen = false;
  
  int k = 0;
  
  public ThreeWayIntersection(String rootNodeId) {
    super(rootNodeId);
    generateGroups();
  }
  
  private List<Node> findNodesWithAngleBetweenClosestTo180(Node centerNode, List<Node> nodes) {
    Node selectedNode1 = null, selectedNode2 = null;
    double angleBetweenSelectedNodes = Double.MAX_VALUE;
    
    for (int i = 0; i < nodes.size(); i++) {
      for (int ii = 0; ii < nodes.size(); ii++) {
        if (i == ii)
          continue;
        
        Node node1 = nodes.get(i);
        Node node2 = nodes.get(ii);
        
        double angle = angleBetween2Lines(Double.valueOf(centerNode.lon), Double.valueOf(centerNode.lat), Double.valueOf(node1.lon), Double.valueOf(node1.lat),
                                          Double.valueOf(centerNode.lon), Double.valueOf(centerNode.lat), Double.valueOf(node2.lon), Double.valueOf(node2.lat));
        
        System.out.println("\tangle = " + Math.toDegrees(angle));
        
        if (angleBetweenSelectedNodes == Double.MAX_VALUE || Math.abs(180-Math.toDegrees(angleBetweenSelectedNodes))>Math.abs(180-Math.toDegrees(angle))) {
          selectedNode1 = node1;
          selectedNode2 = node2;
          angleBetweenSelectedNodes = angle;
          continue;
        }
      }
    }
    
    System.out.println("picked angle = ");
    
    return Arrays.asList(selectedNode1, selectedNode2);
  }
  
  private void generateGroups() {
    Node rootNode = Global.openStreetMap.getNode(getRootNodeId());
    
    List<Node> connectedNodes = (List<Node>)rootNode.connectedNodes.clone();
    
    List<Node> g1 = findNodesWithAngleBetweenClosestTo180(rootNode, connectedNodes);
    eastNodeId = g1.get(0).id;
    westNodeId = g1.get(1).id;
    
    connectedNodes.remove(g1.get(0));
    connectedNodes.remove(g1.get(1));
    
    northNodeId = connectedNodes.get(0).id;
  }
  
  public static double angleBetween2Lines(double l1x1, double l1y1, double l1x2, double l1y2,
                                          double l2x1, double l2y1, double l2x2, double l2y2) {
    double angle1 = Math.atan2(l1y1 - l1y2,
                               l1x1 - l1x2);
    double angle2 = Math.atan2(l2y1 - l2y2,
                               l2x1 - l2x2);
    return angle1-angle2;
  }
  
  // 0 = green, 1 = yellow, 2 = red
  // public int getLightForWayOnLane(Way w, int lane) {
  public int getLightForWayOnLane(Way w, String originNodeId, int lane) {
    if (originNodeId.equals(eastNodeId) || originNodeId.equals(westNodeId)) {
      return ewGreen ? 0 : 2;
    }
    else if (originNodeId.equals(northNodeId)) {
      return nsGreen ? 0 : 2;
    }
    else {
      System.out.println("no equals");
    }
    
    return 2;
  }
  
  public void tick() {
    //  |
    // - -
    //  |
    
    if (k % 2000 == 0) {
      if (nsGreen) {
        nsGreen = false;
        ewGreen = true;
      }
      else {
        nsGreen = true;
        ewGreen = false;
      }
    }
    
    k++;
  }
  
  public void vehicleIsApproaching(Vehicle v) {
    // info about vehicle
    // System.out.println("v " + v.vin + " is approaching " + id);
    // System.out.println("\tvin = " + v.vin);
    // System.out.println("\torigin node = " + v.getOriginNode().id);
    // System.out.println("\tdestination node = " + v.getDestinationNode().id);
  }
  
  public void vehicleIsLeaving(Vehicle v) {
    // System.out.println("v " + v.vin + " is leaving " + id);
  }
}
