package org.uci.luci.interchange.Intersections;

import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Vehicles.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ThreeWayBiddingIntersection extends Intersection {
  String eastNodeId, westNodeId;
  String northNodeId;
  
  boolean ewGreen = false;
  boolean nsGreen = false;
  
  HashMap<String, Integer> ewBids, nsBids;
  
  public ThreeWayBiddingIntersection(String rootNodeId) {
    super(rootNodeId);
    generateGroups();
    ewBids = new HashMap<String, Integer>();
    nsBids = new HashMap<String, Integer>();
  }
  
  private void generateGroups() {
    Node rootNode = Global.openStreetMap.getNode(getRootNodeId());
    List<Node> connectedNodes = (List<Node>)rootNode.connectedNodes.clone();
    List<Node> g1 = Utils.findNodesWithAngleBetweenClosestTo180(rootNode, connectedNodes);
    eastNodeId = g1.get(0).id;
    westNodeId = g1.get(1).id;
    connectedNodes.remove(g1.get(0));
    connectedNodes.remove(g1.get(1));
    northNodeId = connectedNodes.get(0).id;
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
  
  public void tick(double simTime, double tickLength, int tick) {
    if (nsBidTotal() > ewBidTotal()) {
      ewGreen = false;
      nsGreen = true;
    }
    else {
      ewGreen = true;
      nsGreen = false;
    }
  }
  
  public int nsBidTotal() {
    return nsBids.size();
    // int total = 0;
    // for (Map.Entry<String, Integer> entry : nsBids.entrySet()) {
    //   Integer bid = entry.getValue();
    //   total += bid;
    // }
    // return total;
  }
  
  public int ewBidTotal() {
    return ewBids.size();
    // int total = 0;
    // for (Map.Entry<String, Integer> entry : ewBids.entrySet()) {
    //   Integer bid = entry.getValue();
    //   total += bid;
    // }
    // return total;
  }
  
  public void vehicleIsApproaching(Vehicle v) {
    // just count it as a +1 bid for the moment
    if (v.getOriginNode().id.equals(eastNodeId) || v.getOriginNode().id.equals(westNodeId)) {
      ewBids.put(v.vin+"", 1);
    }
    else if (v.getOriginNode().id.equals(northNodeId)) {
      nsBids.put(v.vin+"", 1);
    }
  }
  
  public void vehicleIsLeaving(Vehicle v) {
    // remove the vehicles bid
    nsBids.remove(v.vin+"");
    ewBids.remove(v.vin+"");
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
