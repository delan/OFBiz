
<%Iterator states = UtilMisc.toIterator(delegator.findByAnd("Geo", UtilMisc.toMap("geoTypeId", "STATE"), UtilMisc.toList("geoName")));%>
<%while(states != null && states.hasNext()){%><%GenericValue state = (GenericValue)states.next();%>
    <option value='<%=state.getString("geoId")%>'><%=state.getString("geoName")%></option>
<%}%>
