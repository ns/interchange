package org.uci.luci.interchange;

// Pathfinder class, Aaron Steed 2007

// Some code and structure still intact from Tom Carden's version:
// <http://www.tom-carden.co.uk/p5/a_star_web/applet/index.html>
// Some links that helped me:
// <http://theory.stanford.edu/~amitp/GameProgramming/AStarComparison.html>
// <http://www.policyalmanac.org/games/aStarTutorial.htm>
// <http://www.geocities.com/jheyesjones/astar.html>
// <http://www-b2.is.tokushima-u.ac.jp/~ikeda/suuri/dijkstra/Dijkstra.shtml>
// <http://www.cs.usask.ca/resources/tutorials/csconcepts/1999_8/tutorial/advanced/dijkstra/dijkstra.html>

import java.util.ArrayList;

public class Pathfinder{

  // public ArrayList nodes; // Storage ArrayList for the Nodes
  // public ArrayList open = new ArrayList(); // Possible Nodes for consideration
  // public ArrayList closed = new ArrayList(); // Best of the Nodes
  // public boolean wrap = false; // Setting for makeCuboidWeb() for grid wrap
  //                              // around
  // public boolean corners = true; // Setting for makeCuboidWeb() for connecting
  //                                // nodes at corners
  // public boolean manhattan = false; // Setting for using Manhattan distance
  //                                  // measuring method (false uses Euclidean
  //                                  // method)
  // public float offsetX = 0.0f, offsetY = 0.0f, offsetZ = 0.0f;// Offset to added
  //                                                            // to Nodes made
  //                                                            // with
  //                                                            // makeCuboidWeb
  // 
  // // Constructors
  // 
  // public Pathfinder(){
  //  this.nodes = new ArrayList();
  // }
  // 
  // public Pathfinder(ArrayList nodes){
  //  this.nodes = nodes;
  // }
  // 
  // public Pathfinder(int w, int h, float scale){
  //  setCuboidNodes(w, h, scale);
  // }
  // 
  // public Pathfinder(int w, int h, int d, float scale){
  //  setCuboidNodes(w, h, d, scale);
  // }
  // 
  // /**
  // * Search algortihms A star
  // *
  //     * @return ArrayList<Node>
  //     * 
  // * */
  //     
  // public ArrayList aStar(Node start, Node finish) {
  //   System.out.println("aStar(Node start, Node finish) STARTED");
  //  for(int i = 0; i < nodes().size(); i++){
  //    Node n = (Node) nodes().get(i);
  //    n.reset();
  //  }
  //  open.clear();
  //  closed.clear();
  //  open.add(start);
  //  while(open.size() > 0){
  //      System.out.println("LOOP BODY STARTED open.size()=" + open.size());
  //      
  //    float lowest = Float.MAX_VALUE;
  //    int c = -1;
  //    for(int i = 0; i < open.size(); i++){
  //      Node temp = (Node) open.get(i);
  //      if(temp.f < lowest){
  //        lowest = temp.f;
  //        c = i;
  //      }
  //    }
  //    
  //    System.out.println("\t lowest sum node = " + c + " cost="+lowest);
  //    
  //    Node current = (Node) open.remove(c);
  //    closed.add(current);
  //    if(current == finish) {
  //      System.out.println("\tcurrent = finish, breaking");
  //      break;
  //    }
  //    
  //    System.out.println("\tcurrent node # links = " + current.links.size());
  //    
  //    for(int i = 0; i < current.links.size(); i++){
  //      Connector a = (Connector) current.links.get(i);
  //      Node adjacent = a.n;
  //      if(adjacent.walkable && !arrayListContains(closed, adjacent)){
  //        if(!arrayListContains(open, adjacent)){
  //          open.add(adjacent);
  //          adjacent.parent = current;
  //          adjacent.setG(a);
  //          if(manhattan)
  //            adjacent.MsetF(finish);
  //          else
  //            adjacent.setF(finish);
  //        }else{
  //          if(adjacent.g > current.g + a.d){
  //            adjacent.parent = current;
  //            adjacent.setG(a);
  //            if(manhattan)
  //              adjacent.MsetF(finish);
  //            else
  //              adjacent.setF(finish);
  //          }
  //        }
  //      }
  //    }
  //  }
  //  
  //   System.out.println("LOOP BODY ENDED");
  //  
  //  // Path generation
  //  ArrayList path = new ArrayList();
  //  Node pathNode = finish;
  //  while(pathNode != null){
  //    path.add(pathNode);
  //    pathNode = pathNode.parent;
  //  }
  //  // Hack to provide a compromise path when a route to the finish node is
  //  // unavailable
  //  Node test = (Node) path.get(path.size() - 1);
  //  if(test == finish){
  //    float leastDist = Float.MAX_VALUE;
  //    Node bestNode = null;
  //    for(int i = 0; i < closed.size(); i++){
  //      Node n = (Node) closed.get(i);
  //      float nDist = n.dist(finish);
  //      if(nDist < leastDist){
  //        leastDist = nDist;
  //        bestNode = n;
  //      }
  //    }
  //    if(bestNode.parent != null){
  //      pathNode = bestNode;
  //      path = new ArrayList();
  //      while(pathNode != null){
  //        path.add(pathNode);
  //        pathNode = pathNode.parent;
  //      }
  //    }
  //  }
  //  return path;
  // }
  // 
  // // BEST FIRST SEARCH
  // 
  // public ArrayList bfs(Node start, Node finish){
  //  for(int i = 0; i < nodes().size(); i++){
  //    Node n = (Node) nodes().get(i);
  //    n.reset();
  //  }
  //  open.clear();
  //  closed.clear();
  //  open.add(start);
  //  while(open.size() > 0){
  //    float lowest = Float.MAX_VALUE;
  //    int c = -1;
  //    for(int i = 0; i < open.size(); i++){
  //      Node temp = (Node) open.get(i);
  //      if(temp.h < lowest){
  //        lowest = temp.h;
  //        c = i;
  //      }
  //    }
  //    Node current = (Node) open.remove(c);
  //    closed.add(current);
  //    if(current == finish){
  //      break;
  //    }
  //    for(int i = 0; i < current.links.size(); i++){
  //      Connector a = (Connector) current.links.get(i);
  //      Node adjacent = a.n;
  //      if(adjacent.walkable && !arrayListContains(closed, adjacent)){
  //        if(!arrayListContains(open, adjacent)){
  //          open.add(adjacent);
  //          adjacent.parent = current;
  //          if(manhattan)
  //            adjacent.MsetH(finish);
  //          else
  //            adjacent.setH(finish);
  //        }
  //      }
  //    }
  //  }
  //  // Path generation
  //  ArrayList path = new ArrayList();
  //  Node pathNode = finish;
  //  while(pathNode != null){
  //    path.add(pathNode);
  //    pathNode = pathNode.parent;
  //  }
  //  // Hack to provide a compromise path when a route to the finish node is
  //  // unavailable
  //  Node test = (Node) path.get(path.size() - 1);
  //  if(test == finish){
  //    float leastDist = Float.MAX_VALUE;
  //    Node bestNode = null;
  //    for(int i = 0; i < closed.size(); i++){
  //      Node n = (Node) closed.get(i);
  //      float nDist = n.dist(finish);
  //      if(nDist < leastDist){
  //        leastDist = nDist;
  //        bestNode = n;
  //      }
  //    }
  //    if(bestNode.parent != null){
  //      pathNode = bestNode;
  //      path = new ArrayList();
  //      while(pathNode != null){
  //        path.add(pathNode);
  //        pathNode = pathNode.parent;
  //      }
  //    }
  //  }
  //  return path;
  // }
  // 
  // // DIJKSTRA
  // 
  // public void dijkstra(Node start){
  //  dijkstra(start, null);
  // }
  // 
  // public ArrayList dijkstra(Node start, Node finish){
  //  for(int i = 0; i < nodes().size(); i++){
  //    Node n = (Node) nodes().get(i);
  //    n.reset();
  //  }
  //  open.clear();
  //  closed.clear();
  //  open.add(start);
  //  start.g = 0;
  //  while(open.size() > 0){
  //    Node current = (Node) open.remove(0);
  //    closed.add(current);
  //    if(current == finish){
  //      break;
  //    }
  //    for(int i = 0; i < current.links.size(); i++){
  //      Connector a = (Connector) current.links.get(i);
  //      Node adjacent = a.n;
  //      if(adjacent.walkable && !arrayListContains(closed, adjacent)){
  //        if(!arrayListContains(open, adjacent)){
  //          open.add(adjacent);
  //          adjacent.parent = current;
  //          adjacent.setG(a);
  //        }else{
  //          if(adjacent.g > current.g + a.d){
  //            adjacent.parent = current;
  //            adjacent.setG(a);
  //          }
  //        }
  //      }
  //    }
  //  }
  //  // Path generation
  //  ArrayList path = new ArrayList();
  //  Node pathNode = finish;
  //  while(pathNode != null){
  //    path.add(pathNode);
  //    pathNode = pathNode.parent;
  //  }
  //  return path;
  // }
  // 
  // public ArrayList getPath(Node pathNode){
  //  ArrayList path = new ArrayList();
  //  while(pathNode != null){
  //    path.add(pathNode);
  //    pathNode = pathNode.parent;
  //  }
  //  return path;
  // }
  // 
  // // Shortcut to adding a makeCuboidWeb construct to Pathfinder
  // 
  // public void setCuboidNodes(int w, int h, float scale) {
  //  nodes = new ArrayList();
  //  nodes = createCuboidNodes(new int[]{
  //      w, h}, scale);
  // }
  // 
  // public void setCuboidNodes(int w, int h, int d, float scale){
  //  nodes = new ArrayList();
  //  nodes = createCuboidNodes(new int[]{
  //      w, h, d}, scale);
  // }
  // 
  // public void addNodes(ArrayList nodes){
  //  this.nodes().addAll(nodes);
  // }
  // 
  // // Creates a construct of Nodes and connects them across adjacent dimensions.
  // // Adapts for corners and wrap-around but at a cost of speed - only for init
  // 
  // public ArrayList createCuboidNodes(int w, int h, float scale){
  //  return createCuboidNodes(new int[]{
  //      w, h}, scale);
  // }
  // 
  // public ArrayList createCuboidNodes(int w, int h, int d, float scale){
  //  return createCuboidNodes(new int[]{
  //      w, h, d}, scale);
  // }
  // 
  // /*
  //  * // Just some notes incase I have to remove the array method of building a
  //  * map public ArrayList createCuboidNodes(int w, int h, int d, float scale){
  //  * ArrayList world = new ArrayList(); int totalLength = w * h * d; for(int i =
  //  * 0; i < totalLength; i++){ float x = offsetX + ((i % (w * h)) % w) * scale;
  //  * float y = offsetY + ((i % (w * h)) / w) * scale; float z = offsetZ + i / (w *
  //  * h); world.add(new Node(x, y, z)); } }
  //  */
  // 
  // // This beast I'd rather leave as is. Sorry.
  // // I'm hiding it though. I may rely on array building ArrayList3Ds in the
  // // future.
  // private ArrayList createCuboidNodes(int [] dim, float scale){
  //  ArrayList world = new ArrayList();
  //  int totalLength = 1;
  //  for(int i = 0; i < dim.length; i++){
  //    if(dim[i] > 0){
  //      totalLength *= dim[i];
  //    }
  //  }
  //  for(int i = 0; i < totalLength; i++){
  //    int [] intP = getFolded(i, dim);
  //    float [] p = new float[intP.length];
  //    for(int j = 0; j < p.length; j++){
  //      p[j] = intP[j] * scale;
  //    }
  //    Node temp = new Node(p);
  //    temp.x += offsetX;
  //    temp.y += offsetY;
  //    temp.z += offsetZ;
  //    world.add(temp);
  //  }
  //  int directions = (int) Math.pow(3, dim.length);
  //  for(int i = 0; i < totalLength; i++){
  //    int [] p = getFolded(i, dim);
  //    Node myNode = (Node) world.get(i);
  //    int [] b = new int[p.length];
  //    int [] w = new int[p.length];
  //    for(int j = 0; j < b.length; j++){
  //      b[j] = p[j] - 1;
  //      w[j] = 0;
  //    }
  //    for(int j = 0; j < directions; j++){
  //      boolean valid = true;
  //      for(int k = 0; k < dim.length; k++){
  //        if(b[k] > dim[k] - 1 || b[k] < 0){
  //          if(wrap){
  //            if(b[k] > dim[k] - 1){
  //              b[k] -= dim[k];
  //              w[k]--;
  //            }
  //            if(b[k] < 0){
  //              b[k] += dim[k];
  //              w[k]++;
  //            }
  //          }else{
  //            valid = false;
  //          }
  //        }
  //        if(!corners){
  //          int combinations = 0;
  //          for(int l = 0; l < dim.length; l++){
  //            if(b[l] != p[l]){
  //              combinations++;
  //            }
  //          }
  //          if(combinations > 1){
  //            valid = false;
  //          }
  //        }
  //      }
  //      if(valid){
  //        Node connectee = (Node) world.get(getUnfolded(b, dim));
  //        if(myNode != connectee){
  //          myNode.connect(connectee);
  //        }
  //      }
  //      if(wrap){
  //        for(int k = 0; k < dim.length; k++){
  //          switch(w[k]){
  //            case 1:
  //              b[k] -= dim[k];
  //              w[k] = 0;
  //              break;
  //            case -1:
  //              b[k] += dim[k];
  //              w[k] = 0;
  //              break;
  //          }
  //        }
  //      }
  //      b[0]++;
  //      for(int k = 0; k < b.length - 1; k++){
  //        if(b[k] > p[k] + 1){
  //          b[k + 1]++;
  //          b[k] -= 3;
  //        }
  //      }
  //    }
  //  }
  //  return world;
  // }
  // 
  // // The next two functions are shortcut methods for disconnecting unwalkables
  // 
  // public void disconnectUnwalkables(){
  //  for(int i = 0; i < nodes().size(); i++){
  //    Node temp = (Node) nodes().get(i);
  //    if(!temp.walkable){
  //      temp.disconnect();
  //    }
  //  }
  // }
  // 
  // public void radialDisconnectUnwalkables(){
  //  for(int i = 0; i < nodes().size(); i++){
  //    Node temp = (Node) nodes().get(i);
  //    if(!temp.walkable){
  //      temp.radialDisconnect();
  //    }
  //  }
  // }
  // 
  // //
  // // Utilities
  // //
  // 
  // // Faster than running ArrayList.contains - we only need the reference, not an
  // // object match
  // 
  // public boolean arrayListContains(ArrayList c, Node n){
  //  for(int i = 0; i < c.size(); i++){
  //    Node o = (Node) c.get(i);
  //    if(o == n){
  //      return true;
  //    }
  //  }
  //  return false;
  // }
  // 
  // // Faster than running ArrayList.indexOf - we only need the reference, not an
  // // object match
  // 
  // public int indexOf(Node n){
  //  for(int i = 0; i < nodes().size(); i++){
  //    Node o = (Node) nodes().get(i);
  //    if(o == n){
  //      return i;
  //    }
  //  }
  //  return -1;
  // }
  // 
  // // Returns an n-dimensional arrayList from a point on a line of units given an
  // // n-dimensional space
  // 
  // public int [] getFolded(int n, int [] d){
  //  int [] coord = new int[d.length];
  //  for(int i = 0; i < d.length; i++){
  //    coord[i] = n;
  //    for(int j = d.length - 1; j > i; j--){
  //      int level = 1;
  //      for(int k = 0; k < j; k++){
  //        level *= d[k];
  //      }
  //      coord[i] %= level;
  //    }
  //    int level = 1;
  //    for(int j = 0; j < i; j++){
  //      level *= d[j];
  //    }
  //    coord[i] /= level;
  //  }
  //  return coord;
  // }
  // 
  // // Returns a point on a line of units from an n-dimensional arrayList in an
  // // n-dimensional space
  // 
  // public int getUnfolded(int [] p, int [] d){
  //  int coord = 0;
  //  for(int i = 0; i < p.length; i++){
  //    int level = 1;
  //    for(int j = 0; j < i; j++){
  //      level *= d[j];
  //    }
  //    coord += p[i] * level;
  //  }
  //  return coord;
  // }

}
