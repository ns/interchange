package org.uci.luci.interchange.Driver.VehicleDriverBehavior;

import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Driver.*;

public class FollowingBehavior implements VehicleDriverBehavior {
  private VehicleDriver vehicleDriver;
  private Vehicle vehicle;
  double speed = 25 * 1.609344; // km/h (30mph)
  // double speed = Vehicle.DISTANCE_TO_CONSIDER_AS_SAME/100;
  
  public FollowingBehavior(VehicleDriver d) {
    vehicleDriver = d;
    vehicle = vehicleDriver.vehicle;
  }
  
  public void tick() {
      double d = vehicle.getDistanceToVehicleInFront();
      if (d == -1 || d > 0.00005) {
        vehicle.setVelocity(speed);
      }
      else {
        vehicle.setVelocity(0);
      }
  }
}