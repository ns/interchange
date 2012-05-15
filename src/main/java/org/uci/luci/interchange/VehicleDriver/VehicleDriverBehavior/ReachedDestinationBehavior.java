package org.uci.luci.interchange;

public class ReachedDestinationBehavior implements VehicleDriverBehavior {
  private VehicleDriver vehicleDriver;
  private Vehicle vehicle;
  
  public ReachedDestinationBehavior(VehicleDriver d) {
    vehicleDriver = d;
    vehicle = vehicleDriver.vehicle;
  }
  
  public void tick() {
    vehicle.setVelocity(0);
  }
}