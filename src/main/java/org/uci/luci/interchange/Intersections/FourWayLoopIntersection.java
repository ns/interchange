package org.uci.luci.interchange.Intersections;

import org.uci.luci.interchange.Driver.VehicleDriverBehavior.V2IMessage;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Vehicles.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FourWayLoopIntersection extends Intersection {
	String eastNodeId, westNodeId;
	String northNodeId, southNodeId;

	boolean ewGreen = false;
	boolean nsGreen = false;

	int switchInterval;
	double lastFlip = -1;

	private LoopLightFSM lightFSM;

	public FourWayLoopIntersection(String rootNodeId) {
		super(rootNodeId);
		Random randomGenerator = Utils.randomNumberGenerator();
		switchInterval = 30;
		lastFlip = -randomGenerator.nextInt(switchInterval);
		generateGroups();
		lightFSM = new LoopLightFSM(30, 5, 10, 5);
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
		lightFSM.tick(simTime, tickLength, tick);
	}

	public void vehicleIsApproaching(Vehicle v, String originNodeId,
			String toNodeId, int lane, V2IMessage msg) {
		if (v.getOriginNode().id.equals(eastNodeId)
				|| v.getOriginNode().id.equals(westNodeId)) {
			if (toNodeId != null && isLeftTurn(originNodeId, toNodeId)) {
				lightFSM.detectVehicleOnLeftGroup1(v.vin + "");
			} else if (toNodeId != null && isRightTurn(originNodeId, toNodeId)) {
				// ignore
			} else {
				lightFSM.detectVehicleOnThroughGroup1(v.vin + "");
			}
		} else if (v.getOriginNode().id.equals(northNodeId)
				|| v.getOriginNode().id.equals(southNodeId)) {
			if (toNodeId != null && isLeftTurn(originNodeId, toNodeId)) {
				lightFSM.detectVehicleOnLeftGroup2(v.vin + "");
			} else if (toNodeId != null && isRightTurn(originNodeId, toNodeId)) {
				// ignore
			} else {
				lightFSM.detectVehicleOnThroughGroup2(v.vin + "");
			}
		}
	}

	public void vehicleIsLeaving(Vehicle v) {
		lightFSM.undetectVehicle(v.vin + "");
	}
}
