
<%Iterator countries = UtilMisc.toIterator(delegator.findByAndCache("Geo", UtilMisc.toMap("geoTypeId", "COUNTRY"), UtilMisc.toList("geoName")));%>
<%while(countries != null && countries.hasNext()){%><%GenericValue country = (GenericValue)countries.next();%>
    <option value='<%=country.getString("geoId")%>'><%=country.getString("geoName")%></option>
<%}%>
