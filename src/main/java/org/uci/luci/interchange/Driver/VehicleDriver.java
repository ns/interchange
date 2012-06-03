package org.uci.luci.interchange.Driver;

import org.uci.luci.interchange.Util.Utils;
import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Intersections.*;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Driver.VehicleDriverBehavior.*;
import org.uci.luci.interchange.Registry.*;
import org.uci.luci.interchange.Exception.*;

import java.util.ArrayList;

public class VehicleDriver {
	public Vehicle vehicle;
	public int licence;
	String nearbyIntersectionId = null;
	private String state;
	public VehicleDriverBehavior activeBehavior;
	private int rushedness = -1;
	public int spawnedAtPercentRushedness = -1;
	public int driverGroup = -1;

	public VehicleDriver(Vehicle vehicle) {
		this.vehicle = vehicle;
		setState("general_acceleration");
	}

	public void setRushedness(int r) {
		rushedness = r;
	}

	public int rushedness() {
		return rushedness;
	}

	public void pickRandomDestinationAndGo()
			throws NoPathToDestinationException {
		vehicle.navigation = new DestinationNavigation(
				vehicle.getOriginNode().id);
		vehicle.setOriginNodeId(vehicle.getOriginNode().id);
		vehicle.setDestinationNodeId(vehicle.navigation.nextNodeOnPath(vehicle
				.getOriginNode().id).id);
	}

	public void setDestinationAndGo(String destinationNodeId)
			throws NoPathToDestinationException {
		vehicle.navigation = new DestinationNavigation(
				vehicle.getOriginNode().id, destinationNodeId);
		vehicle.setOriginNodeId(vehicle.getOriginNode().id);
		vehicle.setDestinationNodeId(vehicle.navigation.nextNodeOnPath(vehicle
				.getOriginNode().id).id);
	}

	private void processIntersectionEvents(double tickLength) {
		// too far away from intersection
		if (!isNearIntersection()) {
			if (nearbyIntersectionId != null) {
				Intersection i = IntersectionRegistry
						.getIntersection(nearbyIntersectionId);
				nearbyIntersectionId = null;
				i.vehicleIsLeaving(vehicle);
			}
		}
		// we're close to an intersection
		else {
			Intersection i = vehicle.getNextIntersection();
			if (i.id.equals(nearbyIntersectionId)) {
				// ignore
				
				// stats
				if (!vehicle.paused() && vehicle.speed() == 0 && vehicle.getNodeAfterNextIntersection() != null) {
					if (i.isLeftTurn(vehicle.getOriginNode().id,
							vehicle.getNodeAfterNextIntersection().id)) {
          		vehicle.vehicleTotalStoppedTimeAtLeft += tickLength;
					} else if (i.isRightTurn(vehicle.getOriginNode().id,
							vehicle.getNodeAfterNextIntersection().id)) {
        		vehicle.vehicleTotalStoppedTimeAtRight += tickLength;
					} else {
        		vehicle.vehicleTotalStoppedTimeAtThrough += tickLength;
					}
				}
			} else {
				if (nearbyIntersectionId != null) {
					Intersection ii = IntersectionRegistry
							.getIntersection(nearbyIntersectionId);
					nearbyIntersectionId = null;
					ii.vehicleIsLeaving(vehicle);
				}

				nearbyIntersectionId = i.id;

				V2IMessage msg = new V2IMessage();

				if (rushedness == -1)
					msg.bid = Utils.randomNumberGenerator().nextInt(101);
				else
					msg.bid = rushedness;

				if (vehicle.getNodeAfterNextIntersection() != null) {
					i.vehicleIsApproaching(vehicle, vehicle.getOriginNode().id,
							vehicle.getNodeAfterNextIntersection().id,
							vehicle.getOnLaneNumber(), msg);

					if (i.isLeftTurn(vehicle.getOriginNode().id,
							vehicle.getNodeAfterNextIntersection().id)) {
						vehicle.leftTurnsMade++;
					} else if (i.isRightTurn(vehicle.getOriginNode().id,
							vehicle.getNodeAfterNextIntersection().id)) {
						vehicle.rightTurnsMade++;
					} else {
						vehicle.throughsMade++;
					}
				} else {
					i.vehicleIsApproaching(vehicle, vehicle.getOriginNode().id,
							null, vehicle.getOnLaneNumber(), msg);
				}
			}
		}

		if (vehicle.hasArrivedAtDestination() && nearbyIntersectionId != null) {
			Intersection i = IntersectionRegistry
					.getIntersection(nearbyIntersectionId);
			nearbyIntersectionId = null;
			i.vehicleIsLeaving(vehicle);
		}
	}

	private boolean isNearIntersection() {
		double d2I = vehicle.getDistanceToNextIntersection();
		double minDistToIntersection = 0.0762; // 250 ft in km
		return d2I != -1 && d2I < minDistToIntersection;
	}

	private boolean isNearVehicle() {
		double d2V = vehicle.getDistanceToVehicleInFront();
		return d2V != -1;
	}

	private void handleLaneSwitching() {
		if (vehicle.hasArrivedAtDestination())
			return;

		Intersection ii = vehicle.getNextIntersection();
		if (ii != null) {
			Node nextNode = vehicle.navigation.nextNodeOnPath(vehicle
					.getDestinationNode().id);
			if (nextNode != null) {
				if (ii.isLeftTurn(vehicle.getOriginNode().id, nextNode.id)) {
					// car needs to be in left lane

					if (vehicle.getOnLaneNumber() != 0) {
						// here we should check if there's a vehicle on the left
						if (!vehicle.vehicleOnLeft()) {
							// move over to the left
							vehicle.setOnLaneNumber(vehicle.getOnLaneNumber() - 1);
						} else {
							System.out.println("Couldnt move vehicle to left");
						}
					}
				} else if (ii.isRightTurn(vehicle.getOriginNode().id,
						nextNode.id)) {
					// car needs to be in right lane
					if (vehicle.getOnLaneNumber() != vehicle.getWay().lanes - 1) {
						if (vehicle.getOnLaneNumber() > vehicle.getWay().lanes - 1) {
							System.out
									.println("this should not have happened.");
							// vehicle.pause();
							// TODO: look into this
							vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
						}

						// here we should check if there's a vehicle on the left
						if (!vehicle.vehicleOnRight()) {
							// move over to the right
							vehicle.setOnLaneNumber(vehicle.getOnLaneNumber() + 1);
						} else {
							System.out.println("Couldnt move vehicle to right");
						}
					}
				}
			}
		}
	}

	private void determineBehavior() {
		boolean isNearIntersection = isNearIntersection();
		boolean isNearVehicle = isNearVehicle();

		// no intersection/too far away from intersection AND no vehicle in
		// front
		if (!isNearIntersection && !isNearVehicle) {
			setState("general_acceleration");
		} else if (isNearVehicle && isNearIntersection) {
			double d2V = vehicle.getDistanceToVehicleInFront();
			double d2I = vehicle.getDistanceToNextIntersection();

			if (d2V > d2I)
				setState("crossing_intersection");
			else
				setState("following_vehicle");
		} else if (isNearVehicle) {
			setState("following_vehicle");
		} else if (isNearIntersection) {
			setState("crossing_intersection");
		}

		// check if we're at the destination
		if (vehicle.hasArrivedAtDestination()) {
			setState("reached_destination");
		}
	}

	// this is called per-simulator tick which currently represents 1sec
	// don't do anything that will slow down the simulator in here
	public void tick(double simTime, double tickLength, int tick) {
		// this *must* be called first see vehicle.tick() for more info
		vehicle.tick(simTime, tickLength, tick);
		determineBehavior();
		handleLaneSwitching();
		behave(simTime, tickLength);
		processIntersectionEvents(tickLength);
	}

	public String getState() {
		return state;
	}

	public boolean stateEquals(String s) {
		if (state == null)
			return false;
		return state.equals(s);
	}

	private void setState(String s) {
		if (state != null && state.equals(s))
			return;
		state = s;
		if (s.equals("general_acceleration")) {
			activeBehavior = new GeneralAccelerationBehavior(this);
		} else if (s.equals("following_vehicle")) {
			activeBehavior = new FollowingBehavior(this);
		} else if (s.equals("crossing_intersection")) {
			activeBehavior = new IntersectionCrossingBehavior(this);
		} else if (s.equals("reached_destination")) {
			activeBehavior = new ReachedDestinationBehavior(this);
		}
	}

	private void behave(double simTime, double tickLength) {
		activeBehavior.tick(simTime, tickLength);
	}
}