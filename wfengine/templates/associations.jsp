<%		Collection ass = getAssociations(clazz);
		 
		for(Iterator ass_it = ass.iterator();ass_it.hasNext();) {
			MLinkEnd le =null;
			boolean setter=true;
			MAssociationEnd here = ((MAssociationEnd)ass_it.next());

			MAssociationEnd opp = here.getOppositeEnd();
			String oppName = MetaModel.getNameFU(opp);
			

			/* owl, 2001-01-10
			 * Merkwürdiges System: Novosoft markiert das richtige Ende scheinbar dadurch, das er das andere auf
			 * eine Leerstring setzt???? Jedenfalls funktioniert es so
			 */
			if (oppName.length()==0) {
				MAssociationEnd dummy = here;
				here = opp;
				opp = dummy;
			}
			String oppType = MetaModel.getName(opp.getType());
			String hereName = MetaModel.getNameFU(here);
			int low = MetaModel.getMultLower(opp.getMultiplicity());
			int high= MetaModel.getMultUpper(opp.getMultiplicity());
			int hereHigh= MetaModel.getMultUpper(here.getMultiplicity());

			if (oppName.length()==0) {
				// Hier können wir nichts mehr tun
				EventService.fireWarning("Association with empty names found!");
				continue;
			}
			if (oppName.charAt(0)=='/') { // Abgeleitetes Attribut?
				oppName = MetaModel.fu(oppName.substring(1));
				setter = false;
			}
			String oppNameFD = MetaModel.fd(oppName);


			if (opp.isNavigable())  {%>
	// Link attribute of association '<%=oppName%> '
			<%if (high==1) {%>
	private <%=oppType%> <%=oppNameFD%>;

	/**
	 * Getter of association '<%=oppName%>'
	 * @return Current value of association '<%=oppName%>'.<%if (low==1) {%>
	 * @throws RuntimeException, if value is null<%}%>
	 */
	public <%=oppType%> get<%=oppName%>() {
		<%if (low==1) {%>if (<%=oppNameFD%> == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: <%=classname%>#<%=oppName%> is null!");
		}<%}%>
		return <%=oppNameFD%>;
	}

	/**
	 * Setter of association '<%=oppName%>'.
	 * @param p<%=oppName%> New value for association '<%=oppName%>'
	 */
	public void set<%=oppName%> (<%=oppType%> p<%=oppName%>) {<%if (here.isNavigable()) {%>
		if (p<%=oppName%> == null && <%=oppNameFD%> != null) {
			<%=oppNameFD%>.unlink<%=hereName%>( this );
		}<%}%>
		<%=oppNameFD%> = p<%=oppName%>;		 
		<%if (here.isNavigable())  {%><%=oppNameFD%>.link<%=hereName%>(this);<%}%>
	}
	<%if (low==1) {%>	
	/**
	 * Checks, if aggregate '<%=oppName%>' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean is<%=oppName%>Null() {		
		return <%=oppNameFD%> == null;
	}
	<%}%>
<%} else { // zu n Assoziation %>
	private <%=COLLECTION_TYPE%> <%=oppNameFD%>;

	/**
	 * Getter of association '<%=oppName%>'
	 * @return Currents contents of association '<%=oppName%>'
	 */
	public <%=COLLECTION_TYPE%> get<%=oppName%>() {
		return <%=oppNameFD%> != null ? <%=oppNameFD%> : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  '<%=oppName%>'. All existing elements are dropped. An null argument
	 * creates the same result as removeAll<%=oppName%>
	 * @param p<%=oppName%> List containing the new elements for association  '<%=oppName%>'. 
	 */
	public void set<%=oppName%> (<%=COLLECTION_TYPE%> p<%=oppName%>) {
		removeAll<%=oppName%>();	
		if (p<%=oppName%> != null ) {
			addAllTo<%=oppName%>( p<%=oppName%> );
		}
	}

	/**
	 * Removes all elements from assoziation '<%=oppName%>'
	 */
	public void removeAll<%=oppName%>() {
		if (<%=oppNameFD%> == null) return; // nothing to do
		
		for(Iterator it = <%=oppNameFD%>.iterator(); it.hasNext();) {
			<%=oppType%> lElement = (<%=oppType%>) it.next();
			<%if (here.isNavigable())  {%>lElement.unlink<%=hereName%>( this );<%}%>
			remove<%=oppName%>( lElement );				
		}		
	}

	/**
	 * Removes p<%=oppName%> from assoziation '<%=oppName%>'
	 * @param p<%=oppName%> element to remove
	 */
	public void remove<%=oppName%>(<%=oppType%> p<%=oppName%>) {
		if (<%=oppNameFD%> != null) {
			<%=oppNameFD%>.remove( p<%=oppName%> );<%if (here.isNavigable())  {%>
			p<%=oppName%>.unlink<%=hereName%>( this );<%}%> // notify other end
			notifyRemove<%=oppName%>( p<%=oppName%> ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in p<%=oppName%>List to association '<%=oppName%>'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>p<%=oppName%>List.size()</code>)
	 */
	public int addAllTo<%=oppName%> (<%=COLLECTION_TYPE%> p<%=oppName%>List) {
		if (p<%=oppName%>List == null) {
			throw new RuntimeException("Attempted to add null container to <%=element.Name%>#<%=oppName%>!");
		}
		int lInserted=0;
		for(Iterator it = p<%=oppName%>List.iterator(); it.hasNext(); ) {
			try {
				<%=oppType%> l<%=oppName%> = (<%=oppType%>)it.next();				
				add<%=oppName%>( l<%=oppName%> );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds p<%=oppName%> to association '<%=oppName%>'
	 * @param p<%=oppName%> Element to add
	 */
	public void add<%=oppName%> (<%=oppType%> p<%=oppName%>) {
		if (p<%=oppName%> == null) {
			throw new RuntimeException("Attempted to add null object to <%=element.Name%>#<%=oppName%>!");
		}
		
		if (<%=oppNameFD%> == null) {
			<%=oppNameFD%> = new ArrayList();
		}
		<%=oppNameFD%>.add(p<%=oppName%>);
		<%if (here.isNavigable())  {%>
		p<%=oppName%>.link<%=hereName%>(this); // notify other end
		<%}%>
		notifyRemove<%=oppName%>( p<%=oppName%> ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association '<%=oppName%>'
	 */
	private void notifyAdd<%=oppName%>(<%=oppType%> p<%=oppName%>) {
		//System.out.println("Add " + p<%=oppName%> + " to <%=classname%>#<%=oppName%>");
	}		
	
	/**
	 * Hook for 'remove' on association '<%=oppName%>'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemove<%=oppName%>(<%=oppType%> p<%=oppName%>) {
		//System.out.println("Remove " + p<%=oppName%> + " from <%=classname%>#<%=oppName%>");		
	}
	<%}%>	
	
	/**
	 * Internal use only
	 */
	public void link<%=oppName%>(<%=oppType%> p<%=oppName%>) {		
		<%if (high!=1) {%>if (<%=oppNameFD%> == null) {
			<%=oppNameFD%> = new ArrayList();
		}
		<%=oppNameFD%>.add(p<%=oppName%>);<%} else { if (here.isNavigable()) {%>
		if (<%=oppNameFD%> != null) {
			<%=oppNameFD%>.unlink<%=hereName%>(this); // Alte Beziehung löschen
		}<%}%>
		<%=oppNameFD%> = p<%=oppName%>;<%}%>
		<%if (high!=1) {%>notifyAdd<%=oppName%>( p<%=oppName%> ); // notify ourselves<%}%>
	}
	
	/**
	 * Internal use only
	 */
	public void unlink<%=oppName%>(<%=oppType%> p<%=oppName%>) {
		<%if (high!=1) {%>if (<%=oppNameFD%> == null) return;<%=oppNameFD%>.remove(p<%=oppName%>);<%} else {%><%=oppNameFD%> = null;<%}%>
		<%if (high!=1) {%>notifyRemove<%=oppName%>( p<%=oppName%> ); // notify ourselves<%}%>
	}	
<%}}%>