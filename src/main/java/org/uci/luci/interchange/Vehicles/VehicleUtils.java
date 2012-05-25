package org.uci.luci.interchange.Vehicles;

import java.util.List;

import org.uci.luci.interchange.Registry.VehicleRegistry;
import org.uci.luci.interchange.Util.Oracle;

public class VehicleUtils {
	public static Vehicle findVehicleClosestToOriginNodeOnLane(
			String originNodeId, String destinationNodeId, int onLaneNumber) {
		List<String> vehicles = Oracle.vehiclesWithNodeAsOrigin(originNodeId);

		if (vehicles == null || vehicles.isEmpty()) {
			return null;
		} else {
			Vehicle vehicle = null;
			for (String vin : vehicles) {
				Vehicle v = VehicleRegistry.getVehicle(vin);
				if (v == null) {
					// System.out.println("2: vehicle with VIN " + vin +
					// " is null (i am vehicle " + this.vin +
					// ") (node "+originNodeId+")");
					continue;
				}
				if (!v.getDestinationNode().id.equals(destinationNodeId)
						|| v.getOnLaneNumber() != onLaneNumber)
					continue;
				if (vehicle == null
						|| v.getNodeTraversingMehanism()
								.distanceFromOriginNode(v.lat, v.lon) < vehicle
								.getNodeTraversingMehanism()
								.distanceFromOriginNode(vehicle.lat,
										vehicle.lon))
					vehicle = v;
			}
			return vehicle;
		}
	}

	// vf^2 = vi^2 + 2ad
	public static double determineNecessaryAcceleration(double initialSpeed,
			double finalSpeed, double distance) {
		if (distance == 0 || Double.isNaN(distance))
			return Double.MAX_VALUE;
		double a = ((Math.pow(finalSpeed, 2) - Math.pow(initialSpeed, 2)) / (2 * distance)) / 3600;
		if (Double.isNaN(a)) {
			System.out.println("params is = " + initialSpeed + " finalSpeed = "
					+ finalSpeed + " distance = " + distance);
		}
		return a;
	}
}
