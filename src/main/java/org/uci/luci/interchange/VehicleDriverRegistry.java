package org.uci.luci.interchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleDriverRegistry {
  private static int nextLicenseToGenerate = 0;
	private static HashMap<Integer, VehicleDriver> driverHash = new HashMap<Integer, VehicleDriver>();
  
  public static void registerDriver(VehicleDriver d) {;
    driverHash.put(nextLicenseToGenerate, d);
    nextLicenseToGenerate++;
  }
  
  public static VehicleDriver getDriver(Integer licenseNumber) {
    return driverHash.get(licenseNumber);
  }
  
  public static List<VehicleDriver> allLicensedDrivers() {
	  return new ArrayList<VehicleDriver>(driverHash.values());
	}
}