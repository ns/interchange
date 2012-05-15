package org.uci.luci.interchange.Driver.VehicleDriverBehavior;

import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Driver.*;

public class GeneralAccelerationBehavior implements VehicleDriverBehavior {
  private VehicleDriver vehicleDriver;
  private Vehicle vehicle;
  double speed = Vehicle.DISTANCE_TO_CONSIDER_AS_SAME/100;
  
  public GeneralAccelerationBehavior(VehicleDriver d) {
    vehicleDriver = d;
    vehicle = vehicleDriver.vehicle;
  }
  
  public void tick() {
    vehicle.setVelocity(speed);
  }
}