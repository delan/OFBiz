package org.ofbiz.designer.util;

import java.util.*;
import java.io.*;

public class ImapGenerator{
	public static void main(String[] args) throws Exception{
		if (args.length != 2){
			System.err.println("format <source file> <destination file>");
			return;
		}
		//DataInputStream is = new DataInputStream(new FileInputStream(args[0]));
		BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
		DataOutputStream os = new DataOutputStream(new FileOutputStream("args[1]"));
		
		os.writeBytes("<img src=c:\\temp\\world.gif usemap=#hiermap>");
		os.writeBytes("<map name=\"hiermap\">");
		
		while (true){
			String line = is.readLine();
			//System.err.println("line is >" + line + "<");
			if (line == null) break;
			if (!line.startsWith("rectangle (")) continue;
			
			line = "<area shape=RECT COORDS=\"" + line.substring("rectangle (".length());
			
			
			line = line.substring(0, line.indexOf(") (")) + "," + line.substring(line.indexOf(") (") + ") (".length(), line.length());
			line = line.substring(0, line.indexOf(") ")) + "\" HREF=" + line.substring(line.indexOf(") ") + ") ".length(), line.length());
			line += ">";
			os.writeBytes(line + "\n");
		}

		os.writeBytes("</map>");
		os.close();
	}
}
