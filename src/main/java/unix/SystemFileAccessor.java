package unix;

import java.io.*;
import java.util.*;

public class SystemFileAccessor {

	public static List<String> load(SystemFile sf) {

		String fileName = sf.getName();
		File f = new File(fileName);
		BufferedReader is = null;
		try {
			is = new BufferedReader(new FileReader(f));
			List<String> ret = new ArrayList<String>();
			String line;
			while ((line = is.readLine()) != null) {
				ret.add(line);
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException("I/O Error on file " + fileName, e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e2) {
					System.out.println("ERROR IN CLOSE: " + e2);
				}
			}
		}
	}
	
	public static void store(SystemFile sf, List<String> contents) {
		
		String fileName = sf.getName();
		File f = new File(fileName);
		PrintWriter p = null;
		try {
			p = new PrintWriter(new FileWriter(f));
			for (String line : contents) {
				p.println(line);
			}
			return;
		} catch (IOException e) {
			throw new RuntimeException("I/O Error on file " + fileName, e);
		} finally {
			if (p != null) {
				p.close();
			}
		}
	}
}
