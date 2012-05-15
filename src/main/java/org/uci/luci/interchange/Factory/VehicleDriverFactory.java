package org.uci.luci.interchange.Factory;

import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Registry.*;
import org.uci.luci.interchange.Driver.*;

import java.util.Random;
import java.util.List;

public class VehicleDriverFactory {
	public static VehicleDriver createVehicleDriver(Vehicle v) {
		VehicleDriver d = new VehicleDriver(v);
		VehicleDriverRegistry.registerDriver(d);
		return d;
	}
	
	public static void destroyVehicleDriver(VehicleDriver d) {
	  VehicleDriverRegistry.deregisterDriver(d);
	}
}