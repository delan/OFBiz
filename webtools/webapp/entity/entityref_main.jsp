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

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<% 
if(security.hasPermission("ENTITY_MAINT", session)) {
  initReservedWords();
  //boolean checkWarnings = "true".equals(request.getParameter("CHECK_WARNINGS"));
  boolean checkWarnings = true;
  String search = null;
  //GenericDelegator delegator = GenericHelperFactory.getDefaultHelper();
  ModelReader reader = delegator.getModelReader();
  Map packages = new HashMap();
  TreeSet packageNames = new TreeSet();

  //put the entityNames TreeSets in a HashMap by packageName
  Collection ec = reader.getEntityNames();
  TreeSet entityNames = new TreeSet(ec);
  Iterator ecIter = ec.iterator();
  while(ecIter.hasNext()) {
    String eName = (String)ecIter.next();
    ModelEntity ent = reader.getModelEntity(eName);
    TreeSet entities = (TreeSet)packages.get(ent.getPackageName());
    if(entities == null) {
      entities = new TreeSet();
      packages.put(ent.getPackageName(), entities);
      packageNames.add(ent.getPackageName());
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
<%if(checkWarnings) {%>
  <A href='#WARNINGS'>View Warnings</A>
<%}else{%>
  <A href='<%=response.encodeURL(controlPath + "/view/entityref_main?CHECK_WARNINGS=true")%>'>View With Warnings Check</A>  
<%}%>
<%
  Iterator piter = packageNames.iterator();
  while (piter.hasNext()) {
    String pName = (String) piter.next();
    TreeSet entities = (TreeSet) packages.get(pName);
%><A name='<%=pName%>'></A><HR><DIV class='packagetext'><%=pName%></DIV><HR><%
    Iterator i = entities.iterator();
    while (i.hasNext()) {
      String entityName = (String)i.next();
      String helperName = delegator.getEntityHelperName(entityName);
      String groupName = delegator.getEntityGroupName(entityName);
      if (search == null || entityName.toLowerCase().indexOf(search.toLowerCase()) != -1) {
        ModelEntity entity = reader.getModelEntity(entityName);
        if (checkWarnings) {
          if (helperName == null) {
            warningString = warningString + "<li><div style=\"color: red;\">[HelperNotFound]</div> No Helper (DataSource) definition found for entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A>.</li>";
            //only show group name warning if helper name not found
            if (groupName == null)
              warningString = warningString + "<li><div style=\"color: red;\">[GroupNotFound]</div> No Group Name found for entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A>.</li>";
          }
          if (entity.getTableName() != null && entity.getTableName().length() > 30)
            warningString = warningString + "<li><div style=\"color: red;\">[TableNameGT30]</div> Table name <b>" + entity.getTableName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A> is longer than 30 characters.</li>";
          if (entity.getTableName() != null && reservedWords.contains(entity.getTableName().toUpperCase()))
            warningString = warningString + "<li><div style=\"color: red;\">[TableNameRW]</div> Table name <b>" + entity.getTableName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A> is a reserved word.</li>";
        }
%>	
  <a name="<%= entityName %>"></a>
  <table width="95%" border="1" cellpadding='2' cellspacing='0'>
    <tr bgcolor="#CCCCCC"> 
      <td colspan="5"> 
        <div align="center" class="titletext">ENTITY: <%=entityName%> | TABLE: <%=entity.getTableName()%></div>
        <div align="center" class="entitytext"><b><%=entity.getTitle()%></b>&nbsp;
            <a target='main' href="<%=response.encodeURL(controlPath + "/FindGeneric?entityName=" + entityName + "&find=true&VIEW_SIZE=50&VIEW_INDEX=0")%>">[view data]</a></div>
        <%if (entity.getDescription() != null && !entity.getDescription().equalsIgnoreCase("NONE") && !entity.getDescription().equalsIgnoreCase("")) {%>
        <div align="center" class="entitytext"><%=entity.getDescription()%></div>
        <%}%>
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
  for (int y = 0; y < entity.getFieldsSize(); y++) {
    ModelField field = entity.getField(y);	
    ModelFieldType type = delegator.getEntityFieldType(entity, field.getType());
    String javaName = null;
    javaName = field.getIsPk() ? "<div style=\"color: red;\">" + field.getName() + "</div>" : field.getName();

    if(checkWarnings) {
      if(ufields.contains(field.getName()))
        warningString += "<li><div style=\"color: red;\">[FieldNotUnique]</div> Field <b>" + field.getName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A> is not unique for that entity.</li>";
      else
        ufields.add(field.getName());
      if(field.getColName().length() > 30)
        warningString += "<li><div style=\"color: red;\">[FieldNameGT30]</div> Column name <b>" + field.getColName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A> is longer than 30 characters.</li>";
      if(field.getColName().length() == 0)
        warningString += "<li><div style=\"color: red;\">[FieldNameGT30]</div> Column name for field name <b>\"" + field.getName() + "\"</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A> is empty (zero length).</li>";
      if(reservedWords.contains(field.getColName().toUpperCase()))
        warningString += "<li><div style=\"color: red;\">[FieldNameRW]</div> Column name <b>" + field.getColName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A> is a reserved word.</li>";
    }
%>	
    <tr bgcolor="#EFFFFF">
      <td><div align="left" class='enametext'><%=javaName%></div></td>
      <td><div align="left" class='entitytext'><%=field.getColName()%></div></td>
      <td><div align="left" class='entitytext'><%=field.getType()%></div></td>
    <%if(type != null){%>
      <td><div align="left" class='entitytext'><%=type.getJavaType()%></div></td>
      <td><div align="left" class='entitytext'><%=type.getSqlType()%></div></td>
    <%}else{%>
      <td><div align="left" class='entitytext'>NOT FOUND</div></td>
      <td><div align="left" class='entitytext'>NOT FOUND</div></td>
      <%
        if(checkWarnings) {
            warningString += "<li><div style=\"color: red;\">[FieldTypeNotFound]</div> Field type <b>" + field.getType() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A> not found in field type definitions";
            if (helperName == null)
                warningString += " (no helper definition found)";
            warningString += ".</li>";
        }
      %>
    <%}%>
    </tr>
<%	
			}
			if (entity.getRelationsSize() > 0) {
%>
	<tr bgcolor="#FFCCCC">
	  <td colspan="5"><hr></td>
	</tr>
    <tr class='headertext'> 
      <td align="center">Relation</td>
      <td align="center" colspan='4'>Type</td>	  
      
    </tr>
<%
  TreeSet relations = new TreeSet();
  for ( int r = 0; r < entity.getRelationsSize(); r++ ) {
    ModelRelation relation = entity.getRelation(r);
    
    if (checkWarnings) {
      if (!entityNames.contains(relation.getRelEntityName())) {
        warningString = warningString + "<li><div style=\"color: red;\">[RelatedEntityNotFound]</div> Related entity <b>" + relation.getRelEntityName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A> not found.</li>";
      }
      if (relations.contains(relation.getTitle() + relation.getRelEntityName())) {
        warningString = warningString + "<li><div style=\"color: red;\">[RelationNameNotUnique]</div> Relation <b>" + relation.getTitle() + relation.getRelEntityName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A> is not unique for that entity.</li>";
      } else {
        relations.add(relation.getTitle() + relation.getRelEntityName());
      }

      ModelEntity relatedEntity = reader.getModelEntity(relation.getRelEntityName());
      if (relatedEntity != null) {
        //if relation is of type one, make sure keyMaps match the PK of the relatedEntity
        if ("one".equals(relation.getType()) || "one-nofk".equals(relation.getType())) {
          if (relatedEntity.getPksSize() != relation.getKeyMapsSize())
            warningString = warningString + "<li><div style=\"color: red;\">[RelatedOneKeyMapsWrongSize]</div> The number of primary keys (" + relatedEntity.getPksSize() + ") of related entity <b>" + relation.getRelEntityName() + "</b> does not match the number of keymaps (" + relation.getKeyMapsSize() + ") for relation of type one \"" +  relation.getTitle() + relation.getRelEntityName() + "\" of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A>.</li>";
          for (int repks = 0; repks < relatedEntity.getPksSize(); repks++) {
            ModelField pk = relatedEntity.getPk(repks);
            if(relation.findKeyMapByRelated(pk.getName()) == null) {
              warningString = warningString + "<li><div style=\"color: red;\">[RelationOneRelatedPrimaryKeyMissing]</div> The primary key \"<b>" + pk.getName() + "</b>\" of related entity <b>" + relation.getRelEntityName() + "</b> is missing in the keymaps for relation of type one <b>" +  relation.getTitle() + relation.getRelEntityName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A>.</li>";
            }
          }
        }
      }

      //make sure all keyMap 'fieldName's match fields of this entity
      //make sure all keyMap 'relFieldName's match fields of the relatedEntity
      for (int rkm=0; rkm < relation.getKeyMapsSize(); rkm++) {
        ModelKeyMap keyMap = (ModelKeyMap)relation.getKeyMap(rkm);
        
        ModelField field = entity.getField(keyMap.getFieldName());
        ModelField rfield = null;
        if(relatedEntity != null) {
          rfield = relatedEntity.getField(keyMap.getRelFieldName());
        }
        if(rfield == null)
          warningString = warningString + "<li><div style=\"color: red;\">[RelationRelatedFieldNotFound]</div> The field \"<b>" + keyMap.getRelFieldName() + "</b>\" of related entity <b>" + relation.getRelEntityName() + "</b> was specified in the keymaps but is not found for relation <b>" +  relation.getTitle() + relation.getRelEntityName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A>.</li>";
        if(field == null)
          warningString = warningString + "<li><div style=\"color: red;\">[RelationFieldNotFound]</div> The field <b>" + keyMap.getFieldName() + "</b> was specified in the keymaps but is not found for relation <b>" +  relation.getTitle() + relation.getRelEntityName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A>.</li>";
        if(field != null && rfield != null) {
          if(!field.getType().equals(rfield.getType()) && !field.getType().startsWith(rfield.getType()) && !rfield.getType().startsWith(field.getType()))
            warningString = warningString + "<li><div style=\"color: red;\">[RelationFieldTypesDifferent]</div> The field type (" + field.getType() + ") of <b>" + field.getName() + "</b> of entity <A href=\"#" + entity.getEntityName() + "\">" + entity.getEntityName() + "</A> is not the same as field type (" + rfield.getType() + ") of <b>" + rfield.getName() + "</b> of entity <A href=\"#" + relation.getRelEntityName() + "\">" + relation.getRelEntityName() + "</A> for relation <b>" +  relation.getTitle() + relation.getRelEntityName() + "</b>.</li>";
        }
      }
    }
%>
    <tr bgcolor="#FEEEEE"> 
      <td> 
        <div align="left" class='relationtext'>
          <b><%=relation.getTitle()%></b><A href='#<%=relation.getRelEntityName()%>' class='rlinktext'><%=relation.getRelEntityName()%></A>
        </div>
      </td>
      <td width="60%" colspan='4'><div align="left" class='relationtext'>
        <%=relation.getType()%>:<%if(relation.getType().length()==3){%>&nbsp;<%}%>
        <%for (int km = 0; km < relation.getKeyMapsSize(); km++){ ModelKeyMap keyMap = relation.getKeyMap(km);%>
          <br>&nbsp;&nbsp;<%=km+1%>)&nbsp;
          <%if(keyMap.getFieldName().equals(keyMap.getRelFieldName())){%><%=keyMap.getFieldName()%>
          <%}else{%><%=keyMap.getFieldName()%> : <%=keyMap.getRelFieldName()%><%}%>
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

<%if(checkWarnings) {%>
  <A name='WARNINGS'>WARNINGS:</A>
  <OL>
  <%=warningString%>
  </OL>
<%}%>

</body>
</html>
<%}else{%>
<html>
<head>
  <title>Entity Editor</title>
</head>
<body>

<H3>Entity Editor</H3>

ERROR: You do not have permission to use this page (ENTITY_MAINT needed)

</body>
</html>
<%}%>

<%!
public TreeSet reservedWords = new TreeSet();
public static final String[] rwArray = {
 "ABORT", "ABS", "ABSOLUTE", "ACCEPT", "ACCES", "ACS", "ACTION", "ACTIVATE",
 "ADD", "ADDFORM", "ADMIN", "AFTER", "AGGREGATE", "ALIAS", "ALL", "ALLOCATE",
 "ALTER", "ANALYZE", "AND", "ANDFILENAME", "ANY", "ANYFINISH", "APPEND",
 "ARCHIVE", "ARE", "ARRAY", "AS", "ASC", "ASCENDING", "ASCII", "ASSERT",
 "ASSERTION", "ASSIGN", "AT", "ATTRIBUTE", "ATTRIBUTES", "AUDIT", "AUTHID",
 "AUTHORIZATION", "AUTONEXT", "AUTO_INCREMENT", "AVERAGE", "AVG", "AVGU",
 "AVG_ROW_LENGTH", 

 "BACKOUT", "BACKUP", "BEFORE", "BEGIN", "BEGINLOAD", "BEGINMODIFY",
 "BEGINNING", "BEGWORK", "BETWEEN", "BETWEENBY", "BINARY", "BINARY_INTEGER",
 "BIT", "BIT_LENGTH", "BLOB", "BODY", "BOOLEAN", "BORDER", "BOTH", "BOTTOM",
 "BREADTH", "BREAK", "BREAKDISPLAY", "BROWSE", "BUFERED", "BUFFER", "BUFFERED",
 "BULK", "BY", "BYTE", 

 "CALL", "CANCEL", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CHANGE",
 "CHAR", "CHAR_LENGTH", "CHAR_BASE", "CHARACTER", "CHARACTER_LENGTH",
 "CHAR_CONVERT", "CHECK", "CHECKPOINT", "CHECKSUM", "CHR2FL", "CHR2FLO",
 "CHR2FLOA", "CHR2FLOAT", "CHR2INT", "CLASS", "CLEAR", "CLEARROW", "CLIPPED",
 "CLOB", "CLOSE", "CLUSTER", "CLUSTERED", "CLUSTERING", "COALESCE", "COBOL",
 "COLD", "COLLATE", "COLLATION", "COLLECT", "COLUMN", "COLUMNS", "COMMAND",
 "COMMENT", "COMMIT", "COMMITTED", "COMPLETION", "COMPRESS", "COMPUTE",
 "CONCAT", "COND", "CONDITION", "CONFIG", "CONFIRM", "CONNECT", "CONNECTION",
 "CONSTANT", "CONSTRAINT", "CONSTRAINTS", "CONSTRUCT", "CONSTRUCTOR", "CONTAIN",
 "CONTAINS", "CONTAINSTABLE", "CONTINUE", "CONTROLROW", "CONVERT", "COPY",
 "CORRESPONDING", "COUNT", "COUNTU", "COUNTUCREATE", "CRASH", "CREATE", "CROSS",
 "CUBE", "CURRENT", "CURRENT_DATE", "CURRENT_PATH", "CURRENT_ROLE",
 "CURRENT_SESSION", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER",
 "CURSOR", "CURVAL", "CYCLE", 

 "DATA", "DATALINK", "DATABASE", "DATABASES", "DATAPAGES", "DATA_PGS", "DATE",
 "DATETIME", "DAY", "DAY_HOUR", "DAY_MINUTE", "DAY_SECOND", "DAYNUM",
 "DAYOFMONTH", "DAYOFWEEK", "DAYOFYEAR", "DBA", "DBCC", "DBE", "DBEFILE",
 "DBEFILEO", "DBEFILESET", "DBSPACE", "DBYTE", "DEALLOCATE", "DEC", "DECENDING",
 "DECIMAL", "DECLARE", "DEFAULT", "DEFAULTS", "DEFER", "DEFERRABLE", "DEFINE",
 "DEFINITION", "DELAY_KEY_WRITE", "DELAYED", "DELETE", "DELETEROW", "DENY",
 "DEPTH", "DEREF", "DESC", "DESCENDING", "DESCENDNG", "DESCRIBE", "DESCRIPTOR",
 "DESTPOS", "DESTROY", "DEVICE", "DEVSPACE", "DIAGNOSTICS", "DICTIONARY",
 "DIRECT", "DIRTY", "DISCONNECT", "DISK", "DISPLACE", "DISPLAY", "DISTINCT",
 "DISTINCTROW", "DISTRIBUTED", "DISTRIBUTION", "DIV", "DO", "DOES", "DOMAIN",
 "DOUBLE", "DOWN", "DROP", "DUAL", "DUMMY", "DUMP", "DUPLICATES", 

 "EACH", "EBCDIC", "EDITADD", "EDITUPDATE", "ED_STRING", "ELSE", "ELSEIF",
 "ELSIF", "ENCLOSED", "END", "ENDDATA", "ENDDISPLAY", "ENDFORMS", "ENDIF",
 "ENDING", "ENDLOAD", "ENDLOOP", "ENDMODIFY", "ENDPOS", "ENDRETRIEVE",
 "ENDSELECT", "ENDWHILE", "END_ERROR", "END_EXEC", "END_FETCH", "END_FOR",
 "END_GET", "END_MODIFY", "END_PLACE", "END_SEGMENT_S", "END_SEGMENT_STRING",
 "END_STORE", "END_STREAM", "ENUM", "EQ", "EQUALS", "ERASE", "ERROR", "ERRLVL",
 "ERROREXIT", "ESCAPE", "ESCAPED", "EVALUATE", "EVALUATING", "EVERY", "EXCEPT",
 "EXCEPTION", "EXCLUSIVE", "EXEC", "EXECUTE", "EXISTS", "EXIT", "EXPAND",
 "EXPANDING", "EXPLAIN", "EXPLICIT", "EXTEND", "EXTENDS", "EXTENT", "EXTERNAL",
 "EXTRACT", 

 "FALSE", "FETCH", "FIELD", "FIELDS", "FILE", "FILENAME", "FILLFACTOR",
 "FINALISE", "FINALIZE", "FINDSTR", "FINISH", "FIRST", "FIRSTPOS", "FIXED",
 "FL", "FLOAT", "FLOAT4", "FLOAT8", "FLUSH", "FOR", "FORALL", "FOREACH",
 "FOREIGN", "FORMAT", "FORMDATA", "FORMINIT", "FORMS", "FORTRAN", "FOUND",
 "FRANT", "FRAPHIC", "FREE", "FREETEXT", "FREETEXTTABLE", "FROM", "FRS",
 "FULL", "FUNCTION", 

 "GE", "GENERAL", "GET", "GETFORM", "GETOPER", "GETROW", "GLOBAL", "GLOBALS",
 "GO", "GOTO", "GRANT", "GRANTS", "GRAPHIC", "GROUP", "GROUPING", "GT", 

 "HANDLER", "HASH", "HAVING", "HEAP", "HEADER", "HELP", "HELPFILE", "HELP_FRS",
 "HIGH_PRIORITY", "HOLD", "HOLDLOCK", "HOSTS", "HOUR", "HOUR_MINUTE",
 "HOUR_SECOND", 

 "IDENTIFIED", "IDENTIFIELD", "IDENTITY", "IDENTITY_INSERT", "IF", "IFDEF",
 "IGNORE", "IMAGE", "IMMEDIATE", "IMMIDIATE", "IMPLICIT", "IN", "INCLUDE",
 "INCREMENT", "INDEX", "INDEXED", "INDEXNAME", "INDEXPAGES", "INDICATOR",
 "INFIELD", "INFILE", "INFO", "INGRES", "INIT", "INITIAL", "INITIALISE",
 "INITIALIZE", "INITIALLY", "INITTABLE", "INNER", "INOUT", "INPUT",
 "INQUIRE_EQUEL", "INQUIRE_FRS", "INQUIRE_INGRES", "INQUIR_FRS", "INSERT",
 "INSERT_ID", "INSERTROW", "INSTRUCTIONS", "INT", "INT1", "INT2CHR", "INT2",
 "INT3", "INT4", "INT8", "INTEGER", "INTEGRITY", "INTERESECT", "INTERFACE",
 "INTERRUPT", "INTERSECT", "INTERVAL", "INTO", "INTSCHR", "INVOKE", "IS",
 "ISAM", "ISOLATION", "ITERATE", 

 "JAVA", "JOIN", "JOURNALING", 

 "KEY", "KEYS", "KILL", 

 "LABEL", "LANGUAGE", "LARGE", "LAST", "LAST_INSERT_ID", "LASTPOS", "LATERAL",
 "LE", "LEADING", "LEAVE", "LEFT", "LENGTH", "LENSTR", "LESS", "LET", "LEVEL",
 "LIKE", "LIKEPROCEDURETP", "LIMIT", "LIMITED", "LINE", "LINENO", "LINES",
 "LINK", "LIST", "LISTEN", "LOAD", "LOADTABLE", "LOADTABLERESUME", "LOCAL",
 "LOCALTIME", "LOCALTIMESTAMP", "LOCATION", "LOCATOR", "LOCK", "LOCKING", "LOG",
 "LOGS", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "LOWER",
 "LPAD", "LT", 

 "MAIN", "MANUITEM", "MARGIN", "MATCH", "MATCHES", "MATCHING", "MAX",
 "MAX_ROWS", "MAXEXTENTS", "MAXPUBLICUNION", "MAXRECLEN", "MDY", "MEDIUMBLOB",
 "MEDIUMINT", "MEDIUMTEXT", "MEETS", "MENU", "MENUITEM", "MENUITEMSCREEN",
 "MESSAGE", "MESSAGERELOCATE", "MESSAGESCROLL", "MFETCH", "MIDDLEINT", "MIN",
 "MIN_ROWS", "MINRECLEN", "MINRETURNUNTIL", "MINUS", "MINUTE", "MINUTE_SECOND",
 "MIRROREXIT", "MISLABEL", "MISSING", "MIXED", "MOD", "MODE", "MODIFIES",
 "MODIFY", "MODIFYREVOKEUPDATE", "MODULE", "MONEY", "MONITOR", "MONTH",
 "MONTHNAME", "MOVE", "MULTI", "MYISAM", 

 "NAME", "NAMES", "NATIONAL", "NATURAL", "NATURALN", "NCHAR", "NCLOB", "NE",
 "NEED", "NEW", "NEWLOG", "NEXT", "NEXTSCROLLDOWN", "NEXTVAL", "NO", "NOAUDIT",
 "NOCHECK", "NOCOMPRESS", "NOCOPY", "NOCR", "NOJOURNALING", "NOLIST", "NOLOG",
 "NONCLUSTERED", "NONE", "NORMAL", "NORMALIZE", "NOSYSSORT", "NOT", "NOTFFOUND",
 "NOTFOUND", "NOTIFY", "NOTRANS", "NOTRIM", "NOTRIMSCROLLUP", "NOTROLLBACKUSER",
 "NOWAIT", "NULL", "NULLIF", "NULLIFY", "NULLSAVEUSING", "NULLVAL", "NUMBER",
 "NUMBER_BASE", "NUMERIC", "NXFIELD", 

 "OBJECT", "OCIROWID", "OCTET_LENGTH", "OF", "OFF", "OFFLINE", "OFFSET",
 "OFFSETS", "OFSAVEPOINTVALUES", "OLD", "ON", "ONCE", "ONLINE", "ONLY",
 "ONSELECTWHERE", "ONTO", "OPAQUE", "OPEN", "OPENDATASOURCE", "OPENQUERY",
 "OPENROWSET", "OPENXML", "OPENSETWHILE", "OPENSLEEP", "OPERATION", "OPERATOR",
 "OPTIMIZE", "OPTION", "OPTIONALLY", "OPTIONS", "OR", "ORDER", "ORDERSQLWORK",
 "ORDINALITY", "ORGANIZATION", "ORSOMEWITH", "ORSORT", "OTHERS", "OTHERWISE",
 "OUT", "OUTER", "OUTFILE", "OUTPUT", "OUTPUT_PAGE", "OUTSTOP", "OVER",
 "OVERLAPS", "OWNER", "OWNERSHIP", 

 "PACK_KEYS", "PACKAGE", "PAD", "PAGE", "PAGENO", "PAGES", "PARAM", "PARAMETER",
 "PARAMETERS", "PARTIAL", "PARTITION", "PASCAL", "PASSWORD", "PATH", "PATHNAME",
 "PATTERN", "PAUSE", "PCTFREE", "PERCENT", "PERIOD", "PERM", "PERMANENT",
 "PERMIT", "PERMITSUM", "PIPE", "PLACE", "PLAN", "PLI", "PLS_INTEGER", "POS",
 "POSITION", "POSITIVE", "POSITIVEN", "POSTFIX", "POWER", "PRAGMA", "PRECEDES",
 "PRECISION", "PREFIX", "PREORDER", "PREPARE", "PREPARETABLE", "PRESERVE",
 "PREV", "PREVIOUS", "PREVISION", "PRIMARY", "PRINT", "PRINTER", "PRINTSCREEN",
 "PRINTSCREENSCROLL", "PRINTSUBMENU", "PRINTSUMU", "PRIOR", "PRIV", "PRIVATE",
 "PRIVILAGES", "PRIVILAGESTHEN", "PRIVILEGES", "PROC", "PROCEDURE", "PROCESS",
 "PROCESSEXIT", "PROCESSLIST", "PROGRAM", "PROGUSAGE", "PROMPT",
 "PROMPTSCROLLDOWN", "PROMPTTABLEDATA", "PROTECT", "PSECT", "PUBLIC",
 "PUBLICREAD", "PUT", "PUTFORM", "PUTFORMSCROLLUP", "PUTFORMUNLOADTABLE",
 "PUTOPER", "PUTOPERSLEEP", "PUTROW", "PUTROWSUBMENU", "PUTROWUP", 

 "QUERY", "QUICK", "QUIT", 

 "RAISERROR", "RANGE", "RANGETO", "RAW", "RDB$DB_KEY", "RDB$LENGTH",
 "RDB$MISSING", "RDB$VALUE", "RDB4DB_KEY", "RDB4LENGTH", "RDB4MISSING",
 "RDB4VALUE", "READ", "READS", "READONLY", "READPASS", "READTEXT", "READWRITE",
 "READY", "READ_ONLY", "READ_WRITE", "REAL", "RECONFIGURE", "RECONNECT",
 "RECORD", "RECOVER", "RECURSIVE", "REDISPLAY", "REDISPLAYTABLEDATA",
 "REDISPLAYVALIDATE", "REDO", "REDUCED", "REF", "REFERENCES", "REFERENCING",
 "REGEXP", "REGISTER", "REGISTERUNLOADDATA", "REGISTERVALIDROW", "REJECT",
 "RELATIVE", "RELEASE", "RELOAD", "RELOCATE", "RELOCATEUNIQUE", "REMOVE",
 "REMOVEUPRELOCATEV", "REMOVEVALIDATE", "REMOVEWHENEVER", "RENAME", "REPEAT",
 "REPEATABLE", "REPEATED", "REPEATVALIDROW", "REPLACE", "REPLACEUNTIL",
 "REPLICATION", "REPLSTR", "REPORT", "REQUEST_HANDLE", "RESERVED_PGS",
 "RESERVING", "RESET", "RESIGNAL", "RESOURCE", "REST", "RESTART", "RESTORE",
 "RESTRICT", "RESULT", "RESUME", "RETRIEVE", "RETRIEVEUPDATE", "RETURN",
 "RETURNS", "RETURNING", "REVERSE", "REVOKE", "RIGHT", "RLIKE", "ROLE",
 "ROLLBACK", "ROLLFORWARD", "ROLLBACK", "ROLLUP", "ROUND", "ROUTINE", "ROW",
 "ROWCNT", "ROWCOUNT", "ROWGUID_COL", "ROWID", "ROWLABEL", "ROWNUM", "ROWS",
 "ROWTYPE", "RPAD", "RULE", "RUN", "RUNTIME", 

 "SAMPLSTDEV", "SAVE", "SAVEPOINT", "SAVEPOINTWHERE", "SAVEVIEW", "SCHEMA",
 "SCOPE", "SCREEN", "SCROLL", "SCROLLDOWN", "SCROLLUP", "SEARCH", "SECOND",
 "SECTION", "SEGMENT", "SEL", "SELE", "SELEC", "SELECT", "SELUPD", "SEPERATE",
 "SEQUENCE", "SERIAL", "SESSION", "SESSION_USER", "SET", "SETOF", "SETS",
 "SETWITH", "SET_EQUEL", "SET_FRS", "SET_INGRES", "SETUSER", "SHARE", "SHARED",
 "SHORT", "SHOW", "SHUTDOWN", "SIGNAL", "SIZE", "SKIP", "SLEEP", "SMALLFLOAT",
 "SMALLINT", "SOME", "SONAME", "SORT", "SORTERD", "SOUNDS", "SOURCEPOS",
 "SPACE", "SPACES", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQL_BIG_RESULT",
 "SQL_BIG_SELECTS", "SQL_BIG_TABLES", "SQL_LOG_OFF", "SQL_LOG_UPDATE",
 "SQL_LOW_PRIORITY_UPDATES", "SQL_SELECT_LIMIT", "SQL_SMALL_RESULT",
 "SQL_WARNINGS", "SQLCODE", "SQLDA", "SQLERRM", "SQLERROR", "SQLEXCEPTION",
 "SQLEXEPTION", "SQLEXPLAIN", "SQLNOTFOUND", "SQLSTATE", "SQLWARNING", "SQRT",
 "STABILITY", "START", "STARTING", "STARTPOS", "START_SEGMENT",
 "START_SEGMENTED_?", "START_STREAM", "START_TRANSACTION", "STATE", "STATIC",
 "STATISTICS", "STATUS", "STDDEV", "STDEV", "STEP", "STOP", "STORE",
 "STRAIGHT_JOIN", "STRING", "STRUCTURE", "SUBMENU", "SUBSTR", "SUBSTRING",
 "SUBTYPE", "SUCCEEDS", "SUCCESFULL", "SUCCESSFULL", "SUM", "SUMU", "SUPERDBA",
 "SYB_TERMINATE", "SYNONYM", "SYSDATE", "SYSSORT", "SYSTEM_USER", 

 "TABLE", "TABLEDATA", "TABLES", "TEMP", "TEMPORARY", "TERMINATE", "TERMINATED",
 "TEXT", "TEXTSIZE", "THAN", "THEN", "THROUGH", "THRU", "TID", "TIME",
 "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TINYBLOB", "TINYINT",
 "TINYTEXT", "TO", "TODAY", "TOLOWER", "TOP", "TOTAL", "TOUPPER", "TP",
 "TRAILER", "TRAILING", "TRAN", "TRANS", "TRANSACTION", "TRANSACTION_HANDLE",
 "TRANSFER", "TRANSLATE", "TRANSLATION", "TREAT", "TRIGGER", "TRING", "TRUE",
 "TRUNC", "TRUNCATE", "TSEQUAL", "TYPE", 

 "UID", "UNBUFFERED", "UNDER", "UNDO", "UNION", "UNIQUE", "UNKNOWN", "UNLISTEN",
 "UNLOAD", "UNLOADDATA", "UNLOADTABLE", "UNLOCK", "UNTIL", "UP", "UPDATE",
 "UPDATETEXT", "UPPER", "USAGE", "USE", "USED_PGS", "USER", "USING", 

 "VACUUM", "VALIDATE", "VALIDROW", "VALUE", "VALUES", "VARBINARY", "VARC",
 "VARCH", "VARCHA", "VARCHAR", "VARGRAPHIC", "VARIABLE", "VARIABLES",
 "VARIANCE", "VARYING", "VERB_TIME", "VERBOSE", "VERIFY", "VERSION", "VIEW", 

 "WAIT", "WAITFOR", "WAITING", "WARNING", "WEEKDAY", "WHEN", "WHENEVER",
 "WHERE", "WHILE", "WINDOW", "WITH", "WITHOUT", "WORK", "WRAP", "WRITE",
 "WRITEPASS", "WRITETEXT", 

 "YEAR", 

 "ZEROFILL", "ZONE" };


public void initReservedWords() {
  //create extensive list of reserved words
  int asize = rwArray.length;
  Debug.log("[initReservedWords] array length=" + asize);
  for(int i=0; i<asize; i++) {
    reservedWords.add(rwArray[i]);
  }
}
%>
