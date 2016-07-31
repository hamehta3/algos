import java.util.ArrayList;
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
	
	public static void main(String args[]) {
		Practice p = new Practice();
		System.out.println(p.parseIp("103721"));
		System.out.println(p.parseIp("25525511135"));
		System.out.println(p.parseIp("48576758"));
		System.out.println(p.parseIp("3456"));
		System.out.println(p.parseIp("000000"));
		System.out.println(p.parseIp("847"));
	}
}
