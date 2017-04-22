import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Combinatorics {
	
	static int counter;
	
	/*
	 * Runtime: O(n!)
	 * T(N) = N*T(N-1)
	 */
	public static void allPerms(char [] arr, int index) {
		if (index == arr.length) {
			print(arr);
			return;
		}
		for (int i=index; i<arr.length; i++) {  // Loop from current index through input length
			swap(arr, i, index);
			allPerms(arr, index+1);  // Branch off from index of current *function* invocation, not current loop (narrow by index)
			swap(arr, index, i);
		}
	}
	
	/*
	 * Runtime: O(2^n)
	 * T(N) = T(N-1) + T(N-2) + T(N-3) + ...
	 * T(N-1) = T(N-2) + T(N-3) + T(N-4) + ...
	 * 
	 * Therefore,
	 * T(N) = T(N-1) + T(N-1)
	 * 		= 2*T(N-1)
	 * i.e. it doubles every iteration => 2^n
	 */
	public static void powerset(int[] items, int s, Stack<Integer> res) {
	     System.out.println(res);

	     for(int i = s; i < items.length; i++) {  // Loop from current index through input length
	          res.push(items[i]);
	          powerset(items, i+1, res);  // Branch off from index of current *loop* invocation (narrow by index + loop)
	          res.pop();
	     }
	}
	
	/*
	 * Runtime: O(2^n)
	 * T(N) = 2*T(N-1)
	 * i.e. it doubles every iteration => 2^n
	 * Two fixed calls per invocation (n times) => 2^n
	 */
	public static void powerset2(int [] items, int level, Stack<Integer> res) {
		if (level >= items.length) {
			System.out.println(res);
			return;
		}
		
		// Pick current item
		res.push(items[level]);
		powerset2(items, level+1, res);  // Branch off next level (item)
		res.pop();
		
		// Don't pick current item
		powerset2(items, level+1, res);  // Branch off next level (item)
	}
	
	/*
	 * Runtime: O(n!): growing list; each iteration: num of elems * size of list
	 * N*(N-1)*(N-2)... => N times => N!
	 */
	public static ArrayList<ArrayList<Character>> allPermsIter(char [] arr) {
		ArrayList<ArrayList<Character>> result = new ArrayList<ArrayList<Character>>();
		result.add(new ArrayList<Character>());
		
		for (int i=0; i<arr.length; i++) {
			ArrayList<ArrayList<Character>> curr = new ArrayList<ArrayList<Character>>();
			for (ArrayList<Character> list : result) {
				for (int j=0; j<list.size()+1; j++) {
					list.add(j, arr[i]);
					ArrayList<Character> temp = new ArrayList<Character>(list);
					curr.add(temp);
					list.remove(j);
				}
			}
			result = new ArrayList<ArrayList<Character>>(curr);
		}
		return result;
	}
	
	/*
	 * Runtime: O(2^n): growing list which starts empty and doubles every iteration
	 */
	public static <T> ArrayList<ArrayList<T>> allSubsets(ArrayList <T> arr) {
		ArrayList<ArrayList<T>> allSubsets = new ArrayList<ArrayList<T>>();
		if (arr.size() == 0) {
			ArrayList<T> emptyList = new ArrayList<T>();
			allSubsets.add(emptyList);
			return allSubsets;
		}
		
		T last = arr.remove(arr.size()-1);
		ArrayList<ArrayList<T>> prevSubsets = allSubsets(arr);
		allSubsets.addAll(prevSubsets);
		for (ArrayList<T> prevSubset : prevSubsets) {
			ArrayList<T> newSubset = new ArrayList<T>();
			newSubset.addAll(prevSubset);
			newSubset.add(last);
			allSubsets.add(newSubset);
		}
		return allSubsets;
	}
	
	/*
	 * Runtime: theoretically it should be O(min(n^k, n^(n-k)), but this algo here has a tighter
	 * bound of O(min(n^k, 2^n)). The main loop mimics a powerset loop. k is used to trim the
	 * recursion tree - hence the variable runtime.
	 * 
	 * Summation of combinations => 2^n (all combinations = all subsets)
	 * 
	 * Refs:
	 * https://en.wikipedia.org/wiki/Combination#Number_of_k-combinations_for_all_k
	 * http://stackoverflow.com/questions/24643367/whats-time-complexity-of-this-algorithm-for-finding-all-combinations
	 */
	//public static int c=0;
	public static void nChooseK(char [] arr, int level, int k, int startPos, char[] out) {
		//System.out.println(++c);
		if (level == k) {
			print(out);
			return;
		}
		for (int j=startPos; j<arr.length; j++) {
			out[level] = arr[j];
			nChooseK(arr, level+1, k, j+1, out);
		}
	}
	
	/*
	 * Runtime: Number of permutations = O(NpK) but observed is:
	 * T(NpK) + T(Np(K-1)) + T(Np(K-2)) + ... 1, i.e. summation of all O(NpI) where I = 0 to K
	 * 
	 * Refs:
	 * http://math.stackexchange.com/questions/161314/what-is-the-sum-of-following-permutation-series-np0-np1-np2-cdots-npn/161317
	 * Sequence: https://oeis.org/A000522
	 */
	//public static int c=0;
	public static void nPermuteK(char [] arr, int level, int k, char[] out) {
		//System.out.println(++c);
		if (level == k) {
			print(out);
			return;
		}

		for (int j=level; j<arr.length; j++) {
			out[level] = arr[j];
			swap(arr, level, j);
			nPermuteK(arr, level+1, k, out);
			swap(arr, j, level);
		}
	}
	
	public static void print(char [] arr) {
		System.out.print(++counter+": ");
		for (char c : arr) {
			System.out.print(c);
		}
		System.out.println();
	}
	
	public static void swap(char [] arr, int i, int k) {
		char temp = arr[i];
		arr[i] = arr[k];
		arr[k] = temp;
	}
	
	public static Set<String> nearbyWords(String word) {
		return nearbyWords(word.toCharArray(), 0);
	}
	
	private static Set<String> nearbyWords(char [] word, int index) {
		if (index == word.length) {
			Set<String> set = new HashSet<String>();
			//System.out.println(String.valueOf(word));
			if (isWord(String.valueOf(word))) {
				set.add(String.valueOf(word));
			}
			return set;
		}
		
		Character c = word[index];
		Set<Character> nearby = getNearbyChars(c);
		Set<String> candidateWords = new HashSet<String>();
		for (Character nearbyChar : nearby) {
			word[index] = nearbyChar;
			candidateWords.addAll(nearbyWords(word, index+1));
		}
		return candidateWords;
	}
	
	private static Set<Character> getNearbyChars(Character character) {
		HashSet<Character> characters = new HashSet<Character>();
        if (character == 'g') {
            characters.add('g');
            characters.add('h');
            characters.add('f');
        } else {
            characters.add('i');
            characters.add('o');
            characters.add('k');
        }
        return characters;
	}
	
	private static boolean isWord(String word) {
		if (word.equals("hi") || word.equals("go")) {
			return true;
		}
		return false;
	}
	
	public static String printArray(int [] arr) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<arr.length; i++) {
			sb.append(arr[i]);
			sb.append(", ");
		}
		String array = (sb.length() == 0) ? sb.toString() : sb.substring(0,  sb.length()-2);
		return "["+array+"]";
	}
	
	public static String printArray(char [] arr) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<arr.length; i++) {
			sb.append(arr[i]);
			sb.append(", ");
		}
		String array = (sb.length() == 0) ? sb.toString() : sb.substring(0,  sb.length()-2);
		return "["+array+"]";
	}
	
	public static void main(String [] args) {
		int [] items = {1,2,3,4,5};
		Stack<Integer> st = new Stack<Integer>();
		System.out.println("Power set of "+printArray(items)+":");
		powerset(items, 0, st);
		System.out.println();
		st = new Stack<Integer>();
		System.out.println("Power set 2 of "+printArray(items)+":");
		powerset2(items, 0, st);
		System.out.println();
		char [] arr = {'a', 'b', 'c', 'd', 'e'};
		counter = 0;
		System.out.println("All permutations of "+printArray(arr)+":");
		allPerms(arr, 0);
		System.out.println();
		counter = 0;
		System.out.println("All permutations (iterative) of "+printArray(arr)+":");
		System.out.println(allPermsIter(arr));
		System.out.println();
		//allSubsets(arr);
		int k = 3;
		char [] out = new char[k];
		System.out.println("nChooseK ("+arr.length+"C"+k+") of "+printArray(arr)+":");
		nChooseK(arr, 0, k, 0, out);
		System.out.println();
		counter = 0;
		System.out.println("nPermuteK ("+arr.length+"P"+k+") of "+printArray(arr)+":");
		nPermuteK(arr, 0, k, out);
		System.out.println();
		counter = 0;
		String nearbyWordsString = "gi";
		System.out.println("Nearby words of "+nearbyWordsString+":");
		System.out.println(nearbyWords(nearbyWordsString));
		System.out.println();
		ArrayList<Character> set = new ArrayList<Character>();
		set.add('a');
		set.add('b');
		set.add('c');
		set.add('d');
		System.out.println("All subsets of "+set+":");
		System.out.println(allSubsets(set));
		System.out.println();
	}
}
