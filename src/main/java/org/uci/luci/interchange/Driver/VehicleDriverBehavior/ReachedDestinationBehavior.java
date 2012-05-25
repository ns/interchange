package org.uci.luci.interchange.Driver.VehicleDriverBehavior;

import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Driver.*;

public class ReachedDestinationBehavior implements VehicleDriverBehavior {
	private VehicleDriver vehicleDriver;
	private Vehicle vehicle;

	public ReachedDestinationBehavior(VehicleDriver d) {
		vehicleDriver = d;
		vehicle = vehicleDriver.vehicle;
	}

	public void tick(double simTime, double tickLength) {
		System.out.println("reached destination setting velocity 0");
		vehicle.setAcceleration(Vehicle.MAX_NEG_ACCELERATION);
	}

	public String state() {
		return "STOPPING";
	}
}