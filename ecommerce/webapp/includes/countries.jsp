
<%Iterator countries = UtilMisc.toIterator(helper.findByAnd("Geo", UtilMisc.toMap("geoTypeId", "COUNTRY"), UtilMisc.toList("name")));%>
<%while(countries != null && countries.hasNext()){%><%GenericValue country = (GenericValue)countries.next();%>
    <option value='<%=country.getString("geoId")%>'><%=country.getString("name")%></option>
<%}%>
