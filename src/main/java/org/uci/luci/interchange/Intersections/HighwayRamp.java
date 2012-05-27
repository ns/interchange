package org.uci.luci.interchange.Intersections;

import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Vehicles.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class HighwayRamp extends Intersection {
	public HighwayRamp(String rootNodeId) {
		super(rootNodeId);
	}

	// 0 = green, 1 = yellow, 2 = red
	// public int getLightForWayOnLane(Way w, int lane) {
	public LightFSM.LIGHT getLightForWayOnLane(Way w, String originNodeId, String toNodeId, int lane) {
	  return LightFSM.LIGHT.GREEN;
	}
	
	public String getState() {
	  return "Green";
	}

	public void tick(double simTime, double tickLength, int tick) {}

	public void vehicleIsApproaching(Vehicle v) {}

	public void vehicleIsLeaving(Vehicle v) {}
}
