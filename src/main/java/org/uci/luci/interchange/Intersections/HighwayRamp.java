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
	public int getLightForWayOnLane(Way w, String originNodeId, int lane) {
		return 0;
	}

	public void tick(double simTime, double tickLength, int tick) {
	}

	public void vehicleIsApproaching(Vehicle v) {
	}

	public void vehicleIsLeaving(Vehicle v) {
	}

	public boolean isLeftTurn(String fromNodeId, String toNodeId) {
		return false;
	}

	public boolean isRightTurn(String fromNodeId, String toNodeId) {
		return false;
	}
}
