package org.uci.luci.interchange;

public class GeneralAccelerationBehavior implements VehicleDriverBehavior {
  private VehicleDriver vehicleDriver;
  private Vehicle vehicle;
  double speed = 0.000001;
  
  public GeneralAccelerationBehavior(VehicleDriver d) {
    vehicleDriver = d;
    vehicle = vehicleDriver.vehicle;
  }
  
  public void tick() {
    vehicle.setVelocity(speed);
  }
}