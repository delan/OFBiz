<%@page import="com.innoq.generator.MetaModel"%><%@page import="java.util.HashMap"%><%@page import="ru.novosoft.uml.foundation.core.*"%><%@ page import="ru.novosoft.uml.behavior.common_behavior.MLinkEnd" %><%! 

private HashMap imports; 

public boolean isPrimitiveType(MBase element) {
		return isPrimitiveType(getFQName(getType(element)));
    }

public boolean isPrimitiveType(String pTypename) {
		String s = pTypename;
		int p = s.lastIndexOf(".");
		if (p!=-1) {
			s=s.substring(p+1, s.length());
		}

	  	return (s.equals("int") ||
			s.equals("long") ||
			s.equals("char") ||
			s.equals("boolean") ||
			s.equals("double") ||
			s.equals("float") ||
			s.equals("byte") ||
			s.equals("short") ||
			s.equals("String") ||
			s.equals("NULL") ||  // verwendet Novosoft für "void"
			s.equals("void"));
	}

public boolean isJavaUtilType(String pTypename) {
		String s = pTypename;
		int p = s.lastIndexOf(".");
		if (p!=-1) {
			s=s.substring(p+1, s.length());
		}

	  	return (s.equals("Collection") ||
			s.equals("Vector") ||
			s.equals("Enumeration") ||
			s.equals("Properties") ||
			s.equals("List") ||
			s.equals("Set") ||
			s.equals("Date") ||
			s.equals("Map") ||
			s.equals("Hashtable") ||
			s.equals("HashMap") ||
			s.equals("Iterator"));
}

public String getReturnValue(MBase element) {
		String s = getName(element);
		if (isPrimitiveType(element)) {
			if (s.equals("int") ) {
				return "0";
			} else if (s.equals("long") ) {
				return "0L";
			} else if (s.equals("char") ) {
				return "'_'";
			} else if (s.equals("boolean") ) {
				return "true";
			} else if (s.equals("double") ) {
				return "0.0";
			} else if (s.equals("float") ) {
				return "0.0";
			} else if (s.equals("byte") ) {
				return "0xff";
			} else if (s.equals("short") ) {
				return "0";
			} else if (s.equals("String") ) {
				return "\"\"";
			} else if (s.equals("NULL") ) { // void
				return  "/* "+s+" */";
			} else if (s.equals("VOID") ) { 
				return  "/* "+s+" */";
			} else { // void
				return  "null /* "+s+" */";
			}
		} else {
			return "null /* "+s+" */";
		}
	} // end getReturnValue

 public void writeImports() throws IOException {
      imports = new HashMap();
      Collection c = getAttributes(element);
      String path = getPath();

      Iterator it = c.iterator();
      while (it.hasNext()) {
              addToImport((MBase)it.next(), path);
      }
      
      c = getInterfaces(element);
		
      it = c.iterator();
      while (it.hasNext()) {
      		  MBase iface = (MBase)it.next();
              addPathToImport(expand(getFQName(iface)), path);
      }
  
      // Methoden
      c = getOperations(element);
      it = c.iterator();
      while (it.hasNext()) {
              // Parameter der Methode
              MBase op = (MBase)it.next();
              Collection cp = getAllParameters(op);
              Iterator ip = cp.iterator();
              while(ip.hasNext()) {
                      MParameter par=(MParameter)ip.next();
                      addPathToImport(expand(getFQName(par.getType())), path);
              }
              // Exceptions der Methode
              cp = getExceptions(op);
              ip = cp.iterator();
              while(ip.hasNext()) {              		
                      addPathToImport(
					  	expand(
							getFQName((MBase)ip.next())), path);
              }
      }
      Collection ass = getAssociations(element);
      it = ass.iterator();

      while (it.hasNext()) {
              MLinkEnd le =null;
              MAssociationEnd here = ((MAssociationEnd)it.next());
              MAssociationEnd opp = here.getOppositeEnd();
              if (opp.isNavigable())  {
                      int high= getMultUpper(opp.getMultiplicity());
                      addPathToImport(expand(getFQName(opp.getType())), path);
                      if (high==-1 || high>1) {
                        // TODO: In Variablen packen!
                        addPathToImport("java.util.Collection", path);
                        addPathToImport("java.util.ArrayList", path);
                      }
              }
      }
      addPathToImport(expand(getFQName(getSuperclass(element))), path);
      // Hook aufrufen
      addToImports();
      doWrite();
}

private String expand(String pFQName) {
    if (!isPrimitiveType(pFQName) && pFQName.indexOf(".")!=-1) {
      return getPrefix()+pFQName;
    } else {
    	if (isJavaUtilType(pFQName)) {
    		return "java.util."+pFQName;
    	} else {
      		return pFQName;
      	}
    }
}

private  void doWrite() throws IOException {
    Collection values = Util.sort(imports.values());
    Iterator it = values.iterator();
    while(it.hasNext()) {
      out.println("import "+it.next()+";");
    }
}

private  void addToImport(MBase pElement, String path) {
    if (pElement==null) return;
    String p = expand(getFQTypeName(pElement));
    addPathToImport(p, path);
}

protected void addToImport(String pFQType) {
      addPathToImport(pFQType, getPath());
}

private void addPathToImport(String p, String path) {
    if (p==null) return;
    // add to import if
    // - no primitive type
    // - packages are diffrent
    // - class is not already known
	//System.out.println(">> "+p+"/"+path);
    if (p.indexOf(".")!=-1 && !path.equals(extractPath(p))) {
            if (!imports.containsKey(p)) {
                    imports.put(p, p);
            }
    }

}

private String extractPath(String fqName) {
    if (fqName==null) return null;

          int pos = fqName.lastIndexOf(".");
          if (pos!=-1) {
                  return fqName.substring(0, pos);
          }
          return fqName;
}

private boolean isPrimaryKey(MBase m) {
	if (isAttribute(m)) {
		String st = getStereotype(m);
		return "PrimaryKey".equals(st);
	}
	return false;
}%>
