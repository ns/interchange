package org.uci.luci.interchange.Intersections;

import org.uci.luci.interchange.Driver.VehicleDriverBehavior.V2IMessage;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Vehicles.*;

// import java.util.List;

public abstract class Intersection {
	public String id;
	private String rootNodeId;
	private double bounds = 20;

	public Intersection(String nodeId) {
		rootNodeId = nodeId;
	}

	public abstract void tick(double simTime, double tickLength, int tick);

	// called when a vehicle can 'see' an intersection
	public abstract void vehicleIsApproaching(Vehicle v, String originNodeId,
			String toNodeId, int lane, V2IMessage msg);

	// called when a vehicle passes an intersection
	public abstract void vehicleIsLeaving(Vehicle v);

	public String getRootNodeId() {
		return rootNodeId;
	}

	public double getBounds() {
		return bounds;
	}

	// 0 = green, 1 = yellow, 2 = red
	public LightFSM.LIGHT getLightForWayOnLane(Way w, String originNodeId,
			String toNodeId, int lane) {
		return LightFSM.LIGHT.RED;
	}

	public double angle(String fromNodeId, String toNodeId) {
		Node rootNode = Global.openStreetMap.getNode(getRootNodeId());
		Node node1 = Global.openStreetMap.getNode(fromNodeId);
		Node node2 = Global.openStreetMap.getNode(toNodeId);
		double angle = Utils.angleBetweenNodesWithCenterNode(rootNode, node1,
				node2);
		return Math.toDegrees(angle);
	}

	public abstract String getState();

	public boolean isLeftTurn(String fromNodeId, String toNodeId) {
		double angle = angle(fromNodeId, toNodeId);
		if (angle > 225 && angle < 315)
			return true;
		if (angle < -45 && angle > -135)
			return true;
		return false;
	}

	public boolean isRightTurn(String fromNodeId, String toNodeId) {
		double angle = angle(fromNodeId, toNodeId);
		if (angle < -225 && angle > -315)
			return true;
		if (angle > 45 && angle < 135)
			return true;
		return false;
	}
}
