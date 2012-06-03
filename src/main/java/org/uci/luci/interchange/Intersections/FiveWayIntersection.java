package org.uci.luci.interchange.Intersections;

import org.uci.luci.interchange.Driver.VehicleDriverBehavior.V2IMessage;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Vehicles.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FiveWayIntersection extends Intersection {
	String eastNodeId, westNodeId;
	String northNodeId, southNodeId;
	String branchNodeId;
	boolean branchIsClosestToEWGroup;

	private LightFSM lightFSM;

	public FiveWayIntersection(String rootNodeId) {
		super(rootNodeId);
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
		List<Node> g2 = Utils.findNodesWithAngleBetweenClosestTo180(rootNode,
				connectedNodes);
		eastNodeId = g2.get(0).id;
		westNodeId = g2.get(1).id;
		connectedNodes.remove(g2.get(0));
		connectedNodes.remove(g2.get(1));
		branchNodeId = connectedNodes.get(0).id;
		branchIsClosestToEWGroup = false;
	}

	public String getState() {
		return lightFSM.getState();
	}

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
			} else if (originNodeId.equals(branchNodeId)
					&& branchIsClosestToEWGroup) {
				if (isLeftTurn(originNodeId, toNodeId)) {
					return lightFSM.getLightForLefts1();
				} else if (isRightTurn(originNodeId, toNodeId)) {
					return lightFSM.getLightForRights1();
				} else {
					return lightFSM.getLightForThrough1();
				}
			} else if (originNodeId.equals(branchNodeId)
					&& !branchIsClosestToEWGroup) {
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
		lightFSM.tick(simTime, tickLength, tick);
	}

	public void vehicleIsApproaching(Vehicle v, String originNodeId,
			String toNodeId, int lane, V2IMessage msg) {
	}

	public void vehicleIsLeaving(Vehicle v) {
	}
}
