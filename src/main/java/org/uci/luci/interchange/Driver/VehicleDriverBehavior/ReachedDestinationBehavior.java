package org.uci.luci.interchange.Driver.VehicleDriverBehavior;

import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Driver.*;

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