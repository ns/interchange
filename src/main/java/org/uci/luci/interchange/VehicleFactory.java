package org.uci.luci.interchange;

import java.util.Random;
import java.util.List;

public class VehicleFactory {
  public static Vehicle createVehicleAtRandomPoint() {
    Random generator = new Random();

    List<Node> nodes = Global.openStreetMap.nodes();

    Node randomNode = nodes.get(generator.nextInt(nodes.size()));
    Node randomNextNode = randomNode.connectedNodes.get(generator.nextInt(randomNode.connectedNodes.size()));
    // System.out.println(randomNode.id);
    Vehicle vehicle = new Vehicle(
      randomNode.lat,
      randomNode.lon,
      randomNode.id,
      randomNextNode.id,
      // this isn't really right because node.way might not refer to the right way at intersections
      generator.nextInt(Oracle.wayBetweenNodes(randomNode.id, randomNextNode.id).lanes)
    );

    return vehicle;
 	}
 	
  public static Vehicle createVehicleAtNode(Node n) {
    Random generator = new Random();

    List<Node> nodes = Global.openStreetMap.nodes();

    Node randomNode = n;
    // Node randomNextNode = randomNode.connectedNodes.get(2);
    Node randomNextNode = randomNode.connectedNodes.get(generator.nextInt(randomNode.connectedNodes.size()));

    Vehicle vehicle = new Vehicle(
      randomNode.lat,
      randomNode.lon,
      randomNode.id,
      randomNextNode.id,
      // this isn't really right because node.way might not refer to the right way at intersections
      generator.nextInt(Oracle.wayBetweenNodes(randomNode.id, randomNextNode.id).lanes)
    );

    return vehicle;
 	}
}