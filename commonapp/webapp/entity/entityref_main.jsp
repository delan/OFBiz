<!--
 *  Copyright (c) 2001 The Open For Business Project and respected authors.
 
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 *
 * @author Andy Zeneski (jaz@zsolv.com)
 * @author David E. Jones (jonesde@ofbiz.org)
 * @version 1.0
-->

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*, org.ofbiz.core.entity.model.*, org.ofbiz.core.util.*" %>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />

<% 
  initReservedWords();
  String search = null;
  //GenericDelegator delegator = GenericHelperFactory.getDefaultHelper();
  ModelReader reader = delegator.getModelReader();
  Map packages = new HashMap();
  TreeSet packageNames = new TreeSet();

  //put the entityNames TreeSets in a HashMap by packageName
  Collection ec = reader.getEntityNames();
  TreeSet entityNames = new TreeSet(ec);
  Iterator ecIter = ec.iterator();
  while(ecIter.hasNext())
  {
    String eName = (String)ecIter.next();
    ModelEntity ent = reader.getModelEntity(eName);
    TreeSet entities = (TreeSet)packages.get(ent.packageName);
    if(entities == null)
    {
      entities = new TreeSet();
      packages.put(ent.packageName, entities);
      packageNames.add(ent.packageName);
    }
    entities.add(eName);
  }
  int numberOfEntities = ec.size();
  int numberShowed = 0;
  search = (String) request.getParameter("search");
  //as we are iterating through, check a few things and put any warnings here inside <li></li> tags
  String warningString = "";
%>

<html>
<head>
<title>Entity Reference</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<style>
  .packagetext {font-family: Helvetica,sans-serif; font-size: 18pt; font-weight: bold; text-decoration: none; color: black;}
  .toptext {font-family: Helvetica,sans-serif; font-size: 16pt; font-weight: bold; text-decoration: none; color: black;}
  .titletext {font-family: Helvetica,sans-serif; font-size: 12pt; font-weight: bold; text-decoration: none; color: blue;}
  .headertext {font-family: Helvetica,sans-serif; font-size: 8pt; font-weight: bold; text-decoration: none; background-color: blue; color: white;}
  .enametext {font-family: Helvetica,sans-serif; font-size: 8pt; font-weight: bold; text-decoration: none; color: black;}
  .entitytext {font-family: Helvetica,sans-serif; font-size: 8pt; text-decoration: none; color: black;}
  .relationtext {font-family: Helvetica,sans-serif; font-size: 8pt; text-decoration: none; color: black;}
  A.rlinktext {font-family: Helvetica,sans-serif; font-size: 8pt; font-weight: bold; text-decoration: none; color: blue;}
  A.rlinktext:hover {color:red;}
</style>
</head>

<body bgcolor="#FFFFFF">
<div align="center">

  <DIV class='toptext'>Entity Reference Chart<br>
    <%= numberOfEntities %> Total Entities
    </DIV>

<A href='#WARNINGS'>View Warnings</A>
<%
  Iterator piter = packageNames.iterator();
  while(piter.hasNext())
  {
    String pName = (String)piter.next();
    TreeSet entities = (TreeSet)packages.get(pName);
%><A name='<%=pName%>'></A><HR><DIV class='packagetext'><%=pName%></DIV><HR><%
    Iterator i = entities.iterator();
    while ( i.hasNext() ) 
    {
      String entityName = (String)i.next();
      if ( search == null || entityName.toLowerCase().indexOf(search.toLowerCase()) != -1 )
      {
        ModelEntity entity = reader.getModelEntity(entityName);
        if(entity.tableName.length() > 30)
          warningString = warningString + "<li><div style=\"color: red;\">[TableNameGT30]</div> Table name <b>" + entity.tableName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is longer than 30 characters.</li>";
        if(reservedWords.contains(entity.tableName.toUpperCase()))
          warningString = warningString + "<li><div style=\"color: red;\">[TableNameRW]</div> Table name <b>" + entity.tableName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is a reserved word.</li>";
%>	
  <a name="<%= entityName %>"></a>
  <table width="95%" border="1" cellpadding='2' cellspacing='0'>
    <tr bgcolor="#CCCCCC"> 
      <td colspan="5"> 
        <div align="center" class='titletext'>ENTITY: <%= entityName %> | TABLE: <%= entity.tableName %></div>
      </td>
    </tr>
    <tr class='headertext'>
      <td width="30%" align=center>Java Name</td>
      <td width="30%" align=center>DB Name</td>
      <td width="10%" align=center>Field-Type</td>
      <td width="15%" align=center>Java-Type</td>
      <td width="15%" align=center nowrap>SQL-Type</td>
    </tr>
	
<%
  TreeSet ufields = new TreeSet();
  for(int y = 0; y < entity.fields.size(); y++)
  {
    ModelField field = (ModelField) entity.fields.elementAt(y);	
    ModelFieldType type = delegator.getEntityFieldType(entity, field.type);
    String javaName = new String();
    javaName = field.isPk ? "<div style=\"color: red;\">" + field.name + "</div>" : field.name;
    if(ufields.contains(field.name))
      warningString = warningString + "<li><div style=\"color: red;\">[FieldNotUnique]</div> Field <b>" + field.name + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is not unique for that entity.</li>";
    else
      ufields.add(field.name);
    if(field.colName.length() > 30)
      warningString = warningString + "<li><div style=\"color: red;\">[FieldNameGT30]</div> Column name <b>" + field.colName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is longer than 30 characters.</li>";
    if(reservedWords.contains(field.colName.toUpperCase()))
      warningString = warningString + "<li><div style=\"color: red;\">[FieldNameRW]</div> Column name <b>" + field.colName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is a reserved word.</li>";
%>	
    <tr bgcolor="#EFFFFF">
      <td><div align="left" class='enametext'><%= javaName %></div></td>
      <td><div align="left" class='entitytext'><%= field.colName %></div></td>
      <td><div align="left" class='entitytext'><%= field.type %></div></td>
    <%if(type != null){%>
      <td><div align="left" class='entitytext'><%= type.javaType %></div></td>
      <td><div align="left" class='entitytext'><%= type.sqlType %></div></td>
    <%}else{%>
      <td><div align="left" class='entitytext'>NOT FOUND</div></td>
      <td><div align="left" class='entitytext'>NOT FOUND</div></td>
      <%warningString = warningString + "<li><div style=\"color: red;\">[FieldTypeNotFound]</div> Field type <b>" + field.type + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> not found in field type definitions.</li>";%>
    <%}%>
    </tr>
<%	
			}
			if ( entity.relations != null && entity.relations.size() > 0 ) {
%>
	<tr bgcolor="#FFCCCC">
	  <td colspan="5"><hr></td>
	</tr>
    <tr class='headertext'> 
      <td align="center">Relation</td>
      <td align="center">Table</td>
      <td align="center" colspan='3'>Type</td>	  
      
    </tr>
<%
  TreeSet relations = new TreeSet();
  for ( int r = 0; r < entity.relations.size(); r++ ) {
    ModelRelation relation = (ModelRelation) entity.relations.elementAt(r);
    
    if(!entityNames.contains(relation.relEntityName))
      warningString = warningString + "<li><div style=\"color: red;\">[RelatedEntityNotFound]</div> Related entity <b>" + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> not found.</li>";
    if(relations.contains(relation.title + relation.relEntityName))
      warningString = warningString + "<li><div style=\"color: red;\">[RelationNameNotUnique]</div> Relation <b>" + relation.title + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is not unique for that entity.</li>";
    else
      relations.add(relation.title + relation.relEntityName);

    ModelEntity relatedEntity = reader.getModelEntity(relation.relEntityName);
    if(relatedEntity != null)
    {
      //if relation is of type one, make sure keyMaps match the PK of the relatedEntity
      if(relation.type.equalsIgnoreCase("one"))
      {
        if(relatedEntity.pks.size() != relation.keyMaps.size())
          warningString = warningString + "<li><div style=\"color: red;\">[RelatedOneKeyMapsWrongSize]</div> The number of primary keys (" + relatedEntity.pks.size() + ") of related entity <b>" + relation.relEntityName + "</b> does not match the number of keymaps (" + relation.keyMaps.size() + ") for relation of type one \"" +  relation.title + relation.relEntityName + "\" of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A>.</li>";
        for(int repks=0; repks<relatedEntity.pks.size(); repks++)
        {
          ModelField pk = (ModelField)relatedEntity.pks.get(repks);
          if(relation.findKeyMapByRelated(pk.name) == null)
            warningString = warningString + "<li><div style=\"color: red;\">[RelationOneRelatedPrimaryKeyMissing]</div> The primary key \"<b>" + pk.name + "</b>\" of related entity <b>" + relation.relEntityName + "</b> is missing in the keymaps for relation of type one <b>" +  relation.title + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A>.</li>";
        }
      }
    }

    //make sure all keyMap 'fieldName's match fields of this entity
    //make sure all keyMap 'relFieldName's match fields of the relatedEntity
    for(int rkm=0; rkm<relation.keyMaps.size(); rkm++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(rkm);
      if(relatedEntity != null)
      {
        if(relatedEntity.getField(keyMap.relFieldName) == null)
          warningString = warningString + "<li><div style=\"color: red;\">[RelationRelatedFieldNotFound]</div> The field \"<b>" + keyMap.relFieldName + "</b>\" of related entity <b>" + relation.relEntityName + "</b> was specified in the keymaps but is not found for relation <b>" +  relation.title + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A>.</li>";
      }
      if(entity.getField(keyMap.fieldName) == null)
        warningString = warningString + "<li><div style=\"color: red;\">[RelationFieldNotFound]</div> The field <b>" + keyMap.fieldName + "</b> was specified in the keymaps but is not found for relation <b>" +  relation.title + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A>.</li>";
    }

%>
    <tr bgcolor="#FEEEEE"> 
      <td> 
        <div align="left" class='relationtext'>
          <b><%=relation.title%></b><A href='#<%=relation.relEntityName%>' class='rlinktext'><%=relation.relEntityName%></A>
        </div>
      </td>
      <td><div align="left" class='relationtext'><%= relation.relTableName %></div></td>
      <td width="25%" colspan='3'><div align="left" class='relationtext'>
        <%=relation.type%>:<%if(relation.type.length()==3){%>&nbsp;<%}%>
        <%for(int km=0; km<relation.keyMaps.size(); km++){ ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(km);%>
          <br>&nbsp;&nbsp;<%=km+1%>)&nbsp;
          <%if(keyMap.fieldName.equals(keyMap.relFieldName)){%><%=keyMap.fieldName%>
          <%}else{%><%=keyMap.fieldName%> : <%=keyMap.relFieldName%><%}%>
        <%}%>
      </div></td>
    </tr>				
<%
				}
			}
%>
    <tr bgcolor="#CCCCCC">
	  <td colspan="5">&nbsp;</td>
	</tr>
  </table>
  <br>
<%
      numberShowed++;
      }
    }
  }
%>  
  <br><br>
  <p align="center">Displayed: <%= numberShowed %></p>
</div>

<A name='WARNINGS'>WARNINGS:</A>
<OL>
<%=warningString%>
</OL>

</body>
</html>

<%!
public TreeSet reservedWords = new TreeSet();
public static final String[] rwArray = 
  { "ABORT", "ABS", "ABSOLUTE", "ACCEPT", "ACCES", "ACTION", "ACTIVATE", "ADD", "ADDFORM", "ADMIN",
    "AFTER", "AGGREGATE", "ALIAS", "ALL", "ALLOCATE", "ALTER", "AND", "ANDFILENAME", "ANY", "ANYFINISH", "APPEND", 
    "ARCHIVE", "ARE", "ARRAY", "AS", "ASC", "ASCENDING", "ASCII", "ASSERT", "ASSERTION", "ASSIGN", 
    "AT", "ATTRIBUTE", "ATTRIBUTES", "AUDIT", "AUTHORIZATION", "AUTONEXT", 
    "AVERAGE", "AVG", "AVGU", 
    "BACKOUT", "BEFORE", "BEGIN", "BEGINLOAD", "BEGINMODIFY", "BEGINNING", 
    "BEGWORK", "BETWEEN", "BETWEENBY", "BINARY", "BIT", "BLOB", "BOOLEAN", "BORDER", "BOTH", "BOTTOM", "BREAK", "BREADTH", 
    "BREAKDISPLAY", "BROWSE", "BUFERED", "BUFFER", "BUFFERED", "BULK", "BY", 
    "BYTE", 
    "CALL", "CANCEL", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CHANGE", "CHAR", "CHARACTER", 
    "CHAR_CONVERT", "CHECK", "CHECKPOINT", "CHR2FL", "CHR2FLO", "CHR2FLOA", 
    "CHR2FLOAT", "CHR2INT", "CLASS", "CLEAR", "CLEARROW", "CLIPPED", "CLOB", "CLOSE", "CLUSTER", 
    "CLUSTERED", "CLUSTERING", "COBOL", "COLD", "COLLATE", "COLLATION", "COLUMN", "COLUMNS", "COMMAND", 
    "COMMENT", "COMMIT", "COMMITTED", "COMPLETION", "COMPRESS", "COMPUTE", "CONCAT", "COND", "CONDITION", 
    "CONFIG", "CONFIRM", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONSTRUCT", "CONSTRUCTOR", "CONTAIN", 
    "CONTAINS", "CONTINUE", "CONTROLROW", "CONVERT", "COPY", "CORRESPONDING", "COUNT", "COUNTU", 
    "COUNTUCREATE", "CRASH", "CREATE", "CROSS", "CUBE", "CURRENT", "CURRENT_DATE", "CURRENT_PATH", "CURRENT_ROLE", 
    "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "CYCLE", 
    "DATA", "DATALINK", "DATABASE", "DATAPAGES", "DATABASE", "DATA_PGS", "DATE", "DAY", 
    "DAYNUM", "DBA", "DBCC", "DBE", "DBEFILE", "DBEFILEO", "DBEFILESET", 
    "DBSPACE", "DBYTE", "DEALLOCATE", "DEC", "DECENDING", "DECIMAL", "DECLARE", "DEFAULT", 
    "DEFAULTS", "DEFER", "DEFERRABLE", "DEFINE", "DEFINITION", "DELETE", "DELETEROW", "DEPTH", "DEREF", "DESC", 
    "DESCENDING", "DESCENDNG", "DESCRIBE", "DESCRIPTOR", "DESTPOS", "DESTROY", 
    "DEVICE", "DEVSPACE", "DIAGNOSTICS", "DICTIONARY", "DIRECT", "DIRTY", "DISCONNECT", "DISK", "DISPLACE", 
    "DISPLAY", "DISTINCT", "DISTRIBUTION", "DIV", "DO", "DOES", "DOMAIN", 
    "DOUBLE", "DOWN", "DROP", "DUAL", "DUMMY", "DUMP", "DUPLICATES", 
    "EACH", "EBCDIC", "EDITADD", "EDITUPDATE", "ED_STRING", "ELSE", "ELSEIF", 
    "END", "ENDDATA", "ENDDISPLAY", "ENDFORMS", "ENDIF", "ENDING", "ENDLOAD", 
    "ENDLOOP", "ENDMODIFY", "ENDPOS", "ENDRETRIEVE", "ENDSELECT", "ENDWHILE", 
    "END_ERROR", "END_EXEC", "END_FETCH", "END_FOR", "END_GET", "END_MODIFY", "END_PLACE", 
    "END_SEGMENT_S", "END_SEGMENT_STRING", "END_STORE", "END_STREAM", "EQ", "EQUALS", 
    "ERASE", "ERROR", "ERRLVL", "ERROREXIT", "ESCAPE", "EVALUATE", "EVALUATING", "EVERY", 
    "EXCEPT", "EXCEPTION", "EXCLUSIVE", "EXEC", "EXECUTE", "EXISTS", "EXIT", "EXPAND", "EXPANDING", "EXPLICIT", 
    "EXTENT", "EXTERNAL", 
    "FALSE", "FETCH", "FIELD", "FILE", "FILENAME", "FILLFACTOR", "FINALISE", 
    "FINALIZE", "FINDSTR", "FINISH", "FIRST", "FIRSTPOS", "FIXED", "FL", 
    "FLOAT", "FLUSH", "FOR", "FOREACH", "FOREIGN", "FORMAT", "FORMDATA", "FORMINIT", 
    "FORMS", "FORTRAN", "FOUND", "FRANT", "FRAPHIC", "FREE", "FROM", "FRS", 
    "FUNCTION", 
    "GE", "GENERAL", "GET", "GETFORM", "GETOPER", "GETROW", "GLOBAL", "GLOBALS", "GO", 
    "GOTO", "GRANT", "GRAPHIC", "GROUP", "GROUPING", "GT", 
    "HANDLER", "HASH", "HAVING", "HEADER", "HELP", "HELPFILE", "HELP_FRS", "HOLD", "HOLDLOCK", "HOUR", 
    "IDENTIFIED", "IDENTIFIELD", "IDENTITY", "IF", "IFDEF", "IGNORE", "IMAGE", "IMMEDIATE", 
    "IMMIDIATE", "IMPLICIT", "IN", "INCLUDE", "INCREMENT", "INDEX", "INDEXED", 
    "INDEXNAME", "INDEXPAGES", "INDICATOR", "INFIELD", "INFO", "INGRES", "INIT", 
    "INITIAL", "INITIALISE", "INITIALIZE", "INITIALLY", "INITTABLE", "INNER", "INOUT", "INPUT", "INQUIRE_EQUEL", 
    "INQUIRE_FRS", "INQUIRE_INGRES", "INQUIR_FRS", "INSERT", "INSERTROW", 
    "INSTRUCTIONS", "INT", "INT2CHR", "INTEGER", "INTEGRITY", "INTERESECT", 
    "INTERRUPT", "INTERSECT", "INTERVAL", "INTO", "INTSCHR", "INVOKE", "IS", "ISAM", 
    "ISOLATION", "ITERATE", 
    "JOIN", "JOURNALING", 
    "KEY", "KILL", 
    "LABEL", "LANGUAGE", "LARGE", "LAST", "LASTPOS", "LATERAL", "LE", "LEADING", "LEAVE", "LEFT", "LENGTH", "LENSTR", 
    "LESS", "LET", "LEVEL", "LIKE", "LIKEPROCEDURETP", "LIMIT", "LINE", "LINENO", "LINES", 
    "LINK", "LIST", "LOAD", "LOADTABLE", "LOADTABLERESUME", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOCATION", "LOCATOR", 
    "LOCK", "LOCKING", "LOG", "LONG", "LOOP", "LOWER", "LPAD", "LT", 
    "MAIN", "MANUITEM", "MARGIN", "MATCH", "MATCHES", "MATCHING", "MAX", "MAXEXTENTS", "MAXPUBLICUNION", 
    "MAXRECLEN", "MDY", "MEETS", "MENU", "MENUITEM", "MENUITEMSCREEN", "MESSAGE", 
    "MESSAGERELOCATE", "MESSAGESCROLL", "MFETCH", "MIN", "MINRECLEN", 
    "MINRETURNUNTIL", "MINUS", "MINUTE", "MIRROREXIT", "MISSING", "MIXED", "MOD", "MODE", 
    "MODIFIES", "MODIFY", "MODIFYREVOKEUPDATE", "MODULE", "MONEY", "MONITOR", "MONTH", 
    "MOVE", "MULTI", 
    "NAME", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NE", "NEED", 
    "NEW", "NEWLOG", "NEXT", "NEXTSCROLLDOWN", "NO", 
    "NOAUDIT", "NOCOMPRESS", "NOCR", "NOJOURNALING", "NOLIST", "NOLOG", 
    "NONCLUSTERED", "NONE", "NORMAL", "NORMALIZE", "NOSYSSORT", "NOT", "NOTFFOUND", "NOTFOUND", 
    "NOTRANS", "NOTRIM", "NOTRIMSCROLLUP", "NOTROLLBACKUSER", "NOWAIT", "NULL", 
    "NULLIFY", "NULLSAVEUSING", "NULLVAL", "NUMBER", "NUMERIC", "NXFIELD", 
    "OBJECT", "OF", "OFF", "OFFLINE", "OFSAVEPOINTVALUES", "OFFSETS", "OLD", "ON", "ONCE", 
    "ONLINE", "ONLY", "ONSELECTWHERE", "ONTO", "OPEN", "OPENSETWHILE", "OPENSLEEP", "OPERATION", 
    "OPTIMIZE", "OPTION", "OPTIONS", "OR", "ORDER", "ORDERSQLWORK", "ORDINALITY", "ORSOMEWITH", 
    "ORSORT", "OTHERWISE", "OUT", "OUTER", "OUTPUT", "OUTPUT PAGE", "OUTSTOP", 
    "OVER", "OWNER", "OWNERSHIP", 
    "PAD", "PAGE", "PAGENO", "PAGES", "PARAM", "PARAMETER", "PARAMETERS", "PARTIAL", "PARTITION", "PASCAL", "PASSWORD", 
    "PATH", "PATHNAME", "PATTERN", "PAUSE", "PCTFREE", "PERCENT", "PERIOD", "PERM", "PERMANENT", 
    "PERMIT", "PERMITSUM", "PIPE", "PLACE", "PLAN", "PLI", "POS", "POSTFIX", "POWER", 
    "PRECEDES", "PRECISION", "PREFIX", "PREORDER", "PREPARE", "PREPARETABLE", "PRESERVE", "PREV", "PREVIOUS", 
    "PREVISION", "PRIMARY", "PRINT", "PRINTER", "PRINTSCREEN", "PRINTSCREENSCROLL", 
    "PRINTSUBMENU", "PRINTSUMU", "PRIOR", "PRIV", "PRIVATE", "PRIVILAGES", 
    "PRIVILAGESTHEN", "PRIVILEGES", "PROC", "PROCEDURE", "PROCESSEXIT", 
    "PROGRAM", "PROGUSAGE", "PROMPT", "PROMPTSCROLLDOWN", "PROMPTTABLEDATA", 
    "PROTECT", "PSECT", "PUBLIC", "PUBLICREAD", "PUT", "PUTFORM", 
    "PUTFORMSCROLLUP", "PUTFORMUNLOADTABLE", "PUTOPER", "PUTOPERSLEEP", "PUTROW", 
    "PUTROWSUBMENU", "PUTROWUP", 
    "QUERY", "QUICK", "QUIT", 
    "RAISERROR", "RANGE", "RANGETO", "RAW", "RDB$DB_KEY", "RDB$LENGTH", 
    "RDB$MISSING", "RDB$VALUE", "RDB4DB_KEY", "RDB4LENGTH", "RDB4MISSING", 
    "RDB4VALUE", "READ", "READS", "READONLY", "READPASS", "READTEXT", "READWRITE", 
    "READY", "READ_ONLY", "READ_WRITE", "REAL", "RECONFIGURE", "RECONNECT", 
    "RECORD", "RECOVER", "RECURSIVE", "REDISPLAY", "REDISPLAYTABLEDATA", "REDISPLAYVALIDATE", 
    "REDO", "REDUCED", "REF", "REFERENCES", "REFERENCING", "REGISTER", "REGISTERUNLOADDATA", "REGISTERVALIDROW", "REJECT", 
    "RELATIVE", "RELEASE", "RELOAD", "RELOCATE", "RELOCATEUNIQUE", "REMOVE", 
    "REMOVEUPRELOCATEV", "REMOVEVALIDATE", "REMOVEWHENEVER", "RENAME", "REPEAT", 
    "REPEATABLE", "REPEATED", "REPEATVALIDROW", "REPLACE", "REPLACEUNTIL", 
    "REPLSTR", "REPORT", "REQUEST_HANDLE", "RESERVED_PGS", "RESERVING", "RESET", "RESIGNAL", 
    "RESOURCE", "REST", "RESTART", "RESTORE", "RESTRICT", "RESULT", "RESUME", "RETRIEVE", 
    "RETRIEVEUPDATE", "RETURN", "RETURNS", "RETURNING", "REVOKE", "RIGHT", "ROLE", "ROLLBACK", 
    "ROLLFORWARD", "ROLLBACK", "ROLLUP", "ROUND", "ROUTINE", "ROW", "ROWCNT", "ROWCOUNT", "ROWID", 
    "ROWNUM", "ROWS", "RPAD", "RULE", "RUN", "RUNTIME", 
    "SAMPLSTDEV", "SAVE", "SAVEPOINT", "SAVEPOINTWHERE", "SAVEVIEW", "SCHEMA", 
    "SCOPE", "SCREEN", "SCROLL", "SCROLLDOWN", "SCROLLUP", "SEARCH", "SECOND", "SECTION", "SEGMENT", 
    "SEL", "SELE", "SELEC", "SELECT", "SELUPD", "SEQUENCE", "SERIAL", "SESSION", "SESSION_USER", "SET", "SETS", 
    "SETWITH", "SET_EQUEL", "SET_FRS", "SET_INGRES", "SETUSER", "SHARE", 
    "SHARED", "SHORT", "SHOW", "SHUTDOWN", "SIGNAL", "SIZE", "SKIP", "SLEEP", "SMALLFLOAT", 
    "SMALLINT", "SOME", "SORT", "SORTERD", "SOUNDS", "SOURCEPOS", "SPACE", 
    "SPACES", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLCODE", "SQLDA", "SQLERROR", "SQLEXCEPTION", "SQLEXEPTION", "SQLEXPLAIN", 
    "SQLNOTFOUND", "SQLSTATE", "SQLWARNING", "SQRT", "STABILITY", "START", "STARTING", "STARTPOS", 
    "START_SEGMENT", "START_SEGMENTED_?", "START_STREAM", "START_TRANSACTION", 
    "STATE", "STATIC", "STATISTICS", "STDEV", "STEP", "STOP", "STORE", "STRING", "STRUCTURE", "SUBMENU", 
    "SUBSTR", "SUCCEEDS", "SUCCESFULL", "SUCCESSFULL", "SUM", "SUMU", "SUPERDBA", 
    "SYB_TERMINATE", "SYNONYM", "SYSDATE", "SYSSORT", "SYSTEM_USER", 
    "TABLE", "TABLEDATA", "TEMP", "TEMPORARY", "TERMINATE", "TEXT", "TEXTSIZE", 
    "THAN", "THEN", "THROUGH", "THRU", "TID", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TODAY", "TOLOWER", "TOP", 
    "TOTAL", "TOUPPER", "TP", "TRAILER", "TRAILING", "TRAN", "TRANS", "TRANSACTION", 
    "TRANSACTION_HANDLE", "TRANSFER", "TRANSLATION", "TREAT", "TRIGGER", "TRING", "TRUE", "TRUNC", 
    "TRUNCATE", "TSEQUAL", "TYPE", 
    "UID", "UNBUFFERED", "UNDER", "UNDO", "UNION", "UNIQUE", "UNKNOWN", "UNLOAD", "UNLOADDATA", 
    "UNLOADTABLE", "UNLOCK", "UNTIL", "UP", "UPDATE", "UPPER", "USAGE", "USE", 
    "USED_PGS", "USER", "USING", 
    "VALIDATE", "VALIDROW", "VALUE", "VALUES", "VARC", "VARCH", "VARCHA", "VARCHAR", 
    "VARGRAPHIC", "VARIABLE", "VARYING", "VERB_TIME", "VERIFY", "VERSION", "VIEW", 
    "WAIT", "WAITFOR", "WAITING", "WARNING", "WEEKDAY", "WHEN", "WHENEVER", 
    "WHERE", "WHILE", "WINDOW", "WITH", "WITHOUT", "WORK", "WRAP", "WRITE", 
    "WRITEPASS", "WRITETEXT", 
    "YEAR", "ZONE" };


public void initReservedWords() {
  //create extensive list of reserved words
  int asize = rwArray.length;
  Debug.log("[initReservedWords] array length=" + asize);
  for(int i=0; i<asize; i++) {
    reservedWords.add(rwArray[i]);
  }
}
%>
