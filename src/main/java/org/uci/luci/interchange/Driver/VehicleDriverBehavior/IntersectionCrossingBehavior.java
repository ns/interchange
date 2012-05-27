package org.uci.luci.interchange.Driver.VehicleDriverBehavior;

import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Driver.*;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Intersections.*;

public class IntersectionCrossingBehavior implements VehicleDriverBehavior {
	private VehicleDriver vehicleDriver;
	private Vehicle vehicle;
	private static double DISTANCE_BEFORE_REACTING_TO_INTERSECTION = 0.09144; // 300
																				// ft
																				// in
																				// km
	private String state;

	public IntersectionCrossingBehavior(VehicleDriver d) {
		vehicleDriver = d;
		vehicle = vehicleDriver.vehicle;
	}

	public String state() {
		return state;
	}

	public void tick(double simTime, double tickLength) {
		// System.out.println("IntersectionCrossingBehavior tick");
		Intersection i = vehicle.getNextIntersection();
		double d2I = vehicle.getDistanceToNextIntersection();

		if (i == null) {
			System.out.println("we're near an intersection but we don't know which yet. d="+d2I);
			vehicle.setAcceleration(0);
			state = "maintaining speed";
		} else {
			// we're at the intersection
			int light = i.getLightForWayOnLane(null,
					vehicle.getOriginNode().id, vehicle.getOnLaneNumber());

			// green
			if (light == 0) {
				double speedLimit = vehicle.getWay().getSpeedLimit();
				vehicle.setAcceleration(VehicleUtils
						.determineNecessaryAcceleration(vehicle.speed(),
								speedLimit, 0.1524));
				state = "accel to speed limit";
			}
			// yellow
			else if (light == 1) {
				System.out.println("yellow light");
				vehicle.setAcceleration(VehicleUtils
						.determineNecessaryAcceleration(vehicle.speed(), 20,
								d2I));
				state = "accel to 20 km/h";
			}
			// red and we're
			else if (light == 2) {
				if (d2I > DISTANCE_BEFORE_REACTING_TO_INTERSECTION) {
					vehicle.setAcceleration(0);
					state = "maintaining speed, far from light";
				} else {
					if (d2I < 0.01524)
						vehicle.setAcceleration(Vehicle.MAX_NEG_ACCELERATION);
					else
						vehicle.setAcceleration(VehicleUtils
								.determineNecessaryAcceleration(
										vehicle.speed(), 0, d2I - 0.01524));
					state = "accel to 0";
				}
			}

			if (d2I <= Vehicle.DISTANCE_TO_CONSIDER_AS_SAME) {
				Node nextNode = vehicle.navigation.nextNodeOnPath(vehicle
						.getDestinationNode().id);

				if (nextNode != null) {
					if (i.isLeftTurn(vehicle.getOriginNode().id, nextNode.id)) {
						vehicle.setOriginNodeId(vehicle.getDestinationNode().id);
						vehicle.setDestinationNodeId(nextNode.id);
						vehicle.setOnLaneNumber(0);
					} else if (i.isRightTurn(vehicle.getOriginNode().id,
							nextNode.id)) {
						vehicle.setOriginNodeId(vehicle.getDestinationNode().id);
						vehicle.setDestinationNodeId(nextNode.id);
						vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
					} else {
						vehicle.setOriginNodeId(vehicle.getDestinationNode().id);
						vehicle.setDestinationNodeId(nextNode.id);
						if (vehicle.getOnLaneNumber() > vehicle.getWay().lanes - 1)
							vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
					}
				} else {
					System.out.println("what???");
				}
			}
		}
	}
}