package org.uci.luci.interchange.Factory;

import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Registry.*;
import org.uci.luci.interchange.Exception.NoPathToDestinationException;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;

import java.util.Random;
import java.util.List;

public class VehicleFactory {
	public static Vehicle createVehicleAtRandomPoint()
			throws NoPathToDestinationException {
    Random generator = Utils.randomNumberGenerator();
    List<Node> nodes = Global.openStreetMap.nodes();
    Node randomNode = nodes.get(generator.nextInt(nodes.size()));
    Node randomNextNode = randomNode.connectedNodes.get(generator
       .nextInt(randomNode.connectedNodes.size()));
		
    // double m = (randomNextNode.lon - randomNode.lon) / (randomNextNode.lat - randomNode.lat);
    // double p = (double)generator.nextInt(101) / 100.0;
    // double d_lat = (randomNextNode.lat - randomNode.lat) * p;
    // double d_lon = d_lat * m;
    // 
    // if (randomNextNode.lon - randomNode.lon < 0) {
    //   d_lat = -d_lat;
    //   d_lon = -d_lon;
    // }
    double d_lat = 0;
    double d_lon = 0;
		
		Vehicle vehicle = new Vehicle(randomNode.lat+d_lat, randomNode.lon+d_lon,
				randomNode.id,
				// randomNextNode.id,
				// this isn't really right because node.way might not refer to
				// the right way at intersections
				generator.nextInt(Oracle.wayBetweenNodes(randomNode.id,
						randomNextNode.id).lanes));

		VehicleRegistry.registerVehicle(vehicle);

		return vehicle;
	}

	public static Vehicle createVehicleAtNode(Node n)
			throws NoPathToDestinationException {
		Random generator = Utils.randomNumberGenerator();

		List<Node> nodes = Global.openStreetMap.nodes();

		Node randomNode = n;
		// Node randomNextNode = randomNode.connectedNodes.get(2);
		Node randomNextNode = randomNode.connectedNodes.get(generator
				.nextInt(randomNode.connectedNodes.size()));

		Vehicle vehicle = new Vehicle(randomNode.lat, randomNode.lon,
				randomNode.id,
				// randomNextNode.id,
				// this isn't really right because node.way might not refer to
				// the right way at intersections
				generator.nextInt(Oracle.wayBetweenNodes(randomNode.id,
						randomNextNode.id).lanes));

		VehicleRegistry.registerVehicle(vehicle);

		return vehicle;
	}
	
	public static Vehicle createVehicleAt(String n1id, String n2id)
			throws NoPathToDestinationException {
		Random generator = Utils.randomNumberGenerator();

		Node randomNode = Global.openStreetMap.getNode(n1id);
		Node randomNextNode = Global.openStreetMap.getNode(n2id);
		
		if (!Oracle.hasRoomForCarAtNode(randomNode))
		  return null;
		
		Vehicle vehicle = new Vehicle(randomNode.lat, randomNode.lon,
				randomNode.id,
				// randomNextNode.id,
				// this isn't really right because node.way might not refer to
				// the right way at intersections
				generator.nextInt(Oracle.wayBetweenNodes(randomNode.id,
						randomNextNode.id).lanes));

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