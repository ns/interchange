package org.uci.luci.interchange;

import java.util.Random;
import java.util.List;

public class VehicleDriverFactory {
 public static VehicleDriver createVehicleDriver(Vehicle v) {
   VehicleDriver d = new VehicleDriver(v);
   VehicleDriverRegistry.registerDriver(d);
   return d;
	}
}