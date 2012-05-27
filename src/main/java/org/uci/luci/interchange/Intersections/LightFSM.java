package org.uci.luci.interchange.Intersections;

import org.uci.luci.interchange.Util.*;
import java.util.Random;

public class LightFSM {
  public static enum LIGHT {
    RED, GREEN, YELLOW
  };
  
  private double deadTimeDur = 5;
  private double throughsGreenDur, throughsYellowDur;
  private double leftsGreenDur, leftsYellowDur;
  
  private double lastStateChangeAt;
  private String state;
  
  public LightFSM(double throughsGreenDur, double throughsYellowDur,
                  double leftsGreenDur, double leftsYellowDur) {
    this.throughsGreenDur = throughsGreenDur;
    this.throughsYellowDur = throughsYellowDur;
    this.leftsGreenDur = leftsGreenDur;
    this.leftsYellowDur = leftsYellowDur;
    
		Random randomGenerator = Utils.randomNumberGenerator();
    lastStateChangeAt = -randomGenerator.nextInt((int)throughsGreenDur);
    state = "all_red";
  }
  
  public String getState() {
	  return state;
	}
  
  public LIGHT getLightForRights1() {
    return LIGHT.GREEN;
  }
  
  public LIGHT getLightForRights2() {
    return LIGHT.GREEN;
  }
  
  public LIGHT getLightForThrough1() {
    if (state.equals("throughs_green1"))
      return LIGHT.GREEN;
    else if (state.equals("throughs_yellow1"))
      return LIGHT.YELLOW;
    return LIGHT.RED;
  }
  
  public LIGHT getLightForLefts1() {
    if (state.equals("lefts_green1"))
      return LIGHT.GREEN;
    else if (state.equals("lefts_yellow1"))
      return LIGHT.YELLOW;
    return LIGHT.RED;
  }
  
  public LIGHT getLightForThrough2() {
    if (state.equals("throughs_green2"))
      return LIGHT.GREEN;
    else if (state.equals("throughs_yellow2"))
      return LIGHT.YELLOW;
    return LIGHT.RED;
  }
  
  public LIGHT getLightForLefts2() {
    if (state.equals("lefts_green2"))
      return LIGHT.GREEN;
    else if (state.equals("lefts_yellow2"))
      return LIGHT.YELLOW;
    return LIGHT.RED;
  }
  
  public void tick(double simTime, double tickLength, int tick) {
    double sinceLastStateChange = simTime - lastStateChangeAt;
    
    if (state.equals("all_red") && sinceLastStateChange > deadTimeDur) {
      state = "lefts_green1";
      lastStateChangeAt = simTime;
    }
    else if (state.equals("lefts_green1") && sinceLastStateChange > leftsGreenDur) {
      state = "lefts_yellow1";
      lastStateChangeAt = simTime;
    }
    else if (state.equals("lefts_yellow1") && sinceLastStateChange > leftsYellowDur) {
      state = "throughs_green1";
      lastStateChangeAt = simTime;
    }
    else if (state.equals("throughs_green1") && sinceLastStateChange > throughsGreenDur) {
      state = "throughs_yellow1";
      lastStateChangeAt = simTime;
    }
    else if (state.equals("throughs_yellow1") && sinceLastStateChange > throughsYellowDur) {
      state = "lefts_green2";
      lastStateChangeAt = simTime;
    }
    else if (state.equals("lefts_green2") && sinceLastStateChange > leftsGreenDur) {
      state = "lefts_yellow2";
      lastStateChangeAt = simTime;
    }
    else if (state.equals("lefts_yellow2") && sinceLastStateChange > leftsYellowDur) {
      state = "throughs_green2";
      lastStateChangeAt = simTime;
    }
    else if (state.equals("throughs_green2") && sinceLastStateChange > throughsGreenDur) {
      state = "throughs_yellow2";
      lastStateChangeAt = simTime;
    }
    else if (state.equals("throughs_yellow2") && sinceLastStateChange > throughsYellowDur) {
      state = "all_red";
      lastStateChangeAt = simTime;
    }
  }
}