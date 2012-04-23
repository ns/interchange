package org.uci.luci.interchange;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.Random;

public class Vehicle {
  double lat, lon;
  Vector2d velocity;
  String lastPassedNodeId;
  Way _way;
  OpenStreetMap openStreetMap;
  
  public Vehicle(float lat, float lon) {
    this.lat = lat;
    this.lon = lon;
  }
  
  private String nextNodeOnWay(Way w, String nId, boolean forward) {
    int nIdIndex = w.nd.indexOf(nId);
    
    if (forward) {
      if (nIdIndex >= w.nd.size() - 1)
        return null;
      else
        return w.nd.get(nIdIndex + 1);
    }
    else {
      nIdIndex = w.nd.size();
      if (nIdIndex == 0)
        return null;
      else
        return w.nd.get(nIdIndex - 1);
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
  
  double speed = 0.000001;
  String originNodeId, destinationNodeId;
  public void navTick() {
    if (originNodeId == null) {
      // establish the origin node from our lat/lon
      originNodeId = nextNodeOnWay(_way, null, true);
    }
    
    if (destinationNodeId == null) {
      destinationNodeId = nextNodeOnWay(_way, originNodeId, true);
    }
    
    Node lastNode = openStreetMap.getNode(originNodeId);
    Node nextNode = openStreetMap.getNode(destinationNodeId);
    
    double d = Math.sqrt(Math.pow(lat - Double.valueOf(nextNode.lat),2)+Math.pow(lon - Double.valueOf(nextNode.lon),2));
    
    if (lastNode.connectedNodes.size()==0) {
      System.out.println("no connected nodes!!!");
    }
    
    if (d < 0.0000005) {
      if (nextNode.connectedNodes.size() == 2) {
        // pretty obvious where the car goes..
        int i = nextNode.connectedNodes.indexOf(lastNode);
        originNodeId = nextNode.id;
        if (i == 0) {
          destinationNodeId = nextNode.connectedNodes.get(1).id;
        }
        else {
          destinationNodeId = nextNode.connectedNodes.get(0).id;
        }
      }
      else if (nextNode.connectedNodes.size() == 1) {
        String _originNodeId = originNodeId;
        originNodeId = destinationNodeId;
        destinationNodeId = _originNodeId;
      }
      else {
        // we assume that this is an intersection
        // pick a random way to go.
        int i = nextNode.connectedNodes.indexOf(lastNode);
        originNodeId = nextNode.id;
        destinationNodeId = randomConnectedNode(nextNode, lastNode).id;
      }
    }
    
    
    // System.out.println("Tag Data:");
    // for (int i = 0; i < lastNode.way.tags.size(); i++) {
    //   Tag t = lastNode.way.tags.get(i);
    //   System.out.println("\t"+t.k + " = " + t.v);
    // }
  }
  
  private double angleOfTravel() {
    Node lastNode = openStreetMap.getNode(originNodeId);
    Node nextNode = openStreetMap.getNode(destinationNodeId);
    // double angle = Math.abs(Math.atan((Double.valueOf(nextNode.lat) - Double.valueOf(lastNode.lat)) / (Double.valueOf(nextNode.lon) - Double.valueOf(lastNode.lon))));
    // double angle = -Math.atan2((Double.valueOf(nextNode.lon) - Double.valueOf(lastNode.lon)), (Double.valueOf(nextNode.lat) - Double.valueOf(lastNode.lat)));
    double angle = -Math.atan2((Double.valueOf(nextNode.lat) - Double.valueOf(lastNode.lat)), (Double.valueOf(nextNode.lon) - Double.valueOf(lastNode.lon)));
    angle = Math.toDegrees(angle);
    // System.out.println("--> " + angle);
    if (angle < 0)
      angle = 360 + angle;
    return Math.toRadians(angle);
  }
  
  private double distanceToNextNode() {
    Node lastNode = openStreetMap.getNode(originNodeId);
    Node nextNode = openStreetMap.getNode(destinationNodeId);
    double d = Math.sqrt(Math.pow(lat - Double.valueOf(nextNode.lat),2)+Math.pow(lon - Double.valueOf(nextNode.lon),2));
    return d;
  }
  
  public void tick() {
    // on every tick the vehicle does the following
    // (1) figures out it's distance to the next node (based last node and next node)
    // (2) figures out the angle for the vector it must traverse to reach the next node
    // (3) figures out the speed (magnitude) of the vector it must traverse
    // (4) sets it's velocity vector based on these calculations
    
    navTick();
    
    // double speed = 0.000002;
    
    // move by velocity in the right direction
    Node lastNode = openStreetMap.getNode(originNodeId);
    Node nextNode = openStreetMap.getNode(destinationNodeId);
    
    double angle = angleOfTravel();
    
    double oldC = Math.sqrt(Math.pow(lat - Double.valueOf(lastNode.lat),2)+Math.pow(lon - Double.valueOf(lastNode.lon),2));
    double newC = (oldC + speed);
    double deltaLat = Math.sin(angle)*newC;
    double deltaLon = Math.cos(angle)*newC;
    
    
    // System.out.println("rad = " + angle + " deg = " + Math.toDegrees(angle));
    
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
      // System.out.println("???");
    }
    
    double newLat = Double.valueOf(lastNode.lat) + deltaLat;
    double newLon = Double.valueOf(lastNode.lon) + deltaLon;
    
    velocity = new Vector2d(newLat-lat, newLon-lon);
    // velocity = new Vector2d(0,0);
    
    // v.lat = (float)newLat;
    // v.lon = (float)newLon;
    
    // // move by velocity in the right direction
    // String lastNodeId = lastPassedNodeId;
    // if (lastNodeId == null)
    //   lastNodeId = lastPassedNodeId = _way.nd.get(0);
    // Node lastNode = openStreetMap.getNode(lastNodeId);
    // 
    // String nextNodeId = _way.nd.get(_way.nd.indexOf(lastNodeId) + 1);
    // Node nextNode = openStreetMap.getNode(nextNodeId);
    // 
    // double d = Math.sqrt(Math.pow(lat - Double.valueOf(nextNode.lat),2)+Math.pow(lon - Double.valueOf(nextNode.lon),2));
    // 
    // // System.out.println(d);
    // if (d < 0.00005) {
    //   if (_way.nd.size() - 2 == _way.nd.indexOf(lastNodeId)) {
    //     
    //     // if (nextNode.connectedNodes().get(0).)
    //     // here we have to figure out if we can jump onto another way
    //     // by looking at nextNode.connectedNodes and figuring out a node
    //     // we'd like to hop onto. we have to make sure it's not lastNode
    //     // and then we need to figure out which way that node belongs to and set
    //     // that as _way and figure out what lastPassedNodeId will be and what
    //     // nextNode will be. since we wont be able to find lastPassedNodeId based
    //     // on our current _way =/
    //     
    //     System.out.println("I don't know where to go!");
    //     velocity = new Vector2d(0,0);
    //     
    //     
    //     Node nodeOnNextWay = null;
    //     
    //     for (int i = 0; i < nextNode.connectedNodes.size(); i++) {
    //       Node conNode = nextNode.connectedNodes.get(i);
    //       System.out.println("-> " + conNode);
    //       
    //       if (nodeOnNextWay != conNode) {
    //         nodeOnNextWay = conNode;
    //       }
    //     }
    //     
    //     if (nodeOnNextWay == null) {
    //       System.out.println("oh no, we have no where to go!");
    //       return;
    //     }
    //     else {
    //       _way = nodeOnNextWay.way;
    //       lastPassedNodeId = nodeOnNextWay.way.nd.get(nodeOnNextWay.way.nd.size()-1);//nodeOnNextWay.id;
    //       return;
    //       // lastNode = nextNode;
    //       // nextNode = nodeOnNextWay;
    //     }
    //     
    //     // return;
    //   }
    //   else {
    //     lastNodeId = lastPassedNodeId = _way.nd.get(_way.nd.indexOf(lastNodeId) + 1);
    //     lastNode = openStreetMap.getNode(lastNodeId);
    //     nextNodeId = _way.nd.get(_way.nd.indexOf(lastNodeId) + 1);
    //     nextNode = openStreetMap.getNode(nextNodeId);
    //     // System.out.println("next node");
    //   }
    // }
    // 
    // double angle = Math.abs(Math.atan((Double.valueOf(nextNode.lat) - Double.valueOf(lastNode.lat)) / (Double.valueOf(nextNode.lon) - Double.valueOf(lastNode.lon))));
    // 
    // double oldC = Math.sqrt(Math.pow(lat - Double.valueOf(lastNode.lat),2)+Math.pow(lon - Double.valueOf(lastNode.lon),2));
    // 
    // double newC = (oldC + speed); //velocity;
    // 
    // double deltaLat = Math.sin(angle)*newC;
    // double deltaLon = Math.cos(angle)*newC;
    // 
    // // System.out.println("angle = " + Math.toDegrees(angle) + " oldC = " + oldC + " newC="+newC);
    // 
    // // based on this we can negate deltaLat or deltaLon to the correct sign
    // if (Math.toDegrees(angle) > 45) {
    //   deltaLat*=-1;
    //   deltaLon*=1;
    // }
    // else {
    //   deltaLat*=-1;
    //   deltaLon*=1;
    //   // System.out.println("???");
    // }
    // 
    // // double newLat = Double.valueOf(lastNode.lat) + deltaLat;
    // // double newLon = Double.valueOf(lastNode.lon) + deltaLon;
    // 
    // // System.out.println("deltaLat = " + deltaLat + " deltaLon = " + deltaLat);
    // // System.out.println("newC = " + newC + " old ("+v.lat+","+v.lon+") new ("+newLat+","+newLon+")");
    // 
    // double newLat = Double.valueOf(lastNode.lat) + deltaLat;
    // double newLon = Double.valueOf(lastNode.lon) + deltaLon;
    // 
    // velocity = new Vector2d(newLat-lat, newLon-lon);
    // 
    // // v.lat = (float)newLat;
    // // v.lon = (float)newLon;
  }
}