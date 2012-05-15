package org.uci.luci.interchange;

public class FollowingBehavior implements VehicleDriverBehavior {
  private VehicleDriver vehicleDriver;
  private Vehicle vehicle;
  double speed = 0.000001;
  
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