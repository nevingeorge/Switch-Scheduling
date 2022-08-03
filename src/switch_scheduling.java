import java.util.ArrayList;

public class switch_scheduling {
	
	// mxm bipartite graph
	public static final int minM = 2;
	public static final int maxM = 10;
	public static final int minNumEdgeSets = 30;
	public static final int maxNumEdgeSets = 50;
	public static final int maxNumEdgesPerSet = 10;
	public static final int numIterations = 500;

	public static void main(String[] args) {
		double maxRatio = -1;
		int maxLB = -1;
		int maxCost = -1;
		
		for (int l = 0; l < numIterations; l++) {
			for (int m = minM; m <= maxM; m++) {
				for (int n = minNumEdgeSets; n <= maxNumEdgeSets; n++) {
					// each big arraylist is an edge set
					// each edge set contains an arraylist of the edges
					// an edge is an int[] where int[0] = left v, int[1] = right v
					ArrayList<ArrayList<int[]>> edgeSets = new ArrayList<ArrayList<int[]>>();
					
					for (int i = 0; i < n; i++) {
						int numEdges = 1 + (int) (Math.random() * maxNumEdgesPerSet);
						
						ArrayList<int[]> timeStep = new ArrayList<int[]>();
						
						// create the edges
						for (int j = 0; j < numEdges; j++) {
							int[] edge = {(int) (Math.random() * m), (int) (Math.random() * m)};
							timeStep.add(edge);
						}
						edgeSets.add(timeStep);
					}
					
//					ArrayList<int[]> timeStep0 = new ArrayList<int[]>();
//					int[] edge0 = {2, 2};
//					timeStep0.add(edge0);
//					edgeSets.add(timeStep0);
//					ArrayList<int[]> timeStep2 = new ArrayList<int[]>();
//					int[] edge2 = {0, 1};
//					timeStep2.add(edge2);
//					edgeSets.add(timeStep2);
//					ArrayList<int[]> timeStep3 = new ArrayList<int[]>();
//					int[] edge3 = {2, 0};
//					int[] edge4 = {2, 1};
//					int[] edge5 = {0, 2};
//					int[] edge6 = {1, 0};
//					int[] edge7 = {0, 2};
//					int[] edge8 = {2, 0};
//					int[] edge9 = {0, 0};
//					timeStep3.add(edge3);
//					timeStep3.add(edge4);
//					timeStep3.add(edge5);
//					timeStep3.add(edge6);
//					timeStep3.add(edge7);
//					timeStep3.add(edge8);
//					timeStep3.add(edge9);
//					edgeSets.add(timeStep3);
				
					// contains the edges in every time step
					// each element ArrayList<int[]> is a list of edges, where (int[0], int[1]) is the (i,j) = (edge set number, edge number) of edgeSets
					ArrayList<ArrayList<int[]>> timeSteps = new ArrayList<ArrayList<int[]>>();
					boolean[][][] usedVertices = new boolean[n * maxNumEdgesPerSet][m][2];
					
					for (int i = 0; i < n; i++) {
						for (int j = 0; j < edgeSets.get(i).size(); j++) {
							// place the edge
							for (int graph = 0; graph <= n * maxNumEdgesPerSet; graph++) {
								if (graph == n * maxNumEdgesPerSet) {
									System.out.println("Something bad happened.");
									System.exit(0);
								}
								
								if (!usedVertices[graph][edgeSets.get(i).get(j)[0]][0] && !usedVertices[graph][edgeSets.get(i).get(j)[1]][1]) {
									usedVertices[graph][edgeSets.get(i).get(j)[0]][0] = true;
									usedVertices[graph][edgeSets.get(i).get(j)[1]][1] = true;
									
									if (graph >= timeSteps.size()) {
										// add new graph
										ArrayList<int[]> timeStep = new ArrayList<int[]>();
										timeSteps.add(timeStep);
									}
									
									int[] edge = {i, j};
									timeSteps.get(graph).add(edge);
						
									break;
								}
							}
						}
					}
					
					// display1(edgeSets, timeSteps, n);
					
					for (int j = 1; j < n; j++) {
						for (int i = j-1; i >= 0; i--) {
							// run Konig's theorem on coflows i to j
							
							int startTS = getEdgeSetStartStop(i, timeSteps, 0);
							int endTS = getEdgeSetStartStop(j, timeSteps, 1);
							if (startTS == -1 || endTS == -1) {
								System.out.println("Edge set start/stop broke.");
								System.exit(0);
							}
							
							ArrayList<int[]> edges = new ArrayList<int[]>();
							for (int k = startTS; k <= endTS; k++) {
								for (int[] edge : timeSteps.get(k)) {
									edges.add(edge);
								}
							}
							
							ArrayList<ArrayList<int[]>> compressedTimeSlots = new ArrayList<ArrayList<int[]>>();
							
							while (true) {
								if (edges.size() == 0) {
									break;
								}
								
								// find the degrees of every vertex
								int[][] degrees = new int[m][2];
								for (int[] edge : edges) {
									degrees[edgeSets.get(edge[0]).get(edge[1])[0]][0]++;
									degrees[edgeSets.get(edge[0]).get(edge[1])[1]][1]++;
								}
								int maxDegree = 0;
								for (int d = 0; d < m; d++) {
									for (int k = 0; k < 2; k++) {
										if (degrees[d][k] > maxDegree) {
											maxDegree = degrees[d][k];
										}
									}
								}
								
								// get all the vertices of the specified degree
								boolean[][] vertices = new boolean[m][2];
								int numVertices = 0;
								for (int v = 0; v < m; v++) {
									for (int lr = 0; lr < 2; lr++) {
										if (degrees[v][lr] == maxDegree) {
											vertices[v][lr] = true;
											numVertices++;
										}
									}
								}
		
								ArrayList<int[]> edgesToMatch = new ArrayList<int[]>();
								
								// get the edges that have vertices in the array
								for (int[] edge : edges) {
									if (vertices[edgeSets.get(edge[0]).get(edge[1])[0]][0] || vertices[edgeSets.get(edge[0]).get(edge[1])[1]][1]) {
										edgesToMatch.add(edge);
									}
								}
								
								ArrayList<int[]> matchedEdges = match(edgeSets, edgesToMatch, new boolean[m][2], 0, numVertices, vertices);
								if (matchedEdges == null) {
									System.out.println("Matching process broke.");
									System.exit(0);
								}
								
								compressedTimeSlots.add(matchedEdges);
								for (int[] edge : matchedEdges) {
									edges.remove(edge);
								}
							}
							
							if (better(timeSteps, compressedTimeSlots, startTS, endTS, n)) {
								// update timeSteps
								for (int count = 1; count <= endTS - startTS + 1; count++) {
									timeSteps.remove(startTS);
								}
								for (int ind = compressedTimeSlots.size() - 1; ind >= 0; ind--) {
									timeSteps.add(startTS, compressedTimeSlots.get(ind));
								}
							}
						}
					}
					
					int lb = getLB(edgeSets, n, m);
					int newCost = getCost(timeSteps, n);
					double ratio = newCost / (double) lb;
					
					if (ratio > maxRatio) {
						maxRatio = ratio;
						maxLB = lb;
						maxCost = newCost;
					}
					
					if (ratio > 1.34) {
						displayEdgeSets(edgeSets, n);
						System.out.println("Lower bound: " + maxLB);
						System.out.println("New cost: " + maxCost);
						System.out.println("Ratio: " + maxRatio);
						System.exit(0);
					}
					
//					System.out.println("Lower bound: " + lb);
//					System.out.println("New cost: " + newCost);
//					System.out.println("Ratio: " + ratio);
				}
			}
		}
		
//		System.out.println("Lower bound: " + maxLB);
//		System.out.println("New cost: " + maxCost);
//		System.out.println("Ratio: " + maxRatio);
	}
	
	public static int getLB(ArrayList<ArrayList<int[]>> edgeSets, int n, int m) {
		int[][] degrees = new int[m][2];
		int sum = 0;
		int maxDegree = 0;
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < edgeSets.get(i).size(); j++) {
				degrees[edgeSets.get(i).get(j)[0]][0]++;
				degrees[edgeSets.get(i).get(j)[1]][1]++;
			}
			
			for (int j = 0; j < m; j++) {
				for (int k = 0; k < 2; k++) {
					if (degrees[j][k] > maxDegree) {
						maxDegree = degrees[j][k];
					}
				}
			}
			sum += maxDegree;
		}
		
		return sum;
	}
	
	public static boolean better(ArrayList<ArrayList<int[]>> timeSteps, ArrayList<ArrayList<int[]>> compressedTimeSlots, int startTS, int endTS, int n) {
		int originalCost = getCost(timeSteps, n);
		
		int newCost = 0;
		boolean[] seen = new boolean[n];
		
		for (int i = timeSteps.size() - 1; i >= endTS + 1; i--) {
			for (int[] edge : timeSteps.get(i)) {
				if (!seen[edge[0]]) {
					newCost += i + 1;
					seen[edge[0]] = true;
				}
			}
		}
		for (int i = compressedTimeSlots.size() - 1; i >= 0; i--) {
			for (int[] edge : compressedTimeSlots.get(i)) {
				if (!seen[edge[0]]) {
					newCost += startTS + 1 + i;
					seen[edge[0]] = true;
				}
			}
		}
		for (int i = startTS - 1; i >= 0; i--) {
			for (int[] edge : timeSteps.get(i)) {
				if (!seen[edge[0]]) {
					newCost += i + 1;
					seen[edge[0]] = true;
				}
			}
		}
		
		if (newCost < originalCost) {
			return true;
		}
		return false;
	}
	
	public static int getCost(ArrayList<ArrayList<int[]>> timeSteps, int n) {
		int cost = 0;
		boolean[] seen = new boolean[n];
		
		for (int i = timeSteps.size() - 1; i >= 0; i--) {
			for (int[] edge : timeSteps.get(i)) {
				if (!seen[edge[0]]) {
					cost += i + 1;
					seen[edge[0]] = true;
				}
			}
		}
		return cost;
	}
	
	public static int getEdgeSetStartStop(int edgeSetNum, ArrayList<ArrayList<int[]>> timeSteps, int startStop) {
		if (startStop == 0) {
			// start case
			for (int i = 0; i < timeSteps.size(); i++) {
				for (int[] edge : timeSteps.get(i)) {
					if (edge[0] == edgeSetNum) {
						return i;
					}
				}
			}
		} else {
			// end case
			for (int i = timeSteps.size() - 1; i >= 0; i--) {
				for (int[] edge : timeSteps.get(i)) {
					if (edge[0] == edgeSetNum) {
						return i;
					}
				}
			}
		}
		return -1;
	}
	
	public static ArrayList<int[]> match(ArrayList<ArrayList<int[]>> edgeSets, ArrayList<int[]> edgesToMatch, boolean[][] matchedVertices, int index, int numVertices, boolean[][] vertices) {
		if (numVertices == 0) {
			return new ArrayList<int[]>();
		}
		
		for (int i = index; i < edgesToMatch.size(); i++) {
			int[] edge = edgesToMatch.get(i);
			if (!matchedVertices[edgeSets.get(edge[0]).get(edge[1])[0]][0] && !matchedVertices[edgeSets.get(edge[0]).get(edge[1])[1]][1]) {
				matchedVertices[edgeSets.get(edge[0]).get(edge[1])[0]][0] = true;
				matchedVertices[edgeSets.get(edge[0]).get(edge[1])[1]][1] = true;
				
				int newNumVertices = numVertices;
				if (vertices[edgeSets.get(edge[0]).get(edge[1])[0]][0]) {
					newNumVertices--;
				}
				if (vertices[edgeSets.get(edge[0]).get(edge[1])[1]][1]) {
					newNumVertices--;
				}
				if (newNumVertices == numVertices) {
					System.out.println("Num vertices broke.");
					System.exit(0);
				}
				
				ArrayList<int[]> matchedEdges = match(edgeSets, edgesToMatch, matchedVertices, i + 1, newNumVertices, vertices);
				if (matchedEdges != null) {
					matchedEdges.add(edge);
					return matchedEdges;
				}
				
				matchedVertices[edgeSets.get(edge[0]).get(edge[1])[0]][0] = false;
				matchedVertices[edgeSets.get(edge[0]).get(edge[1])[1]][1] = false;
			}
		}
		
		return null;
	}
	
	public static void display1(ArrayList<ArrayList<int[]>> edgeSets, ArrayList<ArrayList<int[]>> timeSteps, int n) {
		displayEdgeSets(edgeSets, n);
		
		for (int i = 0; i < timeSteps.size(); i++) {
			System.out.println("i:" + i);
			for (int j = 0; j < timeSteps.get(i).size(); j++) {
				int k = timeSteps.get(i).get(j)[0];
				int z = timeSteps.get(i).get(j)[1];
				System.out.print(edgeSets.get(k).get(z)[0] + " " + edgeSets.get(k).get(z)[1] + "|");
			}
			System.out.println();
		}
	}
	
	public static void displayEdgeSets(ArrayList<ArrayList<int[]>> edgeSets, int n) {
		System.out.println("Edge Sets:");
		for (int i = 0; i < n; i++) {
			System.out.print("i = " + (i + 1) + ": [");
			for (int j = 0; j < edgeSets.get(i).size(); j++) {
				if (j == 0) {
					System.out.print("(" + edgeSets.get(i).get(j)[0] + ", " + edgeSets.get(i).get(j)[1] + ")");
				} else {
					System.out.print(", (" + edgeSets.get(i).get(j)[0] + ", " + edgeSets.get(i).get(j)[1] + ")");
				}
			}
			System.out.println("]");
		}
	}

}
