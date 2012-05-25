package org.uci.luci.interchange.Registry;

import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Vehicles.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class VehicleRegistry {
	private static int nextVinToGenerate = 0;
	private static Hashtable<String, Vehicle> vehicleHash = new Hashtable<String, Vehicle>();

	public static void registerVehicle(Vehicle v) {
		v.vin = nextVinToGenerate;
		vehicleHash.put(nextVinToGenerate + "", v);
		nextVinToGenerate++;
	}

	public static void deregisterVehicle(Vehicle v) {
		vehicleHash.remove(v.vin + "");
	}

	public static Vehicle getVehicle(String vin) {
		return vehicleHash.get(vin);
	}

	public static List<Vehicle> allRegisteredVehicles() {
		return new ArrayList<Vehicle>(vehicleHash.values());
	}

	public static void reset() {
		vehicleHash.clear();
		nextVinToGenerate = 0;
	}
}