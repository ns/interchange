package org.uci.luci.interchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleRegistry {
  private static int nextVinToGenerate = 0;
	private static HashMap<Integer, Vehicle> vehicleHash = new HashMap<Integer, Vehicle>();
  
  public static void registerVehicle(Vehicle v) {
    v.vin = nextVinToGenerate;
    vehicleHash.put(nextVinToGenerate, v);
    nextVinToGenerate++;
  }
  
  public static void deregisterVehicle(Vehicle v) {
    vehicleHash.remove(v.vin);
  }
  
  public static Vehicle getVehicle(Integer vin) {
    return vehicleHash.get(vin);
  }
  
  public static List<Vehicle> allRegisteredVehicles() {
	  return new ArrayList<Vehicle>(vehicleHash.values());
	}
}