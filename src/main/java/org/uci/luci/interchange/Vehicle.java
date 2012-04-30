package org.uci.luci.interchange;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.List;

public class Vehicle {
  int vin;
  
  double lat, lon;
  Vector2d velocity;
  private String originNodeId, destinationNodeId;
  
  String state = "";
  
  private void setOriginNodeId(String nodeId) {
    if (originNodeId != null)
      Oracle.deregisterVehicleOrigin(vin, originNodeId);
    originNodeId = nodeId;
    Oracle.registerVehicleOrigin(vin, originNodeId);
  }
  
  public Vehicle(float lat, float lon, String anOriginNodeId, String aDestinationNodeId) {
    VehicleRegistry.registerVehicle(this);
    
    this.lat = lat;
    this.lon = lon;
    setOriginNodeId(anOriginNodeId);
    destinationNodeId = aDestinationNodeId;
  }
  
  public boolean isAtDestinationNode() {
    return distanceToDestinationNode() < 0.0000005;
  }
  
  public double distanceToDestinationNode() {
    Node nextNode = getDestinationNode();
    double d = Math.sqrt(Math.pow(lat - Double.valueOf(nextNode.lat),2)+Math.pow(lon - Double.valueOf(nextNode.lon),2));
    return d;
  }
  public double distanceFromOriginNode() {
    Node lastNode = getOriginNode();
    double d = Math.sqrt(Math.pow(lat - Double.valueOf(lastNode.lat),2)+Math.pow(lon - Double.valueOf(lastNode.lon),2));
    return d;
  }
  
  public Node getOriginNode() {
    return Global.openStreetMap.getNode(originNodeId);
  }
  
  public Node getDestinationNode() {
    return Global.openStreetMap.getNode(destinationNodeId);
  }
  
  // we're lying, the vehicle does some magic internally
  // to handle going from node to node. this is only
  // for node-to-node movements that aren't actual
  // transporation infrastructure (intersections, stop signs, etc)
  // for those situations the vehicle does nothing. the driver
  // must figure out where he wants to take the car
  // *this should only be called by the driver*
  public void tick() {
    Node lastNode = getOriginNode();
    Node nextNode = getDestinationNode();
    
    if (isAtDestinationNode()) {
      if (nextNode.connectedNodes.size() == 2) {
        // pretty obvious where the car goes..
        int i = nextNode.connectedNodes.indexOf(lastNode);
        setOriginNodeId(nextNode.id);
        if (i == 0) {
          destinationNodeId = nextNode.connectedNodes.get(1).id;
        }
        else {
          destinationNodeId = nextNode.connectedNodes.get(0).id;
        }
        state = "";
      }
      else if (nextNode.connectedNodes.size() == 1) {
        // we make the car go back down the path it came from
        // this happens when the car hits a dead end,
        // potentially remove this and make the driver handle it
        String _originNodeId = originNodeId;
        setOriginNodeId(destinationNodeId);
        destinationNodeId = _originNodeId;
        state = "";
      }
      else {
        // // make the driver handle this, it's an intersection
        // // we assume that this is an intersection
        // // pick a random way to go.
        int i = nextNode.connectedNodes.indexOf(lastNode);
        // originNodeId = nextNode.id;
        setOriginNodeId(nextNode.id);
        destinationNodeId = randomConnectedNode(nextNode, lastNode).id;
        // state = "reached_intersection";
      }
    }
  }
  
  private Node randomConnectedNode(Node n, Node excludeNode) {
    Random randomGenerator = new Random();
    
    int nodeIndex = -1;
    int excludeNodeIndex = n.connectedNodes.indexOf(excludeNode);
    
    while (nodeIndex == -1 || nodeIndex == excludeNodeIndex) {
      nodeIndex = randomGenerator.nextInt(n.connectedNodes.size());
    }
    
    return n.connectedNodes.get(nodeIndex);
  }
  
  // the vehicle doesn't actuate anything,
  // it simply knows the state of the vehicle
  // in the simulator. also, it provides a convenience
  // methods to calculate the car in front and behind this vehicle.
  
  // double speed = 0.000001;
  double minSpeed = 0;
  double maxSpeed = 0.000002;
  int direction = 0; // 0 or 1
  int lane = 0;
  
  Vehicle vehicleBehind, vehicleInFront;
  private void calculateVehicleBehind() {
    Vehicle vehicle = null;
    
    for (Vehicle v : VehicleRegistry.allRegisteredVehicles()) {
      // Vehicle v = entry.getValue();
      
      if (this == v)
        continue;
      
      if (v.originNodeId.equals(originNodeId) && v.destinationNodeId.equals(destinationNodeId)) {
        // at least on the same way, the way we can check if this vehicle is
        // in behind us is by checking how far both vehicles are from their
        // destinationNode
        if (distanceToDestinationNode() < v.distanceToDestinationNode()) {
          vehicle = v;
        }
      }
    }
    
    vehicleBehind = vehicle;
  }
  
  public Intersection getNextIntersection() {
    Node destNode = Global.openStreetMap.getNode(destinationNodeId);
    // this is an intersection
    if (destNode.connectedNodes.size() > 2) {
      return IntersectionRegistry.getIntersectionAtNode(destNode);
    }
    // this is a simple node
    else if (destNode.connectedNodes.size() == 2) {
      return null;
    }
    // this is the end of the road
    else if (destNode.connectedNodes.size() == 1) {
      return null;
    }
    System.out.println("this also shouldn't happen...");
    return null;
  }
  
  public double getDistanceToNextIntersection() {
    Node destNode = Global.openStreetMap.getNode(destinationNodeId);
    // this is an intersection
    if (destNode.connectedNodes.size() > 2) {
      return distanceToDestinationNode();
    }
    // this is a simple node
    else if (destNode.connectedNodes.size() == 2) {
      return Integer.MAX_VALUE;
    }
    // this is the end of the road
    else if (destNode.connectedNodes.size() == 1) {
      return -1;
    }
    // what??
    // else if (destNode.connectedNodes.size() == 0) {
      System.out.println("this also shouldn't happen");
    //   return -1;
    // }
    return -1;
  }
  
  public double getDistanceToVehicleInFront() {
    calculateVehicleInFront();
    if (vehicleInFront == null) {
      return -1;
    }
    else {
      double d = Math.sqrt(Math.pow(lat - vehicleInFront.lat,2)+Math.pow(lon - vehicleInFront.lon,2));
      return d;
    }
  }
  
  private void calculateVehicleInFront() {
    Vehicle vehicle = null;
    
    List<Integer> vehicles = Oracle.vehiclesWithNodeAsOrigin(originNodeId);
    for (int vin : vehicles) {
      Vehicle v = VehicleRegistry.getVehicle(vin);
      if (v == this || !v.destinationNodeId.equals(destinationNodeId))
        continue;
      
      if (distanceToDestinationNode() > v.distanceToDestinationNode()) {
        if (vehicle == null || v.distanceToDestinationNode() > vehicle.distanceToDestinationNode())
          vehicle = v;
      }
    }
    
    // check one node ahead
    if (vehicle == null) {
      Node destNode = Global.openStreetMap.getNode(destinationNodeId);
      if (destNode.connectedNodes.size() == 2) {
        if (destNode.connectedNodes.get(0).id.equals(originNodeId))
          vehicle = findVehicleClosestToOriginNode(destNode.id, destNode.connectedNodes.get(1).id);
        else
          vehicle = findVehicleClosestToOriginNode(destNode.id, destNode.connectedNodes.get(0).id);
      }
    }
    
    vehicleInFront = vehicle;
  }
  
  private Vehicle findVehicleClosestToOriginNode(String originNodeId, String destinationNodeId) {
    List<Integer> vehicles = Oracle.vehiclesWithNodeAsOrigin(originNodeId);
    
    if (vehicles == null || vehicles.isEmpty()) {
      return null;
    }
    else {
      Vehicle vehicle = null;
      for (Integer vin : vehicles) {
        Vehicle v = VehicleRegistry.getVehicle(vin);
        if (!v.getDestinationNode().id.equals(destinationNodeId))
          continue;
        if (vehicle == null || v.distanceFromOriginNode() < vehicle.distanceFromOriginNode())
          vehicle = v;
      }
      return vehicle;
    }
  }
  
  private double angleOfTravel() {
    Node lastNode = getOriginNode();
    Node nextNode = getDestinationNode();
    double angle = -Math.atan2((Double.valueOf(nextNode.lat) - Double.valueOf(lastNode.lat)), (Double.valueOf(nextNode.lon) - Double.valueOf(lastNode.lon)));
    angle = Math.toDegrees(angle);
    if (angle < 0)
      angle = 360 + angle;
    return Math.toRadians(angle);
  }
  
  // move by velocity in the right direction
  public void setVelocity(double speed) {
    Node lastNode = getOriginNode();
    Node nextNode = getDestinationNode();
    
    double oldC = distanceFromOriginNode();// Math.sqrt(Math.pow(lat - Double.valueOf(lastNode.lat),2)+Math.pow(lon - Double.valueOf(lastNode.lon),2));
    double newC = (oldC + speed);
    
    double angle = angleOfTravel();
    double deltaLat = Math.sin(angle)*newC;
    double deltaLon = Math.cos(angle)*newC;
    
    // based on this we can negate deltaLat or deltaLon to the correct sign
    if (Math.toDegrees(angle) < 0) {
      deltaLat*=1;
      deltaLon*=1;
    }
    else if (Math.toDegrees(angle) > 45) {
      deltaLat*=-1;
      deltaLon*=1;
    }
    else {
      deltaLat*=-1;
      deltaLon*=1;
    }
    
    double newLat = Double.valueOf(lastNode.lat) + deltaLat;
    double newLon = Double.valueOf(lastNode.lon) + deltaLon;
    
    velocity = new Vector2d(newLat-lat, newLon-lon);
  }
}