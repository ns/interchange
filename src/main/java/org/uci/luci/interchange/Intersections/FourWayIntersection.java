package org.uci.luci.interchange;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FourWayIntersection extends Intersection {
  String eastNodeId, westNodeId;
  String northNodeId, southNodeId;
  
  boolean ewGreen = false;
  boolean nsGreen = false;
  
  int switchInterval;
  
  public FourWayIntersection(String rootNodeId) {
    super(rootNodeId);
    Random randomGenerator = Utils.randomNumberGenerator();
    switchInterval = randomGenerator.nextInt(1000)+500;
    generateGroups();
  }
  
  private void generateGroups() {
    Node rootNode = Global.openStreetMap.getNode(getRootNodeId());
    List<Node> connectedNodes = (List<Node>)rootNode.connectedNodes.clone();
    List<Node> g1 = Utils.findNodesWithAngleBetweenClosestTo180(rootNode, connectedNodes);
    northNodeId = g1.get(0).id;
    southNodeId = g1.get(1).id;
    connectedNodes.remove(g1.get(0));
    connectedNodes.remove(g1.get(1));
    eastNodeId = connectedNodes.get(0).id;
    westNodeId = connectedNodes.get(1).id;
  }
  
  // 0 = green, 1 = yellow, 2 = red
  // public int getLightForWayOnLane(Way w, int lane) {
  public int getLightForWayOnLane(Way w, String originNodeId, int lane) {
    if (originNodeId.equals(eastNodeId) || originNodeId.equals(westNodeId)) {
      return ewGreen ? 0 : 2;
    }
    else if (originNodeId.equals(northNodeId) || originNodeId.equals(southNodeId)) {
      return nsGreen ? 0 : 2;
    }
    else {
      System.out.println("no equals");
    }
    
    return 2;
  }
  
  public void tick(int tick) {
    if (tick % switchInterval == 0) {
      if (nsGreen) {
        nsGreen = false;
        ewGreen = true;
      }
      else {
        nsGreen = true;
        ewGreen = false;
      }
    }
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
  
  public boolean isLeftTurn(String fromNodeId, String toNodeId) {
    Node rootNode = Global.openStreetMap.getNode(getRootNodeId());
    Node node1 = Global.openStreetMap.getNode(fromNodeId);
    Node node2 = Global.openStreetMap.getNode(toNodeId);
    double angle = Utils.angleBetweenNodesWithCenterNode(rootNode, node1, node2);
    if (Math.toDegrees(angle) < -45 && Math.toDegrees(angle) > -135)
      return true;
    return false;
  }
  
  public boolean isRightTurn(String fromNodeId, String toNodeId) {
    Node rootNode = Global.openStreetMap.getNode(getRootNodeId());
    Node node1 = Global.openStreetMap.getNode(fromNodeId);
    Node node2 = Global.openStreetMap.getNode(toNodeId);
    double angle = Utils.angleBetweenNodesWithCenterNode(rootNode, node1, node2);
    if (Math.toDegrees(angle) > 45 && Math.toDegrees(angle) < 135)
      return true;
    return false;
  }
}
