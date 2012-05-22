package org.uci.luci.interchange.Driver.VehicleDriverBehavior;

import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Driver.*;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Intersections.*;

public class IntersectionCrossingBehavior implements VehicleDriverBehavior {
  private VehicleDriver vehicleDriver;
  private Vehicle vehicle;
  double speed = 25 * 1.609344; // km/h (30mph)
  // double speed = Vehicle.DISTANCE_TO_CONSIDER_AS_SAME/100;
  
  public IntersectionCrossingBehavior(VehicleDriver d) {
    vehicleDriver = d;
    vehicle = vehicleDriver.vehicle;
  }
  
  public void tick() {
    // System.out.println("IntersectionCrossingBehavior tick");
    Intersection i = vehicle.getNextIntersection();
    double d2I = vehicle.getDistanceToNextIntersection();

    if (i == null) {
      System.out.println("we're near an intersection but we don't know which yet @_@.");
      vehicle.setVelocity(speed/4);
    }
    else {
      // we're at the intersection
      int light = i.getLightForWayOnLane(null, vehicle.getOriginNode().id, vehicle.getOnLaneNumber());
      // System.out.println("i = " + i.id + " getLightForWayOnLane() = " + light);
      
      // green
      if (light == 0) {
        // System.out.println("Green");
        vehicle.setVelocity(speed);
      }
      // yellow
      else if (light == 1) {
        vehicle.setVelocity(speed);
      }
      // red and we're 
      else if (light == 2 && d2I > 0.00005) {
        vehicle.setVelocity(speed);
      }
      else if (light == 2 && d2I <= 0.00005) {
        vehicle.setVelocity(0);
      }
      else {
        System.out.println("uh.. what?");
      }
      
      
      if (d2I <= Vehicle.DISTANCE_TO_CONSIDER_AS_SAME) {
        
        Node nextNode = vehicleDriver.navigation.nextNodeOnPath(vehicle.getDestinationNode().id);
        
        if (nextNode != null) {
          
          vehicle.setOriginNodeId(vehicle.getDestinationNode().id);
          vehicle.setDestinationNodeId(nextNode.id);
          
          if (i.isLeftTurn(vehicle.getOriginNode().id, nextNode.id)) {
            vehicle.setOnLaneNumber(0);
          }
          else if (i.isRightTurn(vehicle.getOriginNode().id, nextNode.id)) {
            vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
          }
          
          if (vehicle.getOnLaneNumber() > vehicle.getWay().lanes - 1)
            vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
          
        }
        else {
          // vehicle.setVelocity(0);
          // vehicle.pause();
          // vehicle.flagForRemoval = true;
          // System.out.println(vehicle.vin + ": Vehicle has reached dest");
          // VehicleDriverFactory.destroyVehicleDriver(this);
          // VehicleFactory.destroyVehicle(vehicle);
        }
        
        // System.out.println("--- o --- > " + vehicle.getOriginNode().id);
        // System.out.println("--- o --- > " + nextNode.id);
        
        // Global.simulator.pause();
        // System.out.println("----------------------");
      }
    }
  }
}