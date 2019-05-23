import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
			sb.deleteCharAt(sb.length() - 1);
			ips.add(sb.toString());
		}
		return ips;
	}

	private void parseIp(String ip, int index, ArrayList<ArrayList<String>> op, ArrayList<String> currIp) {
		if (currIp.size() == 4 && index == ip.length()) {
			ArrayList<String> temp = new ArrayList<String>(currIp);
			op.add(temp);
			return;
		}
		if (currIp.size() >= 4 && index < ip.length()) {
			// too long, already exceeded our octets
			return;
		}
		
		/*
		// this check not needed really
		if (currIp.size() < 4 && index == ip.length()) {
			// too short, not enough octets
			return;
		}
		*/

		for (int i = 1; i <= 3; i++) {
			if (index + i > ip.length()) {
				break;
			}
			String currOctet = ip.substring(index, index + i);
			if (!isValidOctet(currOctet, currIp.size())) {
				continue;
			}
			currIp.add(currOctet);
			parseIp(ip, index + i, op, currIp);
			currIp.remove(currIp.size() - 1);
		}
	}

	public boolean isValidOctet(String octet, int index) {
		if (octet.charAt(0) == '0') { // not allowing preceding 0s
			return false;
		}
		Integer oct = Integer.parseInt(octet);
		if (oct > 255 || oct < 0) {
			return false;
		}
		return true;
	}

	/*
	 * http://s31.postimg.org/7nwp4gmzv/hurdle1.jpg Basically DFS with
	 * backtracking Assuming 0s for valid path, 1s for hurdles, 2 for
	 * destination
	 */
	public int longestDist(int[][] matrix, int x, int y) {
		boolean[][] visited = new boolean[matrix.length][matrix[0].length];
		return longestDist(matrix, 0, x, y, visited);
	}

	/*
	 * Runtime: O(4^n)
	 */
	private int longestDist(int[][] mat, int dist, int x, int y, boolean[][] visited) {
		if (!isWithinBounds(mat, x, y)) {
			return -1;
		}

		if (visited[x][y]) {
			return -1;
		}

		if (isHurdle(mat, x, y)) {
			return -1;
		}

		if (isDest(mat, x, y)) {
			return dist;
		}

		visited[x][y] = true;
		int max = -1;
		int d1 = longestDist(mat, dist + 1, x + 1, y, visited);
		int d2 = longestDist(mat, dist + 1, x, y + 1, visited);
		int d3 = longestDist(mat, dist + 1, x, y - 1, visited);
		int d4 = longestDist(mat, dist + 1, x - 1, y, visited);
		max = getMax(d1, d2, d3, d4);
		visited[x][y] = false;

		return max;
	}

	private boolean isDest(int[][] matrix, int x, int y) {
		return matrix[x][y] == 2;
	}

	private boolean isHurdle(int[][] matrix, int x, int y) {
		return matrix[x][y] == 1;
	}

	private int getMax(int a, int b, int c, int d) {
		return Math.max(d, Math.max(Math.max(a, b), c));
	}

	private boolean isWithinBounds(int[][] matrix, int x, int y) {
		if (x < 0 || x >= matrix.length || y < 0 || y >= matrix[0].length) {
			return false;
		}
		return true;
	}

	/*
	 * Find maximum number of paths from top left corner of matrix to bottom
	 * right Can only move either right or down
	 */
	public int numPathsTopDown(int[][] matrix) {
		return numPathsTopDown(matrix, 0, 0, new HashMap<String, Integer>());
	}

	private int numPathsTopDown(int[][] mat, int x, int y, HashMap<String, Integer> map) {
		if (!isWithinBounds(mat, x, y)) {
			return 0;
		}

		if (map.containsKey(x + "," + y)) {
			return map.get(x + "," + y);
		}

		if (x == mat.length - 1 && y == mat[0].length - 1) {
			return 1;
		}

		int pathsFromHere = 0;
		int p1 = numPathsTopDown(mat, x + 1, y, map);
		pathsFromHere += p1;
		int p2 = numPathsTopDown(mat, x, y + 1, map);
		pathsFromHere += p2;
		map.put(x + "," + y, pathsFromHere);
		return pathsFromHere;
	}

	public int numPathsBottomUp(int[][] mat) {
		mat[mat.length - 1][mat[0].length - 1] = 1;
		for (int i = mat.length - 1; i >= 0; i--) {
			for (int j = mat[0].length - 1; j >= 0; j--) {
				if (i == mat.length - 1 && j == mat[0].length - 1) {
					mat[i][j] = 1;
				} else {
					mat[i][j] = (j + 1 >= mat[0].length ? 0 : mat[i][j + 1])
							+ (i + 1 >= mat.length ? 0 : mat[i + 1][j]);
				}
			}
		}
		return mat[0][0];
	}

	/*
	 * How many ways can you encode a numeric string with alphabets? Each number
	 * maps to its alphabet index
	 * 
	 * ex. 123: 1,2,3: a,b,c 12,3: l,c 1,23: a,w Answer: 3 ways
	 * 
	 * ex2. 678: 6,7,8: f,g,h Answer: Only 1 way
	 */
	public int numValidAlphaEncoding(String s) {
		return numValidAlphaEncoding(s, new HashMap<String, Integer>());
	}

	/*
	 * Run time (without dp): O(1.618^n) Run time (with dp): O(n)
	 */
	public int numValidAlphaEncoding(String s, HashMap<String, Integer> map) {
		if (s == null || s.length() == 0) {
			return 1;
		}

		if (map.containsKey(s)) {
			return map.get(s);
		}
		int count = 0;
		for (int i = 1; i <= 2; i++) {
			if (i > s.length()) {
				break;
			}
			String s1 = s.substring(0, i);
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
	 * Matrix multiplication. Parenthesize a chain of matrices (assume the
	 * dimensions match). Return all possible combinations of parentheses.
	 * 
	 * ex. ABCD can be parenthesized in these ways: (((AB)C)D) ((AB)(CD))
	 * ((A(BC))D) (A((BC)D)) (A(B(CD)))
	 * 
	 * This solution below still repeats a lot of states (used a HashSet to
	 * return uniques), but that's a band-aid; it doesn't address the deeper
	 * issue.
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
			list.add("(" + s + ")");
			return list;
		}

		HashSet<String> op = new HashSet<String>();
		HashSet<String> result;
		// j=1: splitting up prefixes, ex. f(ABCD) = A,f(BCD)
		// j=2: splitting up suffixes, ex. f(ABCD) = f(ABC),D
		// (couldn't think of a cleaner way)
		for (int j = 1; j <= 2; j++) {
			for (int i = 1; i <= 2; i++) {
				String s1, rem;
				if (j == 1) {
					if (i == s.length() + 1) {
						break;
					}
					s1 = s.substring(0, i);
					rem = s.substring(i);
				} else {
					if (i == s.length()) {
						break;
					}
					s1 = s.substring(s.length() - i);
					rem = s.substring(0, s.length() - i);
				}
				result = matrixMultParen(rem, map);
				if (s1.length() == 1) {
					for (String prev : result) {
						String newExp;
						if (j == 1) {
							newExp = "(" + s1 + prev + ")";
						} else {
							newExp = "(" + prev + s1 + ")";
						}
						op.add(newExp);
					}
				} else {
					for (String prev : result) {
						String newExp;
						if (j == 1) {
							newExp = "((" + s1 + ")" + prev + ")";
						} else {
							newExp = "(" + prev + "(" + s1 + "))";
						}
						op.add(newExp);
					}
				}
			}
		}
		map.put(s, op);
		return op;
	}

	/*
	 * https://leetcode.com/problems/candy/
	 */
	public int candy2(int[] ratings) {
		if (ratings == null) {
			return 0;
		}

		int extras = 0;

		for (int i = 0; i < ratings.length; i++) {
			if (i > 0 && ratings[i] > ratings[i - 1]) {
				extras++;
			} else if (i + 1 < ratings.length && ratings[i] > ratings[i + 1]) {
				extras++;
			}
		}

		return extras + ratings.length;
	}

	public int candy(int[] ratings) {
		if (ratings == null || ratings.length == 0) {
			return 0;
		}

		int[] candies = new int[ratings.length];
		candies[0] = 1;

		// from let to right
		for (int i = 1; i < ratings.length; i++) {
			if (ratings[i] > ratings[i - 1]) {
				candies[i] = candies[i - 1] + 1;
			} else {
				// if not ascending, assign 1
				candies[i] = 1;
			}
		}

		int result = candies[ratings.length - 1];

		// from right to left
		for (int i = ratings.length - 2; i >= 0; i--) {
			int cur = 1;
			if (ratings[i] > ratings[i + 1]) {
				cur = candies[i + 1] + 1;
			}

			result += Math.max(cur, candies[i]);
			candies[i] = cur;
		}

		for (int i = 0; i < candies.length; i++) {
			System.out.print(candies[i] + " ");
		}
		System.out.println();

		return result;
	}

	public void moveZerosToRight(int[] arr) {
		if (arr == null || arr.length == 0) {
			return;
		}
		int l = 0;
		int r = arr.length - 1;

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

	public int houseRobber(int[] a) {
		if (a == null || a.length == 0) {
			return 0;
		}

		int even = a[0];
		int odd = a.length == 1 ? -1 : Math.max(a[0], a[1]);

		for (int i = 2; i < a.length; i++) {
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

	public List<List<String>> findLadders(String beginWord, String endWord, Set<String> wordList) {
		List<List<String>> results = new LinkedList<List<String>>();
		LinkedList<WordPath> pathQ = new LinkedList<WordPath>();

		wordList.remove(beginWord);
		pathQ.add(new WordPath(beginWord, null, 0));
		int minDist = Integer.MAX_VALUE;

		while (!pathQ.isEmpty()) {
			WordPath curr = pathQ.remove();
			int currDist = curr.dist;
			if (currDist > minDist) {
				wordList.remove(curr.word);
				continue;
			}

			for (String neighbor : getNeighbors(curr.word)) {
				if (neighbor.equals(endWord) && currDist <= minDist) {
					LinkedList<String> fullPath = new LinkedList<String>();
					fullPath.add(endWord);
					WordPath prev = curr;
					while (prev != null) {
						fullPath.addFirst(prev.word);
						prev = prev.prev;
					}
					results.add(fullPath);
					minDist = currDist;
					break;
				} else if (wordList.contains(neighbor)) {
					pathQ.add(new WordPath(neighbor, curr, currDist + 1));
				}
			}
			wordList.remove(curr.word);
		}

		return results;
	}

	public List<String> getNeighbors(String word) {
		char[] chars = word.toCharArray();
		ArrayList<String> neighbors = new ArrayList<String>();
		for (int i = 0; i < word.length(); i++) {
			char oldC = chars[i];
			for (char c = 'a'; c <= 'z'; c++) {
				chars[i] = c;
				String newWord = String.valueOf(chars);
				if (!newWord.equals(word)) {
					neighbors.add(newWord);
				}
			}
			chars[i] = oldC;
		}
		return neighbors;
	}

	class WordPath {
		String word;
		WordPath prev;
		int dist;

		public WordPath(String word, WordPath prev, int dist) {
			this.word = word;
			this.prev = prev;
			this.dist = dist;
		}
	}

	class WordNode {
		String word;
		int numSteps;
		WordNode pre;

		public WordNode(String word, int numSteps, WordNode pre) {
			this.word = word;
			this.numSteps = numSteps;
			this.pre = pre;
		}
	}

	public List<List<String>> findLadders2(String start, String end, Set<String> dict) {
		List<List<String>> result = new ArrayList<List<String>>();

		LinkedList<WordNode> queue = new LinkedList<WordNode>();
		queue.add(new WordNode(start, 1, null));

		dict.add(end);

		int minStep = 0;

		HashSet<String> visited = new HashSet<String>();
		HashSet<String> unvisited = new HashSet<String>();
		unvisited.addAll(dict);

		int preNumSteps = 0;

		while (!queue.isEmpty()) {
			WordNode top = queue.remove();
			String word = top.word;
			int currNumSteps = top.numSteps;

			if (word.equals(end)) {
				if (minStep == 0) {
					minStep = top.numSteps;
				}

				if (top.numSteps == minStep && minStep != 0) {
					// nothing
					ArrayList<String> t = new ArrayList<String>();
					t.add(top.word);
					while (top.pre != null) {
						t.add(0, top.pre.word);
						top = top.pre;
					}
					result.add(t);
					continue;
				}

			}

			if (preNumSteps < currNumSteps) {
				unvisited.removeAll(visited);
			}

			preNumSteps = currNumSteps;

			char[] arr = word.toCharArray();
			for (int i = 0; i < arr.length; i++) {
				for (char c = 'a'; c <= 'z'; c++) {
					char temp = arr[i];
					if (arr[i] != c) {
						arr[i] = c;
					}

					String newWord = new String(arr);
					if (unvisited.contains(newWord)) {
						queue.add(new WordNode(newWord, top.numSteps + 1, top));
						visited.add(newWord);
					}

					arr[i] = temp;
				}
			}

		}

		return result;
	}

	/*
	 * TODO some issue with indexing
	 */
	public String minConsecutiveSubstring(String s, String t) {
		HashMap<Character, Integer> map = new HashMap<Character, Integer>();
		int left = 0, right = 0;

		while (map.size() < t.length()) {
			char c = s.charAt(right);
			if (t.indexOf(c) != -1) {
				if (map.containsKey(c)) {
					map.put(c, map.get(c) + 1);
				} else {
					map.put(c, 1);
				}
			}
			right++;
		}

		if (right == s.length()) {
			right--;
		}

		System.out.println(map);

		int minLeft = 0;
		int minRight = right - 1;
		int min = minRight;

		for (int r = right; r < s.length(); r++) {
			char rc = s.charAt(r);
			char lc = s.charAt(left);

			if (map.containsKey(rc)) {
				map.put(rc, map.get(rc) + 1);
				System.out.println(map);
			}

			while (!map.containsKey(lc) || map.get(lc) > 1) {
				left++;
				if (map.containsKey(lc)) {
					map.put(lc, map.get(lc) - 1);
				}
				lc = s.charAt(left);
			}

			if (r - left < min) {
				minLeft = left;
				minRight = r;
				min = r - left;
			}
		}
		return s.substring(minLeft, minRight + 1);
	}

	/*
	 * https://leetcode.com/problems/sort-characters-by-frequency/
	 */
	public String frequencySort(String s) {
		if (s == null) {
			return null;
		}

		if (s.length() <= 1) {
			return s;
		}

		HashMap<Character, Integer> charCounts = new HashMap<Character, Integer>();
		Character[] str = new Character[s.length()];
		int i = 0;
		for (Character c : s.toCharArray()) {
			str[i++] = c;
			if (charCounts.containsKey(c)) {
				charCounts.put(c, charCounts.get(c) + 1);
			} else {
				charCounts.put(c, 1);
			}
		}

		Arrays.sort(str, new CountComparator(charCounts));
		StringBuilder strB = new StringBuilder();
		for (Character c : str) {
			strB.append(c);
		}
		return strB.toString();
	}

	class CountComparator implements Comparator<Character> {
		private HashMap<Character, Integer> charCounts;

		public CountComparator(HashMap<Character, Integer> charCounts) {
			this.charCounts = charCounts;
		}

		@Override
		public int compare(Character a, Character b) {
			return charCounts.get(b).equals(charCounts.get(a)) ? b - a : charCounts.get(b) - charCounts.get(a);
		}
	}
	
	
	/*
	 * Given arrays nums and maxes return an output array which contains counts
	 * of all numbers in nums that are <= every number in maxes
	 */
	public static int[] counts(int[] nums, int[] maxes) {
        Arrays.sort(nums);  // O(nlogn)
        int [] out = new int[maxes.length];
        
        for (int i=0; i<maxes.length; i++) {	// O(m)
            int count = getMaxIndex(nums, maxes[i]);    // O(logn)
            out[i] = count;
        }
        
        return out;
    }

	// Binary search
	public static int getMaxIndex(int [] nums, int target) {
        int l = 0, r = nums.length-1;
        
        while (l <= r) {
            int mid = l + (r-l)/2;
            if (nums[mid] <= target) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        return r + 1;
    }
	
	public String wc(String input) {
		int numWords = 0;
		int numLines = 0;
		
		if (input == null) {
			return numWords+" "+numLines;
		}
		
		numLines++;
		if (input.length() == 0) {
			return numWords+" "+numLines;
		}
		char prev = input.charAt(0);
		for (int i=1; i<input.length(); i++) {
			char curr = input.charAt(i);
			if (!isWordSeparator(prev) && isWordSeparator(curr)) {
				numWords++;
			}
			if (isNewline(prev)) {
				numLines++;
			}
			prev = curr;
		}
		if (!isWordSeparator(prev)) {
			numWords++;
		}
		if (isNewline(prev)) {
			numLines++;
		}
		
		return numWords+" "+numLines;
	}
	
	public boolean isWordSeparator(char character) {
		return (character == ' ' || isNewline(character));
	}
	
	public boolean isNewline(char character) {
		return (character == '\n');
	}
	
	
	public int maxOverlappingIntervals(Interval [] intervals) {
		int count = 0;
		int max = Integer.MIN_VALUE;
		List<Event> events = new ArrayList<Event>();
		for (Interval interval : intervals) {
			Event arrival = new Event(interval.startTime, 'I');
			Event departure = new Event(interval.endTime, 'O');
			events.add(arrival);
			events.add(departure);
		}
		events.sort(new EventComparator());
		
		for (Event event : events) {
			System.out.println(event);
			if (event.type == 'I') {
				count++;
			} else {
				count--;
			}
			if (count > max) {
				max = count;
			}
		}
		
		return max;
	}
	
	class Interval {
		int startTime, endTime;
		
		public Interval(int startTime, int endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}
		
		@Override
		public String toString() {
			return startTime+"-"+endTime;
		}
	}
	
	class Event implements Comparable<Event> {
		int time;
		char type;
		
		public Event(int time, char type) {
			this.time = time;
			this.type = type;
		}
		
		@Override
		public int compareTo(Event o) {
			return (this.time == o.time) ? this.type - o.type : this.time - o.time;
		}
		
		@Override
		public String toString() {
			return time+": "+type;
		}
	}
	
	class EventComparator implements Comparator<Event> {
		@Override
		public int compare(Event o1, Event o2) {
			return (o1.time == o2.time) ? o2.type - o1.type : o1.time - o2.time;  // Output before Input
		}
	}
	
	class MovingAverage {
		LinkedList<Integer> queue;
		double avg;
		int size;
		
		public MovingAverage(int size) {
			this.queue = new LinkedList<Integer>();
			this.size = size;
		}
		
		public double next(int val) {
			if (queue.size() < this.size) {
				int n = queue.size();
				//avg = (avg*n + val) / (n + 1);
				avg = (((double)n/(n + 1)) * avg) + (val / (n + 1));
				queue.offer(val);
				return avg;
			} else {
				int elemRemoved = queue.poll();
				double minus = (double) elemRemoved / this.size;
				queue.offer(val);
				double add = (double) val / this.size;
				avg += (add - minus);
				return avg;
			}
		}
	}
	
	static class Coord {
	  int x, y;
	
	  public Coord(int x, int y) {
	    this.x = x;
	    this.y = y;
	  }
	}
	
	public static Coord[] zerosRect(int [][] mat) {
	  if (mat == null) return null;
	  
	  int m = mat.length;
	  int n = mat[0].length;
	  
	  Coord start = null, end;
	  
	  for (int i=0; i<m; i++) {
	    for (int j=0; j<n; j++) {
	      if (mat[i][j] == 0) {
	        start = new Coord(i,j);
	        break;
	      }
	    }
	    if (start != null) break;
	  }
	  
	  int prevY = start.y;
	  int endY = prevY;
	  for (int i=start.y; i<n; i++) {
	    if (1 == mat[start.x][i]) {
	      break;
	    }
	    prevY = i;
	  }
	  endY = prevY;
	  
	  
	  int prevX = start.x;
	  int endX = prevX;
	  for (int i=start.x; i<m; i++) {
	    if (1 == mat[i][start.y]) {
	      break;
	    }
	    prevX = i;
	  }
	  endX = prevX;
	  
	  end = new Coord(endX, endY);
	  
	  Coord output[] = new Coord[2];
	  output[0] = start;
	  output[1] = end;
	  
	  return output;
	}
	
	/*
     * A rudimentary parser for this grammar:
     * expr: <int> | ( <op> <expr>+ )
     * op: + | *
     * int: 0-9+
     * 
     * Note: Assume well formed input, including spaces
     */
    public static int eval(String s) {
        //System.out.println("s: " + s);
        if (s == null) {
            return 0;
        }
        
        if (isNumber(s)) {
            return Integer.parseInt(s);
        }
        
        char first = s.charAt(0);
        if (first == '(') {
            String inner = s.substring(1, s.length()-1).trim();
            return eval(inner);
        }
        
        if (first == '+') {
            int sum = 0;
            List<String> terms = getTerms(s.substring(1));
            for (String term : terms) {
                sum += eval(term);
            }
            return sum;
        } else if (first == '*') {
            int prod = 1;
            List<String> terms = getTerms(s.substring(1));
            for (String term : terms) {
                prod *= eval(term);
            }
            return prod;
        }
        
        return 0;  // Error case; shouldn't ever reach here
    }
    
    private static boolean isNumber(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    private static List<String> getTerms(String s) {
        List<String> terms = new ArrayList<String>();
        String [] tokens = s.trim().split(" ");
        int i = 0;
        while (i < tokens.length) {
            if (isNumber(tokens[i])) {
                terms.add(tokens[i]);
            } else {  // This is a parenthesis
                StringBuffer sb = new StringBuffer();
                int startParen = i;
                sb.append(tokens[i]);
                i++;
                int countParens = 1;
                while (i < tokens.length) {
                    sb.append(" " + tokens[i]);
                    if ("(".equals(tokens[i])) {
                        countParens++;
                    } else if (")".equals(tokens[i])) {
                        countParens--;
                        if (countParens == 0) {
                            // Bingo; found an expr
                            terms.add(sb.toString());
                            break;
                        }
                    }
                    i++;
                }
            }
            i++;
        }
        return terms;
    }


	public static void main(String args[]) {
		Practice p = new Practice();
		System.out.println(p.parseIp("103721"));
		System.out.println(p.parseIp("25525511135"));
		System.out.println(p.parseIp("48576758"));
		System.out.println(p.parseIp("3456"));
		System.out.println(p.parseIp("000000"));
		System.out.println(p.parseIp("847"));
		
		System.out.println();

		int[][] matrix = new int[3][10];
		matrix[1][2] = 1;
		matrix[1][5] = 1;
		matrix[1][8] = 1;
		matrix[1][7] = 2;
		System.out.println(p.longestDist(matrix, 0, 0));
		
		System.out.println();

		int[][] mat = new int[4][4];
		System.out.println(p.numPathsTopDown(mat));
		System.out.println(p.numPathsBottomUp(mat));

		System.out.println(p.numValidAlphaEncoding("1234"));
		System.out.println(p.numValidAlphaEncoding("100034"));

		System.out.println(p.matrixMultParen("ABCD", new HashMap<String, HashSet<String>>()));

		int[] ratings = { 1, 4, 3, 3, 3, 1 };
		int[] ratings2 = { 2, 3, 1, 6, 5, 4 };
		int[] ratings3 = { 1, 2, 3, 5 };
		System.out.println(p.candy(ratings3));

		int[] a = { 6, 0, 6, 4, 0, 0, 4, 67, 0, 1, 4, 0 };
		int[] a2 = { 1, 9, 8, 4, 0, 0, 2, 7, 0, 6, 0, 9, 0 };
		p.moveZerosToRight(a2);
		for (int i = 0; i < a2.length; i++) {
			System.out.print(a2[i] + " ");
		}
		System.out.println();

		int[] houses = { 10, 5, 30, 25, 15, 30 };
		System.out.println(p.houseRobber(houses));

		System.out.println();

		Set<String> dict = new HashSet<String>();
		dict.add("hot");
		dict.add("dog");
		dict.add("dot");
		dict.add("lot");
		dict.add("log");
		System.out.println(p.findLadders("hit", "cog", dict));

		Set<String> dict2 = new HashSet<String>();
		dict2.add("ted");
		dict2.add("tex");
		dict2.add("red");
		dict2.add("tax");
		dict2.add("tad");
		dict2.add("den");
		dict2.add("rex");
		dict2.add("pee");
		System.out.println(p.findLadders("red", "tax", dict2));
		System.out.println();

		System.out.println(p.minConsecutiveSubstring("adobecodebanc", "abc"));
		System.out.println(p.minConsecutiveSubstring("aaaabdac", "abc"));
		System.out.println(p.minConsecutiveSubstring("aaaabdacftg", "abc"));
		System.out.println();
		
		int [] nums = {5,5,5,1,3,4,4,9,7,4,5,5,3};
		int [] maxes = {3,1,2,8,5,4,0};
		int [] result = counts(nums, maxes);
		for (int i=0; i<result.length; i++) {
			System.out.print(result[i]+" ");
		}
		System.out.println();
		
		System.out.println(p.wc("Harsh Mehta"));
		System.out.println(p.wc("Hi this is a big sentence. with / + - symbols."));
		System.out.println(p.wc(" Harsh    Mehta "));
		System.out.println(p.wc("Harsh\nMehta abc\n"));
		System.out.println(p.wc("Harsh\nMehta abc"));
		System.out.println(p.wc("\n\n\n"));
		System.out.println(p.wc("\np\n"));
		System.out.println(p.wc("\n\\n"));
		System.out.println(p.wc("harsh\nmehta"));
		System.out.println(p.wc("\n"));
		
		System.out.println();
		
		int n = 25;
		Interval [] intervals = new Interval[n];
		for (int i=0; i<n; i++) {
			int arr = ThreadLocalRandom.current().nextInt(1, 50);
			int dep = arr + ThreadLocalRandom.current().nextInt(1, 25);
			Interval interval = p.new Interval(arr, dep);
			intervals[i] = interval;
			System.out.println(interval);
		}
		System.out.println(p.maxOverlappingIntervals(intervals));
		
		System.out.println();
		
		MovingAverage m = p.new MovingAverage(4);
		int [] nos = {10,20,30,20,30,12,34,23,4,2,67};
		for (int i : nos) {
			System.out.println("Adding " +i+ "; Average: " + m.next(i));
		}
		
		System.out.println();
		
		int[][] image = {
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 0, 0, 0, 1},
			{1, 1, 1, 0, 0, 0, 1},
			{1, 1, 1, 1, 1, 1, 1},
		};
	    Coord [] out = zerosRect(image);
	    System.out.println(out[0].x + " " + out[0].y);
	    System.out.println(out[1].x + " " + out[1].y);
	    
	    String [][] testCases = {
                { "( + 1 2 ( + 3 ( * 2 5 9 ) ) 4 5 ( * 4 32 ) ( ( ( + 3 ) ) ) )", "236" },  // kitchen sink
                { "(* 3 (+ 4 5 6 ) 3)", "135" },  // tryna see how robust this algo; handling lack of spaces
                { "( * 3 ( + 8 ) )", "24" },  // single operands
                { "456", "456" },  // int
                { "(42)", "42" }  // (int)
        };
	    
	    System.out.println();
        
        System.out.println("Running eval():");
        for (String[] testCase : testCases) {
            String expected = testCase[1];
            int actual = eval(testCase[0]);
            System.out.print(expected.equals(String.valueOf(actual)) ? "PASS! " : "FAIL! ");
            System.out.println("Expected: " + expected + "; Actual: " + actual);
        }
	}
}
