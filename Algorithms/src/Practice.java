import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	/*
	 * How many ways can you encode a numeric string with alphabets?
	 * Each number maps to its alphabet index
	 * 
	 * ex. 123:
	 * 1,2,3: a,b,c
	 * 12,3: l,c
	 * 1,23: a,w
	 * Answer: 3 ways
	 * 
	 * ex2. 678:
	 * 6,7,8: f,g,h
	 * Answer: Only 1 way
	 */
	public int numValidAlphaEncoding(String s) {
		return numValidAlphaEncoding(s, new HashMap<String, Integer>());
	}
	
	/*
	 * Run time (without dp): O(1.618^n)
	 * Run time (with dp): O(n)
	 */
	public int numValidAlphaEncoding(String s, HashMap<String, Integer> map) {
		if (s == null || s.length() == 0) {
			return 1;
		}
		
		if (map.containsKey(s)) {
			return map.get(s);
		}
		int count = 0;
		for (int i=1; i<=2; i++) {
			if (i > s.length()) {
				break;
			}
			String s1 = s.substring(0,i);
			if (isValidAlphabet(s1)) {
				count += numValidAlphaEncoding(s.substring(i), map);
			}
		}
		map.put(s, count);
		return count;
	}
	
	private boolean isValidAlphabet(String s) {
		int num = Integer.parseInt(s);
		if (num >= 1 && num <= 26) {
			return true;
		}
		return false;
	}
	
	/*
	 * Matrix multiplication.
	 * Parenthesize a chain of matrices (assume the dimensions match). Return all
	 * possible combinations of parentheses.
	 * 
	 * ex. ABCD can be parenthesized in these ways:
	 * (((AB)C)D)
	 * ((AB)(CD))
	 * ((A(BC))D)
	 * (A((BC)D))
	 * (A(B(CD)))
	 * 
	 * This solution below still repeats a lot of states (used a HashSet to return uniques),
	 * but that's a band-aid; it doesn't address the deeper issue.
	 * 
	 */
	HashSet<String> matrixMultParen(String s, HashMap<String, HashSet<String>> map) {
		if (s == null || s.length() == 0) {
			return new HashSet<String>();
		}
		if (map.containsKey(s)) {
			return map.get(s);
		}
		if (s.length() == 1) {
			HashSet<String> list = new HashSet<String>();
			list.add(s);
			return list;
		}
		if (s.length() == 2) {
			HashSet<String> list = new HashSet<String>();
			list.add("("+s+")");
			return list;
		}
		
		HashSet<String> op = new HashSet<String>();
		HashSet<String> result;
		// j=1: splitting up prefixes, ex. f(ABCD) = A,f(BCD)
		// j=2: splitting up suffixes, ex. f(ABCD) = f(ABC),D
		// (couldn't think of a cleaner way)
		for (int j=1; j<=2; j++) {
			for (int i=1; i<=2; i++) {
				String s1, rem;
				if (j == 1) {
					if (i == s.length()+1) {
						break;
					}
					s1 = s.substring(0,i);
					rem = s.substring(i);
				} else {
					if (i == s.length()) {
						break;
					}
					s1 = s.substring(s.length()-i);
					rem = s.substring(0,s.length()-i);
				}
				result = matrixMultParen(rem, map);
				if (s1.length() == 1) {
					for(String prev : result) {
						String newExp;
						if (j == 1) {
							newExp = "("+s1+prev+")";
						} else {
							newExp = "("+prev+s1+")";
						}
						op.add(newExp);
					}
				} else {
					for(String prev : result) {
						String newExp;
						if (j == 1) {
							newExp = "(("+s1+")"+prev+")";
						} else {
							newExp = "("+prev+"("+s1+"))";
						}
						op.add(newExp);
					}
				}
			}
		}
		map.put(s, op);
		return op;
	}
	
	public void moveZerosToRight(int [] arr) {
		if (arr == null || arr.length == 0) {
			return;
		}
		int l = 0;
		int r = arr.length-1;
		
		while (l < r) {
			if (arr[r] == 0) {
				r--;
			}
			
			if (l < r && arr[l] == 0 && arr[r] != 0) {
				int temp = arr[r];
				arr[r] = arr[l];
				arr[l] = temp;
				r--;
			}
			
			l++;
		}
	}
	
	public int houseRobber(int [] a) {
		if (a == null || a.length == 0) {
			return 0;
		}
		
		int even = a[0];
		int odd = a.length == 1 ? -1: Math.max(a[0], a[1]);
		
		for (int i=2; i<a.length; i++) {
			if (a[i] + even > odd) {
				int temp = odd;
				odd = a[i] + even;
				even = temp;
			} else {
				even = odd;
			}
		}
		
		return odd > even ? odd : even;
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
		
		System.out.println(p.numValidAlphaEncoding("1234"));
		System.out.println(p.numValidAlphaEncoding("100034"));
		
		System.out.println(p.matrixMultParen("ABCD", new HashMap<String, HashSet<String>>()));
		
		int [] ratings = {1,4,3,3,3,1};
		int [] ratings2 = {2,3,1,6,5,4};
		int [] ratings3 = {1,2,3,5};
		//System.out.println(p.candy(ratings3));
		
		int [] a = {6,0,6,4,0,0,4,67,0,1,4,0};
		int [] a2 = {1, 9, 8, 4, 0, 0, 2, 7, 0, 6, 0, 9, 0};
		p.moveZerosToRight(a2);
		for (int i=0; i<a2.length; i++) {
			System.out.print(a2[i]+" ");
		}
		System.out.println();
		
		int [] houses = {10,5,30,25,15,30};
		System.out.println(p.houseRobber(houses));
	}
}
