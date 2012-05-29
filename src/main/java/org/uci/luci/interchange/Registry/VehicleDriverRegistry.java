package org.uci.luci.interchange.Registry;

import org.uci.luci.interchange.Driver.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class VehicleDriverRegistry {
	private static int nextLicenseToGenerate = 0;
	private static Hashtable<String, VehicleDriver> driverHash = new Hashtable<String, VehicleDriver>();

	public static void registerDriver(VehicleDriver d) {
		d.licence = nextLicenseToGenerate;
		driverHash.put(nextLicenseToGenerate + "", d);
		nextLicenseToGenerate++;
	}

	public static void deregisterDriver(VehicleDriver d) {
		driverHash.remove(d.licence + "");
	}

	public static VehicleDriver getDriver(Integer licenseNumber) {
		return driverHash.get(licenseNumber + "");
	}

	public static List<VehicleDriver> allLicensedDrivers() {
		return new ArrayList<VehicleDriver>(driverHash.values());
	}

	public static void reset() {
		driverHash.clear();
		nextLicenseToGenerate = 0;
	}
}