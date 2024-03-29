
public class fixed_machine {
	
	// mxm bipartite graph
	public static final int m = 4;
	public static final int maxNumEdgeSets = 1;
	public static final int maxNumEdgesPerSet = 20;
	
	public static void main(String[] args) {
		double maxRatio = 0;
		// number of edge sets
		for (int n = 1; n <= maxNumEdgeSets; n++) {
			for (int l = 0; l < 10000; l++) {
				int[][][] edgeSets = new int[n][maxNumEdgesPerSet + 1][2];
				
				for (int i = 0; i < n; i++) {
					int numEdges = 1 + (int) (Math.random() * maxNumEdgesPerSet);
					
					// creating the edge
					for (int j = 0; j < numEdges; j++) {
						edgeSets[i][j][0] = 1 + (int) (Math.random() * m);
						edgeSets[i][j][1] = 1 + (int) (Math.random() * m);
					}
				}
				
				boolean[][][] usedVertices = new boolean[n * maxNumEdgesPerSet + 1][m + 1][2];
				// graph, edge number, edgeSet # + edge index
				int[][][] edgesPerGraph = new int[n * maxNumEdgesPerSet + 1][m][2];
				int usedGraphs = -1;
				int[] edgeCountPerGraph = new int[n * maxNumEdgesPerSet + 1];
				
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < maxNumEdgesPerSet + 1; j++) {
						if (edgeSets[i][j][0] == 0) {
							break;
						}
						
						// place the edge
						for (int graph = 0; graph <= n * maxNumEdgesPerSet; graph++) {
							if (graph == n * maxNumEdgesPerSet) {
								System.out.println("Something bad happened.");
								System.exit(0);
							}
							
							if (!usedVertices[graph][edgeSets[i][j][0]][0] && !usedVertices[graph][edgeSets[i][j][1]][1]) {
								usedVertices[graph][edgeSets[i][j][0]][0] = true;
								usedVertices[graph][edgeSets[i][j][1]][1] = true;
								
								if (usedGraphs < graph) {
									usedGraphs = graph;
								}
								
								edgesPerGraph[graph][edgeCountPerGraph[graph]][0] = i;
								edgesPerGraph[graph][edgeCountPerGraph[graph]][1] = j;
								edgeCountPerGraph[graph]++;
								break;
							}
						}
					}
				}
				
				int[][] maxDegrees = new int[m + 1][2];
				int maxDegree = -1;
				int[] maxDegreeGraphs = new int[usedGraphs + 1];
				
				for (int i = 0; i <= usedGraphs; i++) {
					for (int j = 1; j <= m; j++) {
						if (usedVertices[i][j][0]) {
							maxDegrees[j][0]++;
							if (maxDegrees[j][0] > maxDegree) {
								maxDegree = maxDegrees[j][0];
							}
						}
						if (usedVertices[i][j][1]) {
							maxDegrees[j][1]++;
							if (maxDegrees[j][1] > maxDegree) {
								maxDegree = maxDegrees[j][1];
							}
						}
					}
					maxDegreeGraphs[i] = maxDegree;
				}
				
				double ratio = (usedGraphs + 1) / (double) maxDegreeGraphs[usedGraphs];
				
				if (ratio > maxRatio) {
					maxRatio = ratio;
					
					if (maxRatio >= 1.5) {
						displayResults(n, edgeSets, usedGraphs, usedVertices, edgesPerGraph, maxDegreeGraphs, ratio);
					}
				}
				// displayResults(edgeSets, usedGraphs, usedVertices, edgesPerGraph, maxDegreeGraphs, ratio);
			}
		}
		
		System.out.println(maxRatio);
	}
	
	public static void displayResults(int n, int[][][] edgeSets, int usedGraphs, boolean[][][] usedVertices, int[][][] edgesPerGraph, int[] maxDegreeGraphs, double ratio) {
		System.out.println("Edge Sets:");
		for (int i = 0; i < n; i++) {
			System.out.print("i = " + (i + 1) + ": [");
			for (int j = 0; j < maxNumEdgesPerSet; j++) {
				if (edgeSets[i][j][0] == 0) {
					break;
				}
				if (j == 0) {
					System.out.print("(" + edgeSets[i][j][0] + ", " + edgeSets[i][j][1] + ")");
				} else {
					System.out.print(", (" + edgeSets[i][j][0] + ", " + edgeSets[i][j][1] + ")");
				}
			}
			System.out.println("]");
		}
		
		System.out.println("------------------------");
		System.out.println("Number of time slots: " + (usedGraphs + 1) + "\n");
		
		for (int i = 0; i <= usedGraphs; i++) {
			System.out.println("Graph " + (i + 1));
			for (int j = 1; j <= m; j++) {
				if (usedVertices[i][j][0]) {
					for (int k = 0; k < m; k++) {
						if (edgeSets[edgesPerGraph[i][k][0]][edgesPerGraph[i][k][1]][0] == j) {
							System.out.print("* " + (edgesPerGraph[i][k][0] + 1) + "  ");
							break;
						}
					}
				} else {
					System.out.print("*    ");
				}
				if (usedVertices[i][j][1]) {
					for (int k = 0; k < m; k++) {
						if (edgeSets[edgesPerGraph[i][k][0]][edgesPerGraph[i][k][1]][1] == j) {
							System.out.println("* " + (edgesPerGraph[i][k][0] + 1));
							break;
						}
					}
				} else {
					System.out.println("*");
				}
			}
			
			System.out.println("Max Degree: " + maxDegreeGraphs[i] + "\n");
		}
		
		System.out.println("(# of time slots)/(max degree) = " + ratio);
	}
}
