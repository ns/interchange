package org.uci.luci.interchange.Util;

import org.uci.luci.interchange.Graph.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Utils {
  private static Random randomNumberGenerator;
  
  public static Random randomNumberGenerator() {
    if (randomNumberGenerator == null)
      randomNumberGenerator = new Random();
    return randomNumberGenerator;
  }
  
  public static double angleBetweenNodesWithCenterNode(Node centerNode, Node node1, Node node2) {
    double angle = Utils.angleBetween2Lines(centerNode.lon, centerNode.lat, node1.lon, node1.lat,
                                            centerNode.lon, centerNode.lat, node2.lon, node2.lat);
    // System.out.println("\tangle = " + Math.toDegrees(angle));
    return angle;
  }
  
	public static double radians(double n) {
		return n * Math.PI / 180;
	}

	public static List<Node> findNodesWithAngleBetweenClosestTo180(Node centerNode, List<Node> nodes) {
		Node selectedNode1 = null, selectedNode2 = null;
		double angleBetweenSelectedNodes = Double.MAX_VALUE;

		for (int i = 0; i < nodes.size(); i++) {
			for (int ii = 0; ii < nodes.size(); ii++) {
				if (i == ii)
					continue;

				Node node1 = nodes.get(i);
				Node node2 = nodes.get(ii);

				double angle = Utils.angleBetween2Lines(centerNode.lon, centerNode.lat, node1.lon, node1.lat,
						centerNode.lon, centerNode.lat, node2.lon, node2.lat);

				// System.out.println("\tangle = " + Math.toDegrees(angle));

				if (angleBetweenSelectedNodes == Double.MAX_VALUE || Math.abs(180-Math.toDegrees(angleBetweenSelectedNodes))>Math.abs(180-Math.toDegrees(angle))) {
					selectedNode1 = node1;
					selectedNode2 = node2;
					angleBetweenSelectedNodes = angle;
					continue;
				}
			}
		}

		// System.out.println("picked angle = ");

		return Arrays.asList(selectedNode1, selectedNode2);
	}

	public static double angleBetween2Lines(double l1x1, double l1y1, double l1x2, double l1y2,
			double l2x1, double l2y1, double l2x2, double l2y2) {
		double angle1 = Math.atan2(l1y1 - l1y2,
				l1x1 - l1x2);
		double angle2 = Math.atan2(l2y1 - l2y2,
				l2x1 - l2x2);
		return angle1-angle2;
	}
	
	//Returns Distance between two lat/long coords
	// - lat1
	// - lng1
	// - lat2
	// - lng2
	// - conv - Unit Conversion
	// 		  - 1 = Miles
	// 		  - 2 = Meters
	// 		  - 3 = Kilometers

	public static float distFrom(double lat1, double lng1, double lat2, double lng2, int conv) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *	Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		if(conv == 2)
			dist *= 1609;
		else if(conv == 3)
			dist *= 1.609;
		return new Float(dist).floatValue();
	}
	
	
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
  /*::                                                                         :*/
  /*::  This routine calculates the distance between two points (given the     :*/
  /*::  latitude/longitude of those points). It is being used to calculate     :*/
  /*::  the distance between two ZIP Codes or Postal Codes using our           :*/
  /*::  ZIPCodeWorld(TM) and PostalCodeWorld(TM) products.                     :*/
  /*::                                                                         :*/
  /*::  Definitions:                                                           :*/
  /*::    South latitudes are negative, east longitudes are positive           :*/
  /*::                                                                         :*/
  /*::  Passed to function:                                                    :*/
  /*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
  /*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
  /*::    unit = the unit you desire for results                               :*/
  /*::           where: 'M' is statute miles                                   :*/
  /*::                  'K' is kilometers (default)                            :*/
  /*::                  'N' is nautical miles                                  :*/
  /*::  United States ZIP Code/ Canadian Postal Code databases with latitude & :*/
  /*::  longitude are available at http://www.zipcodeworld.com                 :*/
  /*::                                                                         :*/
  /*::  For enquiries, please contact sales@zipcodeworld.com                   :*/
  /*::                                                                         :*/
  /*::  Official Web site: http://www.zipcodeworld.com                         :*/
  /*::                                                                         :*/
  /*::  Hexa Software Development Center © All Rights Reserved 2004            :*/
  /*::                                                                         :*/
  /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

  public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    if (unit == 'K') {
      dist = dist * 1.609344;
    } else if (unit == 'N') {
    	dist = dist * 0.8684;
      }
    return (dist);
  }
  
  /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
  /*::  This function converts decimal degrees to radians             :*/
  /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
  private static double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
  /*::  This function converts radians to decimal degrees             :*/
  /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
  private static double rad2deg(double rad) {
    return (rad * 180.0 / Math.PI);
  }
  
  public static double kmToProjectionPts(double km, Node refNode1, Node refNode2) {
    double globalDistKm = distance(Global.minLat, Global.minLon, Global.maxLat, Global.maxLon, 'K');
    double globalDistEuc = Math.sqrt(Math.pow(Global.maxLat-Global.minLat,2)+Math.pow(Global.maxLon-Global.minLon,2));
    double ratio = globalDistEuc/globalDistKm;
    double c = ratio * km;
    return c;
  }
  
  // speed is in km/sec
  public static Vector2d getVelocityVector(double angle, double speed, Node refNode1, Node refNode2) {
    double globalDistKm = distance(Global.minLat, Global.minLon, Global.maxLat, Global.maxLon, 'K');
    // double globalDistProj = Math.sqrt(Math.pow(Global.openStreetMap.projectedMaxX-Global.openStreetMap.projectedMinX,2)+Math.pow(Global.openStreetMap.projectedMaxY-Global.openStreetMap.projectedMinY,2));
    double globalDistEuc = Math.sqrt(Math.pow(Global.maxLat-Global.minLat,2)+Math.pow(Global.maxLon-Global.minLon,2));
    
    // double ratio = globalDistProj/globalDistKm;
    double ratio = globalDistEuc/globalDistKm;
    
    speed = speed/(60*60);
    // speed = speed/60; // 1/60th of a sec
    
    double c = ratio * speed;
    double deltaLat = Math.sin(angle)*c;
    double deltaLon = Math.cos(angle)*c;
    // based on this we can negate deltaLat or deltaLon to the correct sign
    if (Math.toDegrees(angle) < 0) {
      deltaLat*=1;
      deltaLon*=1;
    }
    else if (Math.toDegrees(angle) > 45) {
      deltaLat*=-1;
      deltaLon*=1;
    }
    else {
      deltaLat*=-1;
      deltaLon*=1;
    }
    
    return new Vector2d(deltaLat, deltaLon);
  }
}