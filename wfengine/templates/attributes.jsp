<%	
	for (Iterator it = getAttributes(clazz).iterator() ;it.hasNext(); ) {
		MBase m = (MBase)it.next();
		String attrName;
		if (isFinal(m)) {
			attrName =  getName(m).toUpperCase();
		} else {
		 	attrName =  fd( getName(m));
		 }
		if (attrName.length()==0) {
			continue;
		}
		String attrType =  getTypeName(m);
		boolean isStatic = "True".equals(getTaggedValueAsString(m, "RationalRose$Java:Static"));
		if (!isFinal(m)) {%>
	// Attribute instance '<%=attrName%>'
	private <% if (isStatic) {%>static <%}%><%=attrType%> <%=attrName%>;
	<%} else { // HACK!!!!%>
	/** Constant <%=attrName%>
	<%=m.documentation%> */
	public static final <%=attrType%> _<%=attrName%> = <%=getInitialValue(m)%>;
	/** String representation of constant <%=attrName%> 
	<%=m.documentation%> */
	public static final String <%=attrName%> = "<%=attrName.substring(3)%>";
	<%} //if
	// while(it...)
	}
%>
	