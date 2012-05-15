package org.uci.luci.interchange;

import java.util.ArrayList;

// the driver looks at the gauges in the vehicle and actuates the car's
// velocity based on relevant factors
public class VehicleDriver {
  Vehicle vehicle;
  Navigation navigation;
  
  int licence;
  double speed = 0.000001;
  double minSpeed = 0;
  double maxSpeed = 0.000002;
  double minimum_follow_distance = 10;
  double maximum_follow_distance = 20;
  
  String nearbyIntersectionId = null;
  
  public VehicleDriver(Vehicle vehicle) {
    this.vehicle = vehicle;
    configureDriverBehavior();
    // this.navigation = new Navigation(vehicle.getOriginNode().id);
  }
  
  public void navTick() {
    // if (vehicle.state.equals("reached_intersection")) {
    //   System.out.println("we have to do something!");
    // }
  }
  
  public void pickRandomDestinationAndGo() throws NoPathToDestinationException {
    this.navigation = new Navigation(vehicle.getOriginNode().id);
    vehicle.setDestinationNodeId(navigation.nextNodeOnPath(vehicle.getOriginNode().id).id);
  }
  
  public void setDestinationAndGo(String destinationNodeId) throws NoPathToDestinationException {
    navigation = new Navigation(vehicle.getOriginNode().id, destinationNodeId);
    vehicle.setDestinationNodeId(navigation.nextNodeOnPath(vehicle.getOriginNode().id).id);
  }
  
  private void processIntersectionEvents() {
    double d2I = vehicle.getDistanceToNextIntersection();
    double minDistToIntersection = 0.0002;
    
    // too far away from intersection
    if (d2I == -1 || d2I > minDistToIntersection) {
      if (nearbyIntersectionId != null) {
        Intersection i = IntersectionRegistry.getIntersection(nearbyIntersectionId);
        nearbyIntersectionId = null;
        i.vehicleIsLeaving(vehicle);
      }
    }
    // we're close to an intersection
    else {
      Intersection i = vehicle.getNextIntersection();
      if (i.id.equals(nearbyIntersectionId)) {
        // ignore
      }
      else {
        nearbyIntersectionId = i.id;
        i.vehicleIsApproaching(vehicle);
      }
    }
  }
  
  private boolean isNearIntersection() {
    double d2I = vehicle.getDistanceToNextIntersection();
    double minDistToIntersection = 0.0002;
    return d2I != -1 && d2I < minDistToIntersection;
  }
  
  private boolean isNearVehicle() {
    double d2V = vehicle.getDistanceToVehicleInFront();
    return d2V != -1;
  }
  
  String preparingFor = "";
  private void actuateVelocity() {
    processIntersectionEvents();
    
    boolean isNearIntersection = isNearIntersection();
    boolean isNearVehicle = isNearVehicle();
    
    // no intersection/too far away from intersection AND no vehicle in front
    if (!isNearIntersection && !isNearVehicle) {
      setState("general_acceleration");
    }
    else if (isNearVehicle && isNearIntersection) {
      double d2V = vehicle.getDistanceToVehicleInFront();
      double d2I = vehicle.getDistanceToNextIntersection();
      
      if (d2V > d2I)
        setState("crossing_intersection");
      else
        setState("following_vehicle");
    }
    else if (isNearVehicle) {
      setState("following_vehicle");
    }
    else if (isNearIntersection) {
      setState("crossing_intersection");
    }
    
    
    
    // check if we're at the destination
    
    
    
    // Node dest = Global.openStreetMap.getNode(navigation.getDestination());
    // double dist = Math.sqrt(Math.pow(vehicle.lat-dest.lat,2)+Math.pow(vehicle.lon-dest.lon,2));
    // if (dist < Vehicle.DISTANCE_TO_CONSIDER_AS_SAME && vehicle.getDestinationNode().id.equals(navigation.getDestination())) {
    //   System.out.println("reached_destination");
    //   setState("reached_destination");
    //   System.out.println()
    // }
    
    if (vehicle.getDestinationNode().id.equals(navigation.getDestination())) {
      if (vehicle.isAtDestinationNode()) {
        setState("reached_destination");
      }
      // else {
      //   System.out.format("almost dest (%.8f)", vehicle.distanceToDestinationNode());
      //   System.out.println();
      // }
    }
    else if (vehicle.getOriginNode().id.equals(navigation.getDestination())) {
      if (vehicle.isAtOriginNode()) {
        setState("reached_destination");
      }
      // else {
      //   System.out.format("almost dest (%.8f)", vehicle.distanceFromOriginNode());
      //   System.out.println();
      // }
    }
    
    
    
    Intersection ii = vehicle.getNextIntersection();
    if (ii != null) {
      Node nextNode = navigation.nextNodeOnPath(vehicle.getDestinationNode().id);
      if (nextNode != null) {
        if (ii.isLeftTurn(vehicle.getOriginNode().id, nextNode.id)) {
          // car needs to be in left lane
          
          vehicle.preparingFor = "left";
          
          if (vehicle.getOnLaneNumber() != vehicle.getWay().lanes - 1) {
            System.out.println(vehicle.vin + ": need to switch lanes to the left " + vehicle.getOnLaneNumber() + " (total lanes = " + (vehicle.getWay().lanes - 1) + ")");
            
            // here we should check if there's a vehicle on the left
            if (vehicle.getOnLaneNumber() > vehicle.getWay().lanes - 1)
              vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
            else {
              if (!vehicle.vehicleOnRight()) {
                // move over to the right
                vehicle.setOnLaneNumber(vehicle.getOnLaneNumber()+1);
              }
              else {
                System.out.println("Couldnt move vehicle to left");
              }
            }
          }
        }
        else if (ii.isRightTurn(vehicle.getOriginNode().id, nextNode.id)) {
          vehicle.preparingFor = "right";
          // car needs to be in right lane
          if (vehicle.getOnLaneNumber() != vehicle.getWay().lanes - 1) {
            System.out.println(vehicle.vin + ": need to switch lanes to the right " + vehicle.getOnLaneNumber() + " (total lanes = " + (vehicle.getWay().lanes - 1) + ")");
            
            // here we should check if there's a vehicle on the left
            if (vehicle.getOnLaneNumber() > vehicle.getWay().lanes - 1)
              vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
            else {
              if (!vehicle.vehicleOnRight()) {
                // move over to the right
                vehicle.setOnLaneNumber(vehicle.getOnLaneNumber()+1);
              }
              else {
                System.out.println("Couldnt move vehicle to right");
              }
            }
          }
        }
      }
    }
    
    vehicle.preparingFor = "";
  }
  
  // this is called per-simulator tick which currently represents 1sec
  // don't do anything that will slow down the simulator in here
  public void tick(int tick) {
    // this *must* be called first see vehicle.tick() for more info
    vehicle.tick(tick);
    
    // here we make any changes to the vehicles meta-navigation system
    // e.g. change destination, choose alternate routes, calculate expected delay, etc
    navTick();
    
    // here we determine the velocity of the vehicle based on a number of factors
    actuateVelocity();
    
    behave();
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  private String state;
  VehicleDriverBehavior activeBehavior;
  
  public String getState() {
    return state;
  }
  
  public boolean stateEquals(String s) {
    if (state == null)
      return false;
    return state.equals(s);
  }
  
  private void setState(String s) {
    state = s;
    if (s.equals("general_acceleration")) {
      activeBehavior = new GeneralAccelerationBehavior(this);
    }
    else if (s.equals("following_vehicle")) {
      activeBehavior = new FollowingBehavior(this);
    }
    else if (s.equals("crossing_intersection")) {
      activeBehavior = new IntersectionCrossingBehavior(this);
    }
    else if (s.equals("reached_destination")) {
      activeBehavior = new ReachedDestinationBehavior(this);
    }
  }
  
  private void configureDriverBehavior() {
    setState("general_acceleration");
  }
  
  private void behave() {
    activeBehavior.tick();
  }
}