package org.uci.luci.interchange.Driver.VehicleDriverBehavior;

import org.uci.luci.interchange.Util.Utils;
import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Driver.*;

public class FollowingBehavior implements VehicleDriverBehavior {
	private VehicleDriver vehicleDriver;
	private Vehicle vehicle;

	// double speed = Vehicle.DISTANCE_TO_CONSIDER_AS_SAME/100;

	public FollowingBehavior(VehicleDriver d) {
		vehicleDriver = d;
		vehicle = vehicleDriver.vehicle;
	}

	public String state() {
		return "Following";
	}

	public void tick(double simTime, double tickLength) {
		double d = vehicle.getDistanceToVehicleInFront();
		Vehicle vehicleInFront = vehicle.getVehicleInFront();
		try {
			if (d == -1 || d > 0.018288) { // 60 ft in km
				double speedLimit = vehicle.getWay().getSpeedLimit();
				vehicle.setAcceleration(VehicleUtils
						.determineNecessaryAcceleration(vehicle.speed(),
								speedLimit, d));
			} else {
				if (d < 0.009144) // 30 ft to km between cars
					vehicle.setAcceleration(Vehicle.MAX_NEG_ACCELERATION);
				else
					vehicle.setAcceleration(VehicleUtils
							.determineNecessaryAcceleration(vehicle.speed(),
									vehicleInFront.speed(), d - 0.009144));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}