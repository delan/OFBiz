	// Methoden
<%
		MBase mo;
		Collection ops = MetaModel.getOperations(clazz);
		Iterator ops_it = ops.iterator();
		while (ops_it.hasNext()) {
			mo= (MBase)ops_it.next();
			String retTypeName = MetaModel.getReturnTypename(mo);
			Object retType = MetaModel.getReturnType(mo);
			String parList = MetaModel.getParameterListAsString(mo);
			Collection pars = MetaModel.getParameterList(mo);
			Iterator itp = null;
			if (pars!=null)  {
				itp = pars.iterator();
			}
			String opname = MetaModel.getName(mo);
            boolean isStatic = "True".equals(MetaModel.getTaggedValueAsString(mo, "RationalRose$Java:Static"));
			%>
	/**	 
	 * <%=getDocumentation(mo).length()>0 ? getDocumentation(mo) : "Methode "+opname%><%-#for p in mo.parameterList%>
	 * @param <%=name%> <%=getDocumentation(p).length()>0 ? getDocumentation(p) : "Wert für "+name.substring(1)%><%} if (retType!=null) {%>
	 * @return <%=retTypeName%><%}%>
	 */
	public <% if (isStatic) {%>static <%}%><%=retTypeName%> <%=opname%>(<%=parList%>) <%=mo.exceptionListAsString%>;
<%	}// while (ops...)

%>
