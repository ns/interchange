package org.uci.luci.interchange;

public class LaneChangingBehavior implements VehicleDriverBehavior {
  private VehicleDriver vehicleDriver;
  private Vehicle vehicle;
  
  public LaneChangingBehavior(VehicleDriver d) {
    vehicleDriver = d;
    vehicle = vehicleDriver.vehicle;
  }
  
  public void tick() {
    // Intersection ii = vehicle.getNextIntersection();
    // if (ii != null) {
    //   Node nextNode = navigation.nextNodeOnPath(vehicle.getDestinationNode().id);
    //   if (nextNode != null) {
    //     if (ii.isLeftTurn(vehicle.getOriginNode().id, nextNode.id)) {
    //       // car needs to be in left lane
    //       
    //       vehicle.preparingFor = "left";
    //       
    //       if (vehicle.getOnLaneNumber() != vehicle.getWay().lanes - 1) {
    //         System.out.println(vehicle.vin + ": need to switch lanes to the left " + vehicle.getOnLaneNumber() + " (total lanes = " + (vehicle.getWay().lanes - 1) + ")");
    //         
    //         // here we should check if there's a vehicle on the left
    //         if (vehicle.getOnLaneNumber() > vehicle.getWay().lanes - 1)
    //           vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
    //         else {
    //           if (!vehicle.vehicleOnRight()) {
    //             // move over to the right
    //             vehicle.setOnLaneNumber(vehicle.getOnLaneNumber()+1);
    //           }
    //           else {
    //             System.out.println("Couldnt move vehicle to left");
    //           }
    //         }
    //       }
    //       
    //       
    //       // if (vehicle.getOnLaneNumber() != 0) {
    //       //   System.out.println(vehicle.vin + ": need to switch lanes to the left "  + vehicle.getOnLaneNumber() + " (total lanes = " + (vehicle.getWay().lanes - 1) + ")");
    //       //   
    //       //   if (!vehicle.vehicleOnLeft()) {
    //       //     // move over to the left
    //       //     vehicle.getOnLaneNumber()--;
    //       //   }
    //       //   else {
    //       //     System.out.println("Couldnt move vehicle to left");
    //       //   }
    //       // }
    //     }
    //     else if (ii.isRightTurn(vehicle.getOriginNode().id, nextNode.id)) {
    //       vehicle.preparingFor = "right";
    //       // car needs to be in right lane
    //       if (vehicle.getOnLaneNumber() != vehicle.getWay().lanes - 1) {
    //         System.out.println(vehicle.vin + ": need to switch lanes to the right " + vehicle.getOnLaneNumber() + " (total lanes = " + (vehicle.getWay().lanes - 1) + ")");
    //         
    //         // here we should check if there's a vehicle on the left
    //         if (vehicle.getOnLaneNumber() > vehicle.getWay().lanes - 1)
    //           vehicle.setOnLaneNumber(vehicle.getWay().lanes - 1);
    //         else {
    //           if (!vehicle.vehicleOnRight()) {
    //             // move over to the right
    //             vehicle.setOnLaneNumber(vehicle.getOnLaneNumber()+1);
    //           }
    //           else {
    //             System.out.println("Couldnt move vehicle to right");
    //           }
    //         }
    //       }
    //     }
    //   }
    // }
  }
}