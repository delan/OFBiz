<%
		MBase mo;
		Collection ops = getOwnOperations(clazz);
		Iterator ops_it = ops.iterator();

		while (ops_it.hasNext()) {
			mo= (MBase)ops_it.next();
			String st = getStereotype(mo);
			if (st.length()>0) {
				continue;
			}
			String retTypeName = getReturnTypename(mo);
			MBase retType = getReturnType(mo);
			String parList = getParameterListAsString(mo);
			Collection pars = getParameterList(mo);
			Iterator itp = null;
			if (pars!=null)  {
				itp = pars.iterator();
			}
			String opname = getName(mo);
			String visibility = getVisibility(mo);
			String documentation = getDocumentation(mo).equals("")?"Methode " + opname:getDocumentation(mo);
            		boolean isStatic = "True".equals(getTaggedValueAsString(mo, "RationalRose$Java:Static"));
%>	/**
	 * <%=documentation%><%-#for p in mo.parameterList%>
	 * @param <%=name%> <%=getDocumentation(p).length()>0 ? getDocumentation(p) : "Wert für "+name.substring(1)%><%} if (retType!=null) {%>
	 * @return <%=retTypeName%><%}%>
	 */
	<%=visibility%> <% if (isStatic) {%>static <%}%><%=retTypeName%> <%=opname%>(<%=parList%>) <%=getExceptionListAsString(mo)%>{
		<%if (retType !=null) %>return <%=getReturnValue(retType)%>;
		// !!NO_CODE!!
	}
	
<%	}// while (ops...)

%>