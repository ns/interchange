package org.uci.luci.interchange.Intersections;

import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Vehicles.*;

public class HighwayRamp extends Intersection {
	public HighwayRamp(String rootNodeId) {
		super(rootNodeId);
	}

	// 0 = green, 1 = yellow, 2 = red
	// public int getLightForWayOnLane(Way w, int lane) {
	@Override
	public LightFSM.LIGHT getLightForWayOnLane(Way w, String originNodeId, String toNodeId, int lane) {
	  return LightFSM.LIGHT.GREEN;
	}
	
	@Override
	public String getState() {
	  return "Green";
	}

	@Override
	public void tick(double simTime, double tickLength, int tick) {}

	@Override
	public void vehicleIsApproaching(Vehicle v) {}

	@Override
	public void vehicleIsLeaving(Vehicle v) {}
}
