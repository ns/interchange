package org.uci.luci.interchange;

import java.util.ArrayList;

public class Way {
    public ArrayList<String> nd = new ArrayList<String>();
    public ArrayList<Tag> tags = new ArrayList<Tag>();
    
    public void addNode(String nodeId){
    	nd.add(nodeId);
    }
    
    public void addTag(Tag t){
       	tags.add(t);
    }
    
    public int getNdSize(){
    	return nd.size();
    }
    
    public int getTagsSize(){
    	return tags.size();
    }
    
    public boolean hasTag(String k, String v) {
      for (Tag t : tags) {
        if (t.k.equals(k) && t.v.equals(v))
          return true;
      }
      return false;
    }
    
    public boolean hasTag(String k) {
      for (Tag t : tags) {
        if (t.k.equals(k))
          return true;
      }
      return false;
    }
    
    public String getName() {
      for (Tag t : tags) {
        if (t.k.equals("name"))
          return t.v;
      }
      return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // simulator related
    public ArrayList<Vehicle> vehiclesTraversing = new ArrayList<Vehicle>();
    
    public void tick() {
      // for (Vehicle v : vehiclesTraversing) {
      //   double velocity = v.velocity;
      //   
      //   // move by velocity in the right direction
      //   Node nextNode = calculateNextNodeForVehicle(v);
      //   
      //   
      //   
      // }
    }
    
    // private Node calculateNextNodeForVehicle(Vehicle v) {
    //   for (String nId : nd) {
    //   }
    // }
}
