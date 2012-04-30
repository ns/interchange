package org.uci.luci.interchange;

import java.util.ArrayList;

public class ThreeWayIntersection extends Intersection {
  public ThreeWayIntersection(String rootNodeId) {
    super(rootNodeId);
  }
  
  public void tick() {
  }
  
  public void vehicleIsApproaching(Vehicle v) {
    System.out.println("v " + v.vin + " is approaching " + id);
  }
  
  public void vehicleIsLeaving(Vehicle v) {
    System.out.println("v " + v.vin + " is leaving " + id);
  }
}
