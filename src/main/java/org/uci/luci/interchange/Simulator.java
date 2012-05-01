package org.uci.luci.interchange;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

public class Simulator {
	ArrayList<String> spawnPoints = new ArrayList<String>();
  
  public Simulator() throws InterruptedException {
    simulate();
  }
  
  public void simulate() throws InterruptedException {
    int tick = 0;
    int cars=0;
    while (true) {
      tick++;
      cars=0;
      
      if (tick%100 == 1) {
        Vehicle v = VehicleFactory.createVehicleAtNode(Global.openStreetMap.getNode("122633613"));
        // Vehicle v = VehicleFactory.createVehicleAtRandomPoint();
        VehicleDriver d = VehicleDriverFactory.createVehicleDriver(v);
      }
      
      long startTime = System.nanoTime();
      long endTime;
      
      // System.out.println("vehicles: tick");
      for (VehicleDriver d : VehicleDriverRegistry.allLicensedDrivers()) {
        d.tick();
        cars++;
      }
      
      for (Vehicle v : VehicleRegistry.allRegisteredVehicles()) {
        // each vehicle's velocity vector has been determined by now
        // we simply calculate exactly where the vehicle should be for
        // the next timestep
        
        if (v.velocity == null)
          continue;
        
        Node lastNode = v.getOriginNode();
        Node nextNode = v.getDestinationNode();
        
        double newLat = v.lat + v.velocity.x;
        double newLon = v.lon + v.velocity.y;
        
        v.lat = (double)newLat;
        v.lon = (double)newLon;
      }
      
      for (Intersection i : IntersectionRegistry.allRegisteredIntersections()) {
        i.tick();
      }
      
      endTime = System.nanoTime();
      long duration = endTime - startTime;
      Thread.sleep(5);
    }
  }
}