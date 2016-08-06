import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Practice {
	
	public List<String> parseIp(String ip) {
		if (ip.length() < 4) {
			return null;
		}
		ArrayList<String> ips = new ArrayList<String>();
		ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		parseIp(ip, 0, output, new ArrayList<String>());
		
		for (ArrayList<String> ipAddress : output) {
			StringBuffer sb = new StringBuffer();
			for (String octet : ipAddress) {
				sb.append(octet);
				sb.append(":");
			}
			sb.deleteCharAt(sb.length()-1);
			ips.add(sb.toString());
		}
		return ips;
	}
	
	private void parseIp(String ip, int index, ArrayList<ArrayList<String>> op, ArrayList<String> currIp) {
		if (currIp.size() == 4 && index == ip.length()) {
			ArrayList<String> temp = new ArrayList<String> (currIp);
			op.add(temp);
			return;
		}
		if (currIp.size() >= 4 && index < ip.length()) {
			// too long, already exceeded our octets
			return;
		}
		/*  // this check not needed really
		if (currIp.size() < 4 && index == ip.length()) {
			// too short, not enough octets
			return;
		}
		*/
		
		for (int i=1; i<=3; i++) {
			if (index+i > ip.length()) {
				break;
			}
			String currOctet = ip.substring(index, index+i);
			if (!isValidOctet(currOctet, currIp.size())) {
				continue;
			}
			currIp.add(currOctet);
			parseIp(ip, index+i, op, currIp);
			currIp.remove(currIp.size()-1);
		}
	}
	
	public boolean isValidOctet(String octet, int index) {
		if (octet.charAt(0) == '0') {  // not allowing preceding 0s
			return false;
		}
		Integer oct = Integer.parseInt(octet);
		if (oct > 255 || oct < 0) {
			return false;
		}
		return true;
	}
	
	/*
	 * http://s31.postimg.org/7nwp4gmzv/hurdle1.jpg
	 * Basically DFS with backtracking
	 * Assuming 0s for valid path, 1s for hurdles, 2 for destination
	 */
	public int longestDist(int [][] matrix, int x, int y) {
		boolean [][] visited = new boolean[matrix.length][matrix[0].length];
		return longestDist(matrix, 0, x, y, visited);
	}
	
	/*
	 * Runtime: O(4^n)
	 */
	private int longestDist(int [][] mat, int dist, int x, int y, boolean [][] visited) {
		if (!isWithinBounds(mat, x, y)) {
			return -1;
		}

		if (visited[x][y] ) {
			return -1;
		}

		if (isHurdle(mat,x,y)) {
			return -1;
		}

		if (isDest(mat,x,y)) {
			return dist;
		}

		visited[x][y] = true;
		int max = -1;
		int d1 = longestDist(mat, dist+1, x+1, y, visited);
		int d2 = longestDist(mat, dist+1, x, y+1, visited);
		int d3 = longestDist(mat, dist+1, x, y-1, visited);
		int d4 = longestDist(mat, dist+1, x-1, y, visited);
		max = getMax(d1, d2, d3, d4);
		visited[x][y] = false;
		
		return max;
	}
	
	private boolean isDest(int [][] matrix, int x, int y) {
		return matrix[x][y] == 2;
	}
	
	private boolean isHurdle(int [][] matrix, int x, int y) {
		return matrix[x][y] == 1;
	}
	
	private int getMax(int a, int b, int c, int d) {
		return Math.max(d, Math.max(Math.max(a, b), c));
	}
	
	private boolean isWithinBounds(int [][] matrix, int x, int y) {
		if (x < 0 || x >= matrix.length || y < 0 || y >= matrix[0].length) {
			return false;
		}
		return true;
	}
	
	/*
	 * Find maximum number of paths from top left corner of matrix to bottom right
	 * Can only move either right or down
	 */
	public int numPathsTopDown(int [][] matrix) {
		return numPathsTopDown(matrix, 0, 0, new HashMap<String, Integer>());
	}
	
	private int numPathsTopDown(int [][] mat, int x, int y, HashMap<String, Integer> map) {
		if (!isWithinBounds(mat, x, y)) {
			return 0;
		}

		if (map.containsKey(x+","+y)) {
			return map.get(x+","+y);
		}
		
		if (x == mat.length-1 && y == mat[0].length-1) {
			return 1;
		}
		
		int pathsFromHere = 0;
		int p1 = numPathsTopDown(mat, x+1, y, map);
		pathsFromHere += p1;
		int p2 = numPathsTopDown(mat, x, y+1, map);
		pathsFromHere += p2;
		map.put(x+","+y, pathsFromHere);
		return pathsFromHere;
	}
	
	public int numPathsBottomUp(int [][] mat) {
		mat[mat.length-1][mat[0].length-1] = 1;
		for (int i=mat.length-1; i>=0; i--) {
			for (int j=mat[0].length-1; j>=0; j--) {
				if (i == mat.length-1 && j == mat[0].length-1) {
					mat[i][j] = 1;
				} else {
					mat[i][j] = (j+1 >= mat[0].length ? 0 : mat[i][j+1])
							+ (i+1 >= mat.length ? 0 : mat[i+1][j]);
				}
			}
		}
		return mat[0][0];
	}
	
	public static void main(String args[]) {
		Practice p = new Practice();
		System.out.println(p.parseIp("103721"));
		System.out.println(p.parseIp("25525511135"));
		System.out.println(p.parseIp("48576758"));
		System.out.println(p.parseIp("3456"));
		System.out.println(p.parseIp("000000"));
		System.out.println(p.parseIp("847"));
		
		int [][] matrix = new int [3][10];
		matrix[1][2] = 1;
		matrix[1][5] = 1;
		matrix[1][8] = 1;
		matrix[1][7] = 2;
		System.out.println(p.longestDist(matrix, 0, 0));
		
		int [][] mat = new int[4][4];
		System.out.println(p.numPathsTopDown(mat));
		System.out.println(p.numPathsBottomUp(mat));
	}
}
