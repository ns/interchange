package org.uci.luci.interchange.Util;

public class MercatorProjection implements Projection {
  final private static double R_MAJOR = 6378137.0;
  final private static double R_MINOR = 6356752.3142;

  public double[] merc(double x, double y) {
      return new double[] {mercX(x), mercY(y)};
  }

  private double  mercX(double lon) {
      return R_MAJOR * Math.toRadians(lon);
  }

  private double mercY(double lat) {
      if (lat > 89.5) {
          lat = 89.5;
      }
      if (lat < -89.5) {
          lat = -89.5;
      }
      double temp = R_MINOR / R_MAJOR;
      double es = 1.0 - (temp * temp);
      double eccent = Math.sqrt(es);
      double phi = Math.toRadians(lat);
      double sinphi = Math.sin(phi);
      double con = eccent * sinphi;
      double com = 0.5 * eccent;
      con = Math.pow(((1.0-con)/(1.0+con)), com);
      double ts = Math.tan(0.5 * ((Math.PI*0.5) - phi))/con;
      double y = 0 - R_MAJOR * Math.log(ts);
      return y;
  }
  
  public double convertLongToX(double lng) {
    return mercX(lng);
  }
  
	public double convertLatToY(double lat) {
    return mercY(lat);
	}
	
  
  // public double[] convert(double lat, double lon) {
  //  return convert(lat, lon, earthRadiusKm);
  // }
  // 
  // public double[] convert(double lat, double lng, double radius) {
  //  double[] coords = {(lng - Global.minLon) * radius,Math.log(Math.tan(Math.PI / 4 + lat / 2)) * radius};
  //  return coords;
  // }
  // 
  // public double convertLongToX(double lng)
  // {
  //  return (lng - Global.minLon) * earthRadiusKm;
  // }
  // 
  // public double convertLatToY(double lat)
  // {
  //  return Math.log(Math.tan(Math.PI / 4 + lat / 2)) * earthRadiusKm;
  // }
}
