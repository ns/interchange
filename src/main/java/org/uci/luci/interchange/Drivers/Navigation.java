package org.uci.luci.interchange;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;

public class Navigation {
  private String originNodeId;
  private String destinationNodeId;
  private LinkedList<Node> path;
  
  public Navigation(String originNodeId, String destinationNodeId) throws NoPathToDestinationException {
    this.originNodeId = originNodeId;
    this.destinationNodeId = destinationNodeId;
    generatePath();
  }
  
  public Navigation(String originNodeId) throws NoPathToDestinationException {
    this.originNodeId = originNodeId;
    pickRandomDestination();
    generatePath();
  }
  
  public Navigation() throws NoPathToDestinationException {
    pickRandomOriginAndDestination();
    generatePath();
  }
  
  public Node nextNodeOnPath(String curNodeId) {
    for (int i = 0; i < path.size(); i++) {
      if (path.get(i).id.equals(curNodeId) && i < path.size() - 1) {
        return path.get(i+1);
      }
    }
    
    return null;
  }
  
  //////////////////////////////////////////////////////////////////////////////
  // accessors /////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////
  
  public String getOrigin() {
    return originNodeId;
  }
  
  public String getDestination() {
    return destinationNodeId;
  }
  
  public List<Node> getPath() {
    return path;
  }
  
  //////////////////////////////////////////////////////////////////////////////
  // helpful utilities /////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////
  
  public void pickRandomDestination() {
    List<Node> nodes = Global.openStreetMap.nodes();
    Random generator = new Random();
    while (destinationNodeId == null || destinationNodeId.equals(originNodeId)) {
      Node destinationNode = nodes.get(generator.nextInt(nodes.size()));
      destinationNodeId = destinationNode.id;
    }
  }
  
  public void pickRandomOriginAndDestination() {
    List<Node> nodes = Global.openStreetMap.nodes();
    Random generator = new Random();
    Node originNode = nodes.get(generator.nextInt(nodes.size()));
    originNodeId = originNode.id;
    while (destinationNodeId == null || destinationNodeId.equals(originNodeId)) {
      Node destinationNode = nodes.get(generator.nextInt(nodes.size()));
      destinationNodeId = destinationNode.id;
    }
  }
  
  private void generatePath() throws NoPathToDestinationException {
    Node startNode = Global.openStreetMap.getNode(originNodeId);
    Node endNode = Global.openStreetMap.getNode(destinationNodeId);
    LinkedList<Node> aStarResult = (LinkedList)Global.openStreetMap.AStar2.findPath(startNode, endNode);
    if (aStarResult == null) {
      System.out.println("Unable to generate a path between " + originNodeId + " and " + destinationNodeId);
      throw new NoPathToDestinationException();
    }
    else {
      aStarResult.addFirst(startNode);
      path = aStarResult;
    }
  }
}