package org.ofbiz.designer.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Stack;

/**
 * The following class is used to spit out "pretty" source-code
 * It eliminates having to keep track of proper indenting and carriage returns
 * To use it, wrap DataOutputStream as follows
 * 
 * CodeGenerator cg = new CodeGenerator(DataOutputStream ds)
 * cg.begin("public static void main(String[] args)"
 *	cg.println("System.err.println(\"hello\");");
 * cg.end();
 */
public class CodeGenerator {
    public static final String JAVA = "Java";
    public static final String IDL = "Idl";
    public static final String XML = "Xml";
    private String mode = null;
    private Stack stack = new Stack();

    private DataOutputStream ds = null;
    // indent keeps track of the current value of the indentation
    // in the java code being written out
    private BlankString indent = new BlankString(0);

    // the following keeps track of the first use of a "print" method so that
    // indent can be prepended to the string
    // subsequent prints (being on the same line) do not prepend an indentation
    private boolean firstPrint = true;

    public CodeGenerator(DataOutputStream dsIn, String modeIn) {
        if(modeIn.equals(JAVA) || modeIn.equals(XML)  || modeIn.equals(IDL))
            mode = modeIn;
        else
            throw new RuntimeException("Inappropriate mode !!");
        ds = dsIn;
    }

    public void close() throws IOException{
        ds.close();
    }

    /**
     * This method prints the parameter string, followed by an open brace
     * followed by a carriage return.  It then increases indentation for subsequent
     * lines.
     * For example
     * cg.begin("try") 
     * cg.println("doit();");
     *		would actually print out
     *		try{
     *			doit();
     * For XML
     * cg.begin("Task id=\"jfacc\"") 
     * cg.begin("Name");
     * cg.println("jfacc");
     * cg.end()
     * cg.end()
     *		would actually print out
     *		<Task id=jfacc>
     *			<Name>
     *				jfacc
     *			</Name>
     *		</Task>
     */
    public void begin(String str) throws IOException{
        if(mode.equals(JAVA) || mode.equals(IDL))
            ds.writeBytes(indent + str + "{\n");
        else if(mode.equals(XML)) {
            ds.writeBytes(indent + "<" + str + ">\n");
            // record the nodename in stack so it can be inserted appropriately
            // in the end() method.
            String nodeName = null;
            int spaceIndex = str.indexOf(" ");
            if(spaceIndex != -1)
                nodeName = str.substring(0, str.indexOf(" "));
            else
                nodeName = str;
            stack.push(nodeName);
        }
        indent.tabIn();
    }

    /**
     * This method decreases indentation and then prints out a closing brace.
     */
    public void end() throws IOException{
        indent.tabOut();
        if(mode.equals(JAVA))
            ds.writeBytes(indent + "}\n");
        else if(mode.equals(IDL))
            ds.writeBytes(indent + "};\n");
        else if(mode.equals(XML))
            ds.writeBytes(indent + "</" + (String)stack.pop() + ">\n");
    }

    public void println(String str) throws IOException{
        ds.writeBytes(indent + str + "\n");
        firstPrint = true;
    }

    /**
     * prints out the parameter string.  When following a println statement, it prepends
     * the indent
     */
    public void print(String str) throws IOException{
        if(firstPrint)
            ds.writeBytes(indent + str);
        else
            ds.writeBytes(str);
        firstPrint = false;
    }

    public void print(int x) throws IOException{
        print(new Integer(x).toString());
    }

    public void println(int x)throws IOException{
        println(new Integer(x).toString());
    }

    public void println() throws IOException{
        ds.writeBytes("\n");
        firstPrint = true;
    }
}