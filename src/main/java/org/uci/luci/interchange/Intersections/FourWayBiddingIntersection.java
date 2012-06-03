package org.uci.luci.interchange.Intersections;

import org.uci.luci.interchange.Driver.VehicleDriverBehavior.V2IMessage;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Vehicles.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class FourWayBiddingIntersection extends Intersection {
	String eastNodeId, westNodeId;
	String northNodeId, southNodeId;

	private InterchangeLightFSM lightFSM;

	public FourWayBiddingIntersection(String rootNodeId) {
		super(rootNodeId);
		generateGroups();
		lightFSM = new InterchangeLightFSM(10, 30, 5, 10, 5);
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
		// just count it as a +1 bid for the moment
		if (v.getOriginNode().id.equals(eastNodeId)
				|| v.getOriginNode().id.equals(westNodeId)) {
			if (toNodeId != null && isLeftTurn(originNodeId, toNodeId)) {
				lightFSM.acceptBidGroup1Left(v.vin + "", msg.bid);
			} else if (toNodeId != null && isRightTurn(originNodeId, toNodeId)) {
				lightFSM.acceptBidGroup1Right(v.vin + "", msg.bid);
			} else {
				lightFSM.acceptBidGroup1Through(v.vin + "", msg.bid);
			}
		} else if (v.getOriginNode().id.equals(northNodeId)
				|| v.getOriginNode().id.equals(southNodeId)) {
			if (toNodeId != null && isLeftTurn(originNodeId, toNodeId)) {
				lightFSM.acceptBidGroup2Left(v.vin + "", msg.bid);
			} else if (toNodeId != null && isRightTurn(originNodeId, toNodeId)) {
				lightFSM.acceptBidGroup2Right(v.vin + "", msg.bid);
			} else {
				lightFSM.acceptBidGroup2Through(v.vin + "", msg.bid);
			}
		}
	}

	public void vehicleIsLeaving(Vehicle v) {
		lightFSM.clearBidForVehicle(v.vin + "");
	}
}
