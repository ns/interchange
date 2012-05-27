package org.uci.luci.interchange.Intersections;

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
	public abstract void vehicleIsApproaching(Vehicle v);

	// called when a vehicle passes an intersection
	public abstract void vehicleIsLeaving(Vehicle v);

	public String getRootNodeId() {
		return rootNodeId;
	}

	public double getBounds() {
		return bounds;
	}

	// 0 = green, 1 = yellow, 2 = red
	public LightFSM.LIGHT getLightForWayOnLane(Way w, String originNodeId, String toNodeId, int lane) {
		return LightFSM.LIGHT.RED;
	}
	
	public abstract String getState();

	public abstract boolean isLeftTurn(String fromNodeId, String toNodeId);

	public abstract boolean isRightTurn(String fromNodeId, String toNodeId);
}
