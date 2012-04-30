package org.uci.luci.interchange;

import java.util.List;
import java.util.ArrayList;

public class FourWayIntersection extends Intersection {
  
  
  String eastNodeId, westNodeId;
  String northNodeId, southNodeId;
  
  
  public FourWayIntersection(String rootNodeId) {
    super(rootNodeId);
    generateGroups();
  }
  
  private void generateGroups() {
    Node rootNode = Global.openStreetMap.getNode(getRootNodeId());
    
    List<Node> connectedNodes = rootNode.connectedNodes;
    
    System.out.println("Angles");
    
    for (int i = 0; i < connectedNodes.size(); i++) {
      for (int ii = 0; ii < connectedNodes.size(); ii++) {
        if (i == ii)
          continue;
        
        Node node1 = connectedNodes.get(i);
        Node node2 = connectedNodes.get(ii);
        
        if (node1.id.equals(eastNodeId) || node1.id.equals(westNodeId))
          continue;
        if (node2.id.equals(eastNodeId) || node2.id.equals(westNodeId))
          continue;
        
        double angle = angleBetween2Lines(Double.valueOf(rootNode.lon), Double.valueOf(rootNode.lat), Double.valueOf(node1.lon), Double.valueOf(node1.lat),
                                          Double.valueOf(rootNode.lon), Double.valueOf(rootNode.lat), Double.valueOf(node2.lon), Double.valueOf(node2.lat));
        
        System.out.println("\tangle = " + Math.toDegrees(angle));
        
        if (angle != 180) {
          continue;
        }
        
        if (eastNodeId == null || westNodeId == null) {
          eastNodeId = node1.id;
          westNodeId = node2.id;
        }
        else {
          northNodeId = node1.id;
          southNodeId = node2.id;
        }
      }
    }
  }
  
  // public static double angleBetween2Lines(Line2D line1, Line2D line2) {
  public static double angleBetween2Lines(double l1x1, double l1y1, double l1x2, double l1y2,
                                          double l2x1, double l2y1, double l2x2, double l2y2) {
    double angle1 = Math.atan2(l1y1 - l1y2,
                               l1x1 - l1x2);
    double angle2 = Math.atan2(l2y1 - l2y2,
                               l2x1 - l2x2);
    return angle1-angle2;
  }
  
  // // 0 = green, 1 = yellow, 2 = red
  // 0 = green, 1 = yellow, 2 = red
  public int getLightForWayOnLane(Way w, int lane) {
    // System.out.println("w " + w + " lane = " + lane);
    return 2;
  }
  
  public void tick() {
    //  |
    // - -
    //  |
  }
  
  public void vehicleIsApproaching(Vehicle v) {
    // info about vehicle
    System.out.println("v " + v.vin + " is approaching " + id);
    System.out.println("\tvin = " + v.vin);
    System.out.println("\torigin node = " + v.getOriginNode().id);
    System.out.println("\tdestination node = " + v.getDestinationNode().id);
  }
  
  public void vehicleIsLeaving(Vehicle v) {
    System.out.println("v " + v.vin + " is leaving " + id);
  }
}
