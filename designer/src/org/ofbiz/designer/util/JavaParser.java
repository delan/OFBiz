package org.ofbiz.designer.util;

import java.io.File;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

class JavaParser {
	private File javaFile = null;
	private int numLines = 0;
	private String[] lines = null;

	public JavaParser(String fileName) throws IOException{
		javaFile = new File(fileName);
		if (!javaFile.exists()){
			throw new IOException("Non-existent file " + fileName);
		}

		BufferedReader ds = new BufferedReader(new InputStreamReader(new FileInputStream(javaFile)));
		int actualLastLine = numLines;
		while (true){
			String tempString = ds.readLine();
			if (tempString == null)
				break;
			else{
				numLines++;
				if (tempString.length() != 0)
					actualLastLine = numLines;
			}
		}
		numLines = actualLastLine;
		lines = new String[numLines];
		ds = new BufferedReader(new InputStreamReader( new FileInputStream(javaFile)));
		for (int i=0; i<numLines; i++){
			lines[i] = ds.readLine();
		}
	}

	public int getNumLines(){
		return numLines;
	}

	public String getLineAt(int index){
		return lines[index];
	}

	public boolean containsString(String subString){
		for (int i=0; i<lines.length; i++){
			if (lines[i].indexOf(subString) != -1)
				return true;
		}
		return false;
	}

	public boolean containsStringUnCommented(String subString){
		for (int i=0; i<lines.length; i++){
			int tempIndex = lines[i].indexOf(subString);
			if (tempIndex != -1){ // line contains subString
				int lineCIndex = lines[i].lastIndexOf("//");
				int blockCIndex = lines[i].lastIndexOf("*/");
				if (lineCIndex != -1 && blockCIndex == -1) 
					continue;
				else if (lineCIndex != -1 && blockCIndex != -1 && lineCIndex > blockCIndex) 
					continue;
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws IOException {
		if (args.length == 0){
			System.err.println("fileName : ");
			byte[] tempArgs = new byte[100];
			int temp = System.in.read(tempArgs);
			args = new String[1];
			args[0] = new String(tempArgs, 0, temp-2);
			args[0].trim();
			System.err.println("args[0] length is " + args[0].length());
			System.err.println("temp is " + temp);
		}
		JavaParser test = new JavaParser(args[0]);
		System.err.println("numlines is " + test.getNumLines());
		System.err.println("line 28 is " + test.getLineAt(28));
		System.err.println("contains \"import\" is " + test.containsString("import"));
		System.err.println("contains \"important\" is " + test.containsString("important"));
		System.err.println("contains \"ds\" is " + test.containsString("ds"));
	}
}

interface JavaParserInterface {
	int numLines(); // number of lines total
	int numCommentLines(); // number of pure comment lines
	int numMethods(); // number of methods
	int numFields(); // number of fields
	int numImports(); // number of import statements

	Vector getMethods();
	Vector getFields();
	Vector getInterfaces();
	Vector getImports();
	
	String getPackage();
	String getParent();
	String getAccessModifier();
	String getLineAt(int line);
	
	JavaMethodInterface getMethodAt(int methodIndex);
	JavaFieldInterface getFieldAt(int fieldIndex);
	
	void format(); // indents code appropriately
}

interface JavaMethodInterface{
	int getFirstLine();
	int getLastLine();
}

interface JavaFieldInterface{
}

interface JavaCommentInterface{
}