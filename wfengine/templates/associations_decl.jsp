<%
		
		Collection ass = new ArrayList(MetaModel.getAssociations(clazz));
		int z=0;
		// Wir brauchen auch die Assoziationen der anderen Superklassen (per Standard haben wir nur die der
		// "ersten"). Manche haben aber mehr als eine (z. B. Classifier erbt von Generalizable und Namespace)
		for(Iterator it = MetaModel.getAllSuperclasses(clazz).iterator(); it.hasNext();z++) {
			MBase msup = (MBase)it.next();
			if (z>0) {
				//System.out.println("IF: Adding associations from "+msup);
				ass.addAll(MetaModel.getAssociations(msup));
			}
		}
				 
		HashMap doubles = new HashMap();
		for(Iterator ass_it = ass.iterator();ass_it.hasNext();) {
			
			boolean setter=true;
			MAssociationEnd here = ((MAssociationEnd)ass_it.next());

			MAssociationEnd opp = here.getOppositeEnd();
			String oppName = MetaModel.fu(MetaModel.getPlural(MetaModel.getName(opp)));
			if (oppName.length()==0) {
				opp = here;
				here = here.getOppositeEnd();
			}
			String oppType = MetaModel.getTypeName(opp);
			String hereType = MetaModel.getTypeName(here);
			String hereName = MetaModel.fu(MetaModel.getPlural(MetaModel.getName(here)));
			String dummy1, dummy2;
			int low = MetaModel.getMultLower(opp.getMultiplicity());
			int high= MetaModel.getMultUpper(opp.getMultiplicity());
			int hereLow = MetaModel.getMultLower(here.getMultiplicity());
			int hereHigh= MetaModel.getMultUpper(here.getMultiplicity());

			if (oppName.length()==0) {
				// Hier können wir nichts mehr tun
				EventService.fireWarning("Association with empty names found in element "+classname);
				continue;
			}
			if (oppName.charAt(0)=='/') { // Abgeleitetes Attribut?
				oppName = MetaModel.fu(oppName.substring(1));
				setter = false;
			}
			if (hereName.length()>0 && hereName.charAt(0)=='/') { // Abgeleitetes Attribut?
				hereName = MetaModel.fu(hereName.substring(1));
			}
			if (!doubles.containsKey(oppName)) {
				doubles.put(oppName, oppName);
			} else {
				//System.out.println("Duplicate association "+oppName+" in class "+classname);
				continue;
			}
			String oppNameFD = "m"+oppName;
			

			if (opp.isNavigable())  {%>
<%if (high==1) { // zu n Assoziation %>			
	// Assoziation <%=oppType%>:<%=oppName%> - <%=hereType%>:<%=hereName%> 
	public <%=oppType%> get<%=oppName%>();
	public void set<%=oppName%> (<%=oppType%> p<%=oppName%>);
	
<%} else { // zu n Assoziation %>
	// <%=oppType%>:<%=oppName%> - <%=hereType%>:<%=hereName%> 
	public <%=COLLECTION_TYPE%> get<%=oppName%>();
	public void set<%=oppName%> (<%=COLLECTION_TYPE%> p<%=oppName%>);
	public void removeAll<%=oppName%>();
	public void remove<%=oppName%>(<%=oppType%> p<%=oppName%>);
	public void add<%=oppName%> (<%=oppType%> p<%=oppName%>);	
<%} // else..if%>		
	public void link<%=oppName%>(<%=oppType%> p<%=oppName%>);
	public void unlink<%=oppName%>(<%=oppType%> p<%=oppName%>);
<%
} else {//((if navi...%>
	// NOTE: Association <%=oppType%>:<%=oppName%> - <%=hereType%>:<%=hereName%> is not navigable
<%}}// for
%>