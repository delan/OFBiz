<%for (Iterator it = getAttributes(clazz).iterator() ;it.hasNext(); ) {
		boolean setter = true;
		MBase m = (MBase)it.next();
		boolean isStatic = "True".equalsIgnoreCase(getTaggedValueAsString(m, "RationalRose$Java:Static"));
		String attrName =  getName(m);
		
		if (attrName.length()==0 || isFinal(m)) {
			continue;
		}

		if (attrName.charAt(0)=='/') { // Abgeleitetes Attribut?
			attrName = attrName.substring(1);
			setter = false;
		}
		String attrNameFU =  fu(attrName);
		String attrType =  getTypeName(m);
		String instName =  fd(attrName);
		String id =  getId(m);
%>		
	/**
	 * Getter for attribute '<%=attrName%>'
	 * <%=m.documentation%>
	 * @return Value of attribute <%=attrName%>
	 */
	public <% if (isStatic) {%>static <%}%><%=attrType%> get<%=attrNameFU%>()  {
		return <%=instName%>;
	}
	<%if (setter) {%>
	/**
	 * Setter for attribute '<%=attrName%>'
	 * <%=m.documentation%>
	 * @param p<%=attrNameFU%> Neuer Wert des Attributes <%=attrName%>
	 */
	public <% if (isStatic) {%>static <%}%>void set<%=attrNameFU%>(<%=attrType%> p<%=attrNameFU%>)  {
		if (<%=instName%> == p<%=attrNameFU%>) return;		
		if ( !notifyAttributeChange<%=attrNameFU%>( p<%=attrNameFU%> ) ) return;
		<%=instName%> = p<%=attrNameFU%>;
	}
	
	/**
	 * This method is called, before the attribute '<%=attrNameFU%>' is set to a new
	 * value.
	 * @param p<%=attrNameFU%> New Value for attribute '<%=attrNameFU%>'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChange<%=attrNameFU%>(<%=attrType%> p<%=attrNameFU%>) {		
		return true;
	}
	<%}%>
<%
	// for(it...)
	}
%>