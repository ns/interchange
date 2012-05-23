package org.uci.luci.interchange.Utils;

public class LatLng {
  private double lat, lng;
  
  public LatLng(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }
  
  public void add(double lat, double lng) {
    this.lat += lat;
    this.lng += lng;
  }
  
  public double lat() {
    return lat;
  }
  
  public double lng() {
    return lat;
  }
}