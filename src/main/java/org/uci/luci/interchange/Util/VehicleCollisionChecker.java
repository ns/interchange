package org.uci.luci.interchange.Util;

import org.uci.luci.interchange.Vehicles.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleCollisionChecker {
	private static HashMap<String, ArrayList<Integer>> originNodesToVehicles = new HashMap<String, ArrayList<Integer>>();

	public static List<Vehicle> checkCollisions(List<Vehicle> vehicles) {
		ArrayList<Vehicle> collisions = new ArrayList<Vehicle>();

		for (int i = 0; i < vehicles.size(); i++) {
			Vehicle v = vehicles.get(i);

			for (int ii = 0; ii < vehicles.size(); ii++) {
				if (i == ii)
					continue;

				Vehicle vv = vehicles.get(ii);

				if (v.isCollidingWith(vv)) {
					collisions.add(v);
					collisions.add(vv);
				}
			}
		}

		return collisions;
	}
}
