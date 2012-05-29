package org.uci.luci.interchange.Driver;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.uci.luci.interchange.Exception.NoPathToDestinationException;
import org.uci.luci.interchange.Graph.Node;
import org.uci.luci.interchange.Util.Global;
import org.uci.luci.interchange.Util.Utils;

public class DestinationNavigation implements Navigation {
	private String originNodeId;
	private String destinationNodeId;
	private LinkedList<Node> path;

	public DestinationNavigation(String originNodeId, String destinationNodeId)
	throws NoPathToDestinationException {
		this.originNodeId = originNodeId;
		this.destinationNodeId = destinationNodeId;
		generatePath();
	}

	public DestinationNavigation(String originNodeId)
	throws NoPathToDestinationException {
		this.originNodeId = originNodeId;
		pickRandomDestination();
		generatePath();
	}

	public DestinationNavigation() throws NoPathToDestinationException {
		pickRandomOriginAndDestination();
		generatePath();
	}

	public Node nextNodeOnPath(String curNodeId) {
		for (int i = 0; i < path.size(); i++) {
			if (path.get(i).id.equals(curNodeId) && i < path.size() - 1) {
				return path.get(i + 1);
			}
		}

		return null;
	}

	// ////////////////////////////////////////////////////////////////////////////
	// accessors
	// /////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////

	public String getOrigin() {
		return originNodeId;
	}

	public String getDestination() {
		return destinationNodeId;
	}

	public List<Node> getPath() {
		return path;
	}

	// ////////////////////////////////////////////////////////////////////////////
	// helpful utilities
	// /////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////

	public void pickRandomDestination() {
		List<Node> nodes = Global.openStreetMap.nodes();
		Random generator = Utils.randomNumberGenerator();
		while (destinationNodeId == null
				|| destinationNodeId.equals(originNodeId)) {
			Node destinationNode = nodes.get(generator.nextInt(nodes.size()));
			destinationNodeId = destinationNode.id;
		}
	}

	public void pickRandomOriginAndDestination() {
		List<Node> nodes = Global.openStreetMap.nodes();
		Random generator = Utils.randomNumberGenerator();
		Node originNode = nodes.get(generator.nextInt(nodes.size()));
		originNodeId = originNode.id;
		while (destinationNodeId == null
				|| destinationNodeId.equals(originNodeId)) {
			Node destinationNode = nodes.get(generator.nextInt(nodes.size()));
			destinationNodeId = destinationNode.id;
		}
	}

	@SuppressWarnings("unchecked")
	private void generatePath() throws NoPathToDestinationException {
		Node startNode = Global.openStreetMap.getNode(originNodeId);
		Node endNode = Global.openStreetMap.getNode(destinationNodeId);

		LinkedList<Node> aStarResult = (LinkedList<Node>) Global.openStreetMap.AStar2.findPath(startNode, endNode);

		if (aStarResult == null) {
			System.out.println("Unable to generate a path between "
					+ originNodeId + " and " + destinationNodeId);
			throw new NoPathToDestinationException();
		} else {
			aStarResult.addFirst(startNode);
			path = aStarResult;
		}
	}

}
