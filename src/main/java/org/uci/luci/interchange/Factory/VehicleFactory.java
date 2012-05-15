package org.uci.luci.interchange.Factory;

import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Registry.*;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;

import java.util.Random;
import java.util.List;

public class VehicleFactory {
  public static Vehicle createVehicleAtRandomPoint() {
    Random generator = Utils.randomNumberGenerator();

    List<Node> nodes = Global.openStreetMap.nodes();

    Node randomNode = nodes.get(generator.nextInt(nodes.size()));
    Node randomNextNode = randomNode.connectedNodes.get(generator.nextInt(randomNode.connectedNodes.size()));
    
    Vehicle vehicle = new Vehicle(
      randomNode.lat,
      randomNode.lon,
      randomNode.id,
      // randomNextNode.id,
      // this isn't really right because node.way might not refer to the right way at intersections
      generator.nextInt(Oracle.wayBetweenNodes(randomNode.id, randomNextNode.id).lanes)
    );
    
    VehicleRegistry.registerVehicle(vehicle);
    
    return vehicle;
 	}
 	
  public static Vehicle createVehicleAtNode(Node n) {
    Random generator = Utils.randomNumberGenerator();
    
    List<Node> nodes = Global.openStreetMap.nodes();

    Node randomNode = n;
    // Node randomNextNode = randomNode.connectedNodes.get(2);
    Node randomNextNode = randomNode.connectedNodes.get(generator.nextInt(randomNode.connectedNodes.size()));

    Vehicle vehicle = new Vehicle(
      randomNode.lat,
      randomNode.lon,
      randomNode.id,
      // randomNextNode.id,
      // this isn't really right because node.way might not refer to the right way at intersections
      generator.nextInt(Oracle.wayBetweenNodes(randomNode.id, randomNextNode.id).lanes)
    );
    
    VehicleRegistry.registerVehicle(vehicle);

    return vehicle;
 	}
 	
 	public static void destroyVehicle(Vehicle v) {
    Oracle.deregisterVehicleOrigin(v.vin, v.getOriginNode().id);
    if (v.getDestinationNode() != null)
      Oracle.deregisterVehicleOrigin(v.vin, v.getDestinationNode().id);
    VehicleRegistry.deregisterVehicle(v);
 	}
}