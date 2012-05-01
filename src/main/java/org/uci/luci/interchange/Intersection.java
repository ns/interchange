package org.uci.luci.interchange;

// import java.util.List;

public abstract class Intersection {
  public String id;
  private String rootNodeId;
  private double bounds = 20;
  
  public Intersection(String nodeId) {
    rootNodeId = nodeId;
  }
  
  public abstract void tick();
  
  // called when a vehicle can 'see' an intersection
  public abstract void vehicleIsApproaching(Vehicle v);
  
  // called when a vehicle passes an intersection
  public abstract void vehicleIsLeaving(Vehicle v);
  
  public String getRootNodeId() {
    return rootNodeId;
  }
  
  public double getBounds() {
    return bounds;
  }
  
  // 0 = green, 1 = yellow, 2 = red
  public int getLightForWayOnLane(Way w, String originNodeId, int lane) {
    // System.out.println("w " + w + " lane = " + lane);
    return 0;
  }
  
  // public List<Node> connectingNodes() {
  //   Node root = Global.openStreetMap.getNode(rootNodeId);
  //   return root.connectedNodes;
  // }
}
