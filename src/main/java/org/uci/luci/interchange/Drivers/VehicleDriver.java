package org.uci.luci.interchange;

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
    vehicle.setDestinationNodeId(destinationNodeId);
  }
  
  String preparingFor = "";
  private void actuateVelocity() {
    
    vehicle.preparingFor = "";
    
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
          
          
          // if (vehicle.getOnLaneNumber() != 0) {
          //   System.out.println(vehicle.vin + ": need to switch lanes to the left "  + vehicle.getOnLaneNumber() + " (total lanes = " + (vehicle.getWay().lanes - 1) + ")");
          //   
          //   if (!vehicle.vehicleOnLeft()) {
          //     // move over to the left
          //     vehicle.getOnLaneNumber()--;
          //   }
          //   else {
          //     System.out.println("Couldnt move vehicle to left");
          //   }
          // }
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
    
    
    
    
    
    
    double d2I = vehicle.getDistanceToNextIntersection();
    
    // System.out.println("d2I = " + d2I);
    
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
    
    // too far away from intersection
    if (d2I == -1 || d2I > minDistToIntersection) {
      double d = vehicle.getDistanceToVehicleInFront();
      if (d == -1 || d > 0.00005) {
        vehicle.setVelocity(speed);
      }
      else {
        vehicle.setVelocity(0);
      }
    }
    // we're close to an intersection
    else {
      double d = vehicle.getDistanceToVehicleInFront();
      
      if (d == -1 || d > d2I) {
        // the intersection is closer
        Intersection i = vehicle.getNextIntersection();

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
            vehicle.setVelocity(speed);
          }
          // yellow
          else if (light == 1) {
            vehicle.setVelocity(d2I/20);
          }
          // red and we're 
          else if (light == 2 && d2I > 0.00005) {
            vehicle.setVelocity(d2I/20);
          }
          else if (light == 2 && d2I <= 0.00005) {
            vehicle.setVelocity(0);
          }
          else {
            System.out.println("uh.. what?");
          }
          
          
          if (d2I <= 0.00001) {
            
            Node nextNode = navigation.nextNodeOnPath(vehicle.getDestinationNode().id);
            
            if (nextNode != null) {
              
              vehicle.setOriginNodeId(vehicle.getDestinationNode().id);
              vehicle.setDestinationNodeId(nextNode.id);
              
              if (ii.isLeftTurn(vehicle.getOriginNode().id, nextNode.id)) {
                vehicle.setOnLaneNumber(0);
              }
              else if (ii.isRightTurn(vehicle.getOriginNode().id, nextNode.id)) {
                vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
              }
              
              if (vehicle.getOnLaneNumber() > vehicle.getWay().lanes - 1)
                vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
              
            }
            else {
              vehicle.setVelocity(0);
              vehicle.pause();
              vehicle.flagForRemoval = true;
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
      else {
        // a vehicle is closer
        if (d == -1 || d > 0.00005) {
          vehicle.setVelocity(speed);
        }
        else {
          vehicle.setVelocity(0);
        }
      }
    }
    
    // if (vehicle.state.equals("reached_intersection")) {
    //   // pick a random direction and go for now
    //   vehicle.setVelocity(0);
    //   
    //   // TODO: we either make this event-based and called in a method like
    //   // enteringIntersection(), enteredIntersection(), leavingIntersection(), leftIntersection()
    //   // or we handle it in here or in tick()
    //   // we need an intersection class to handle the actual intersection mechanics
    // }
    // else {
    //   double d = vehicle.getDistanceToVehicleInFront();
    //   if (d == -1 || d > 0.00005) {
    //     vehicle.setVelocity(speed);
    //   }
    //   else {
    //     vehicle.setVelocity(0);
    //   }
    // }
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
  }
}