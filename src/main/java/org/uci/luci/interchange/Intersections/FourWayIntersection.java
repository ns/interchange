package org.uci.luci.interchange.Intersections;

import org.uci.luci.interchange.Driver.VehicleDriverBehavior.V2IMessage;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Vehicles.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FourWayIntersection extends Intersection {
	String eastNodeId, westNodeId;
	String northNodeId, southNodeId;

	boolean ewGreen = false;
	boolean nsGreen = false;

	int switchInterval;
	double lastFlip = -1;

	private LightFSM lightFSM;

	public FourWayIntersection(String rootNodeId) {
		super(rootNodeId);
		Random randomGenerator = Utils.randomNumberGenerator();
		switchInterval = 30;
		lastFlip = -randomGenerator.nextInt(switchInterval);
		generateGroups();
		lightFSM = new LightFSM(30, 5, 10, 5);
	}

	private void generateGroups() {
		Node rootNode = Global.openStreetMap.getNode(getRootNodeId());
		List<Node> connectedNodes = (List<Node>) rootNode.connectedNodes
				.clone();
		List<Node> g1 = Utils.findNodesWithAngleBetweenClosestTo180(rootNode,
				connectedNodes);
		northNodeId = g1.get(0).id;
		southNodeId = g1.get(1).id;
		connectedNodes.remove(g1.get(0));
		connectedNodes.remove(g1.get(1));
		eastNodeId = connectedNodes.get(0).id;
		westNodeId = connectedNodes.get(1).id;
	}

	// public int getLightForWayOnLane(Way w, String originNodeId, int lane) {
	// if (originNodeId.equals(eastNodeId) || originNodeId.equals(westNodeId)) {
	// return ewGreen ? 0 : 2;
	// } else if (originNodeId.equals(northNodeId) ||
	// originNodeId.equals(southNodeId)) {
	// return nsGreen ? 0 : 2;
	// } else {
	// System.out.println("no equals, called with originNodeId = " +
	// originNodeId);
	// }
	// return 2;
	// }

	public String getState() {
		return lightFSM.getState();
	}

	// 0 = green, 1 = yellow, 2 = red
	// public int getLightForWayOnLane(Way w, int lane) {
	public LightFSM.LIGHT getLightForWayOnLane(Way w, String originNodeId,
			String toNodeId, int lane) {
		if (toNodeId == null) {
			if (originNodeId.equals(eastNodeId)
					|| originNodeId.equals(westNodeId)) {
				return lightFSM.getLightForThrough1();
			} else if (originNodeId.equals(northNodeId)
					|| originNodeId.equals(southNodeId)) {
				return lightFSM.getLightForThrough2();
			} else {
				return LightFSM.LIGHT.RED;
			}
		} else {
			if (originNodeId.equals(eastNodeId)
					|| originNodeId.equals(westNodeId)) {
				if (isLeftTurn(originNodeId, toNodeId)) {
					return lightFSM.getLightForLefts1();
				} else if (isRightTurn(originNodeId, toNodeId)) {
					return lightFSM.getLightForRights1();
				} else {
					return lightFSM.getLightForThrough1();
				}
			} else if (originNodeId.equals(northNodeId)
					|| originNodeId.equals(southNodeId)) {
				if (isLeftTurn(originNodeId, toNodeId)) {
					return lightFSM.getLightForLefts2();
				} else if (isRightTurn(originNodeId, toNodeId)) {
					return lightFSM.getLightForRights2();
				} else {
					return lightFSM.getLightForThrough2();
				}
			} else {
				return LightFSM.LIGHT.RED;
			}
		}
	}

	public void tick(double simTime, double tickLength, int tick) {
		if ((simTime - lastFlip) >= switchInterval) {
			if (nsGreen) {
				nsGreen = false;
				ewGreen = true;
			} else {
				nsGreen = true;
				ewGreen = false;
			}
			lastFlip = simTime;
		}

		lightFSM.tick(simTime, tickLength, tick);
	}

	public void vehicleIsApproaching(Vehicle v, String originNodeId,
			String toNodeId, int lane, V2IMessage msg) {
		// info about vehicle
		// System.out.println("v " + v.vin + " is approaching " + id);
		// System.out.println("\tvin = " + v.vin);
		// System.out.println("\torigin node = " + v.getOriginNode().id);
		// System.out.println("\tdestination node = " +
		// v.getDestinationNode().id);
	}

	public void vehicleIsLeaving(Vehicle v) {
		// System.out.println("v " + v.vin + " is leaving " + id);
	}
}
