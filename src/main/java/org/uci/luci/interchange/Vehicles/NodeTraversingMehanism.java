package org.uci.luci.interchange.Vehicles;

import java.util.List;
import java.util.Random;

import org.uci.luci.interchange.Graph.Node;
import org.uci.luci.interchange.Graph.Way;
import org.uci.luci.interchange.Intersections.Intersection;
import org.uci.luci.interchange.Registry.IntersectionRegistry;
import org.uci.luci.interchange.Registry.VehicleRegistry;
import org.uci.luci.interchange.Util.Global;
import org.uci.luci.interchange.Util.Oracle;
import org.uci.luci.interchange.Util.Utils;
import org.uci.luci.interchange.Util.Vector2d;

// This class makes traversing node-by-node invisible to vehicles and drivers.
// Simply set the origin and destination node and getNextNodeId() will keep
// returning the next possible node. It will return null on dead-ends and
// intersections.
public class NodeTraversingMehanism {
	private String originNodeId, destinationNodeId;

	public NodeTraversingMehanism() {

	}

	public void setOriginNodeId(String nodeId) {
		originNodeId = nodeId;
	}

	public void setDestinationNodeId(String nodeId) {
		destinationNodeId = nodeId;
	}

	// public boolean isAtDestinationNode() {
	// return distanceToDestinationNode() < DISTANCE_TO_CONSIDER_AS_SAME;
	// }
	//
	// public boolean isAtOriginNode() {
	// return distanceFromOriginNode() < DISTANCE_TO_CONSIDER_AS_SAME;
	// }

	public double distanceBetweenDestinationAndOriginNode() {
		Node nextNode = getDestinationNode();
		Node lastNode = getOriginNode();
    // double d = Math.sqrt(Math.pow(lastNode.lat - nextNode.lat, 2)
    //    + Math.pow(lastNode.lon - nextNode.lon, 2));
    // return d;
    return Utils.distance(nextNode.lat, nextNode.lon, lastNode.lat, lastNode.lon, 'K');
	}

	// public double distanceToDestinationNode() {
	// Node nextNode = getDestinationNode();
	// double d = Math.sqrt(Math.pow(lat - nextNode.lat, 2)
	// + Math.pow(lon - nextNode.lon, 2));
	// return d;
	// }
	//
	// public double distanceFromOriginNode() {
	// Node lastNode = getOriginNode();
	// double d = Math.sqrt(Math.pow(lat - lastNode.lat, 2)
	// + Math.pow(lon - lastNode.lon, 2));
	// return d;
	// }

	public Node getOriginNode() {
		return Global.openStreetMap.getNode(originNodeId);
	}

	public Node getDestinationNode() {
		return Global.openStreetMap.getNode(destinationNodeId);
	}

	public Node getNextNode() {
		Node lastNode = getOriginNode();
		Node nextNode = getDestinationNode();

		if (nextNode.connectedNodes.size() == 2) {
			// pretty obvious where the car goes..
			int i = nextNode.connectedNodes.indexOf(lastNode);
			setOriginNodeId(nextNode.id);
			if (i == 0) {
				// setDestinationNodeId(nextNode.connectedNodes.get(1).id);
				return nextNode.connectedNodes.get(1);
			} else {
				// setDestinationNodeId(nextNode.connectedNodes.get(0).id);
				return nextNode.connectedNodes.get(0);
			}
		} else if (nextNode.connectedNodes.size() == 1) {
			return null;
		} else {
			return null;
		}
	}

	public double distanceToDestinationNode(double lat, double lon) {
    Node nextNode = getDestinationNode();
    // double d = Math.sqrt(Math.pow(lat - nextNode.lat, 2)
    //    + Math.pow(lon - nextNode.lon, 2));
    // return d;
    return Utils.distance(lat, lon, nextNode.lat, nextNode.lon, 'K');
	}

	public double distanceFromOriginNode(double lat, double lon) {
    Node lastNode = getOriginNode();
    // double d = Math.sqrt(Math.pow(lat - lastNode.lat, 2)
    //    + Math.pow(lon - lastNode.lon, 2));
    // return d;
    return Utils.distance(lat, lon, lastNode.lat, lastNode.lon, 'K');
	}

	// private Node randomConnectedNode(Node n, Node excludeNode) {
	// Random randomGenerator = Utils.randomNumberGenerator();
	//
	// int nodeIndex = -1;
	// int excludeNodeIndex = n.connectedNodes.indexOf(excludeNode);
	//
	// while (nodeIndex == -1 || nodeIndex == excludeNodeIndex) {
	// nodeIndex = randomGenerator.nextInt(n.connectedNodes.size());
	// }
	//
	// return n.connectedNodes.get(nodeIndex);
	// }
}
