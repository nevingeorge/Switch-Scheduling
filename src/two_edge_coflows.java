import java.util.ArrayList;

public class two_edge_coflows {
	
	// mxm bipartite graph
	public static final int minM = 4;
	public static final int maxM = 4;
	public static final int minNumEdgeSets = 2;
	public static final int maxNumEdgeSets = 30;
	public static final int numEdgesPerSet = 2;
	public static final int numIterations = 100000;

	public static void main(String[] args) {
		double maxRatio = -1;
		int maxLB = -1;
		int maxCost = -1;
		ArrayList<ArrayList<int[]>> maxEdgeSets = null;
		int maxN = -1;
		int maxMVal = -1;
		
		for (int l = 0; l < numIterations; l++) {
			for (int m = minM; m <= maxM; m++) {
				for (int n = minNumEdgeSets; n <= maxNumEdgeSets; n++) {
					ArrayList<ArrayList<int[]>> edgeSets = new ArrayList<ArrayList<int[]>>();
					
					for (int i = 0; i < n; i++) {
						ArrayList<int[]> timeStep = new ArrayList<int[]>();
						
						// create the edges
						for (int j = 0; j < numEdgesPerSet; j++) {
							int[] edge = {(int) (Math.random() * m), (int) (Math.random() * m)};
							timeStep.add(edge);
						}
						edgeSets.add(timeStep);
					}
				
					// contains the edges in every time step
					// each element ArrayList<int[]> is a list of edges, where (int[0], int[1]) is the (i,j) = (edge set number, edge number) of edgeSets
					ArrayList<ArrayList<int[]>> timeSteps = new ArrayList<ArrayList<int[]>>();
					boolean[][][] usedVertices = new boolean[n * numEdgesPerSet][m][2];
					
					for (int i = 0; i < n; i++) {
						for (int j = 0; j < edgeSets.get(i).size(); j++) {
							// place the edge
							for (int graph = 0; graph <= n * numEdgesPerSet; graph++) {
								if (graph == n * numEdgesPerSet) {
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
					
					int lb = getLB(edgeSets, n, m);
					int newCost = getCost(timeSteps, n);
					double ratio = newCost / (double) lb;
					
					if (ratio > maxRatio) {
						maxRatio = ratio;
						maxLB = lb;
						maxCost = newCost;
						maxEdgeSets = edgeSets;
						maxN = n;
						maxMVal = m;
					}
					
					if (ratio > 1.4) {
						System.out.println("m: " + m);
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
		
		System.out.println("m: " + maxMVal);
		displayEdgeSets(maxEdgeSets, maxN);
		System.out.println("Lower bound: " + maxLB);
		System.out.println("New cost: " + maxCost);
		System.out.println("Ratio: " + maxRatio);
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
