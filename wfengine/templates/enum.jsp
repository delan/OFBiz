<%@include file="params.jsp" %><%@include file="copyright.jsp" %>

package <%=getPath()%>;

<%@include file="imports.jsp" %>

/**
 * <%=getDocumentation(element).length()>0 ? getDocumentation(element) : "Enumeration "+classname%>
 * @author <%=System.getProperty("user.name")%>
 * @version 1.0
 */

public class <%=classname%> implements Serializable, Comparable {

	<%MBase m=null;
	Collection attr = getAttributes(clazz);
	Iterator cattr_it = attr.iterator();
	int i = 0;
	int max = attr.size();
	while (cattr_it.hasNext()) {
		++i;
		m = (MBase)cattr_it.next();
		String attrName = fd(getName(m));
		String attrNameFU = fu(getName(m));
		String attrNameUP = attrNameFU.toUpperCase();
		String initialValue = getInitialValue(m);
		if (attrName.length()==0) {
			break;
		}
		String attrType = getTypeName(m);

%>
	/** Index of value '<%=attrName%>' */
	public static final int <%=attrNameUP%> = <%=""+i%>;	
	/**
	 * <%=m.documentation%>
	 */
	public static final <%=classname%> <%=attrNameFU%> = new <%=classname%>(<%=attrNameUP%>);
	/** String representation of '<%=attrName%>' */
	<%if (initialValue!=null && initialValue.length()>0) {%>
	public static final String _<%=attrNameFU%> = <%=initialValue%>;
	<%} else {%>
	public static final String _<%=attrNameFU%> = \"<%=attrNameFU%>\";
	<%}}%>
	
	public static final <%=classname%> NO_VALUE = new <%=classname%>();
	public static final int NOT_INITIALIZED = 0;
	public static final int MAXIMUM = <%=""+max%>;
	
	private int value;
	private static final <%=classname%>[] INSTANCES = {
		NO_VALUE<%if (max>0) {%>,<%}
	cattr_it = attr.iterator();
	while (cattr_it.hasNext()) {
		m = (MBase)cattr_it.next();
		String attrNameFU = fu(getName(m));
		if (attrNameFU.length()==0) {
			continue;
		}

%>		
		<%=attrNameFU%><%if (cattr_it.hasNext()) {%>,<%}
}%>
	};

	private static final String[] VALUES = {
		\"?\"<%if (max>0) {%>,<%}
	cattr_it = attr.iterator();
	while (cattr_it.hasNext()) {
		m = (MBase)cattr_it.next();
		String attrNameFU = fu(getName(m));
		if (attrNameFU.length()==0) {
			continue;
		}
		String attrType = getTypeName(m);

%>
    		_<%=attrNameFU%><%if (cattr_it.hasNext()) {%>,<%}
}%>
	};

	/**
	 * Creates an uninitialized enumeration of <%=classname%>
	 */
	private <%=classname%>() {
		this( NOT_INITIALIZED );
	}

	/**
	 * Constructor with predefined value
	 * @param pValue index of instance (equal to INSTANCES[ pValue ])
	 */
	private <%=classname%>(int pValue) {
		if (pValue < 0 || pValue > MAXIMUM) {
			throw new IllegalArgumentException(\"Value\"+pValue+\" is not allowed for enumeration <%=classname%>, valid range is 0..\"+MAXIMUM);
		}
		value = pValue;
	}

	/**
	 * Returns the actual index of the enumeration type
	 * @return actual index of the enumeration type
	 */
	public int getIndex() {
		return value;
	}

	/**
	 * String representation of enumeration '<%=classname%>'
	 * @return '<%=classname%>: <value as string>'
	 */
	public String toString() {
		return "<%=classname%>:" + getValue();
	}

	/**
     	 * Returns the verbose value of enumeration <%=classname%>
     	 * @return String value of current <%=classname%> instance
     	 */
	public String getValue() {
		if (value > 0 && value <= MAXIMUM) {
			return VALUES[ value ];
		} else {
			throw new IllegalStateException(\"Value \"+
				value+\" is not allowed for enumeration <%=classname%>, valid range is 0..\"+MAXIMUM);
		}
	}

	/**
 	 * List of all values as text
	 * @return List of all values 
	 */
	public static String[] getValues() {
		return VALUES;
	}

	/**
 	 * List of all instances
 	 * @return List of all instances
	 */
	public static <%=classname%>[] getInstances() {
		return INSTANCES;
	}
	
	/**
	 * Gets the corresponding instance of pValue. If pValue is invalid, an
	 * IllegalArgumentException is thrown.
	 * @return corresponding instance (equal to INSTANCES[ pValue ])
	 */
	public static <%=classname%> getInstance(int pValue) {
		if (pValue > 0 && pValue <= MAXIMUM) {
			return INSTANCES[ pValue ];
		} else {
			throw new IllegalArgumentException(\"Value \"+
				pValue+\" is not allowed for enumeration <%=classname%>, valid range is 0..\"+MAXIMUM);
		}
	}
	
	/*
	 * Compare this instance with another object
	 * @param pObject to compare with
	 * @return true, if 1) Object is of type <%=classname%> and 2) values are equal
	 */
	public boolean equals(Object pObject) {
		return pObject != null &&
		   getClass().getName().equals( pObject.getClass().getName() ) ?
		       value == ( (<%=classname%>) pObject ).value:
		       false;
	}
	
	/*
	 * Compare this instance with another object
	 * @param pObject to compare with
	 * @return 0, if pObject is equal to 'this', <p>>0 , if this.value > pObject.value <p>else a negative value
	 */
	public int compareTo(Object pObject) {
		return pObject != null &&
		   getClass().getName().equals( pObject.getClass().getName() ) ?
		       value - ( (<%=classname%>) pObject ).value:
		       -1;
	}
}
<%!	
public void addToImports()  {
	super.addToImports();	
	addToImport("java.io.Serializable");	
}
%>
<%@include file="utilities.jsp" %><%@include file="metamodel.jsp" %>