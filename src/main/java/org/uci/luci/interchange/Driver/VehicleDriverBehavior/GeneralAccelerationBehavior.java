package org.uci.luci.interchange.Driver.VehicleDriverBehavior;

import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Driver.*;
import org.uci.luci.interchange.Graph.*;

public class GeneralAccelerationBehavior implements VehicleDriverBehavior {
	private VehicleDriver vehicleDriver;
	private Vehicle vehicle;

	public GeneralAccelerationBehavior(VehicleDriver d) {
		vehicleDriver = d;
		vehicle = vehicleDriver.vehicle;
	}

	public String state() {
		return "Accel to speed limit";
	}

	public void tick(double simTime, double tickLength) {
		Way w = vehicle.getWay();

		if (w == null) {
			System.out.println("vehicle " + vehicle.vin + " has no way");
		}

		double speedLimit = vehicle.getWay().getSpeedLimit();
		vehicle.setAcceleration(VehicleUtils.determineNecessaryAcceleration(
				vehicle.speed(), speedLimit, 0.024384));
	}
}