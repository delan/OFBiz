<%@include file="params.jsp" %>-- SQL-Skript für <%=classname%> (ORACLE)
-- Datum <%=new Date().toString()%>
--
drop table T<%=classname%>;
create table T<%=classname%> (<%
	Collection attr = getAttributes(element);
	Iterator attr_it = attr.iterator();

	while (attr_it.hasNext()) {
		MBase m = (MBase)attr_it.next();
		String attrName = getName(m);
		String attrNameFU = fu(attrName);
		if (attrName.length()==0) {
			break;
		}

		String instName = fd(attrName);
		String id = getId(m);
%> <%=attrName%> <%=getDBType(m)%> <% if (isPrimaryKey(m)) {out.print("PRIMARY KEY ");}%><%if (attr_it.hasNext()) out.print(",");}


		Collection ass = getAssociations(element);
		Iterator ass_ita = ass.iterator();
		while(ass_ita.hasNext()) {
			MAssociationEnd here = (MAssociationEnd)ass_ita.next();
			MAssociationEnd opp = here.getOppositeEnd();
			String oppName = fd(getNameFD(opp));
			String st = getStereotype(opp.getType());
			if (oppName.length()==0) {
				oppName = fd(getNameFD(here));
				st = getStereotype(here.getType());
			}
      int hereHigh = getMultUpper(here.getMultiplicity());
      int high = getMultUpper(opp.getMultiplicity());

			if ("enumeration".equalsIgnoreCase(st)) {
				out.print(", " + oppName+"Key NUMBER ");
			} else {
          // Wenn es eine n-m-Beziehung ist, müssen keine Foreign-Keys erzeugt werden - dann ist eine Zwischentabelle notwendig
          // Diese kann z.Zt. nicht generiert werden und muss von Hand erstellt werden
          if (hereHigh==-1 && high==-1) {
            continue;
          } else
          // Wenn dies die n-Seite einer 1-n-Beziehung ist, muss ein Foreign-Key generiert werden, ...
          if (hereHigh==-1 && high==1) {
            out.print(", " + oppName + " VARCHAR(50) ");
          } else
          // ... an der 1-Seite muss nichts generiert werden
          if (hereHigh==1 && high==-1) {
            continue;
          } else {
	          // Hier werden u.U. zu viele Felder erzeugt - Foreign Keys, die nicht in Beziehungen stehen - Ich weiss noch nicht
	          // wie man das los werden kann
	          out.print(", " + oppName + " VARCHAR(50) ");
          } // end if
      } // end if
   } // end while%>
   <%out.beginUserCode("create");%>
   <%out.endUserCode();%>
);
<%@ page import="java.io.BufferedWriter" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Collection" %>
<%@ page import="ru.novosoft.uml.MBase" %>
<%@ page import="com.innoq.generator.*" %>


<%! public String getDBType(MBase pAttribute)  {
		String type = getTypeName(pAttribute);

		if (type.equals("Date")) {
                        return "DATE";
		} else
		if (type.equals("int")) {
			return "INTEGER";
		} else
		if (type.equals("long")) {
			return "LONG";
		} else
		if (type.equals("boolean")) {
			return "BOOLEAN";
		} else {
		    int length = getLength(pAttribute);
			return "VARCHAR("+length+")";
		}

	}
%>

<%@include "utilities.jsp"%><%@include file="metamodel.jsp" %>

<%! 
private int getLength(MBase m) {return 50;}

public String getFileExtension() {
		return "sql";
	}
public String getFilename()  {
		return getDirectory()+File.separatorChar+"sql"+File.separatorChar+getName(element)+"."+getFileExtension();
	}

public String commentString()  {
		return "--";
	}%>