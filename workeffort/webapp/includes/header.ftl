<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${layoutSettings.companyName?if_exists}: ${page.title?if_exists}</title>
    <script language='javascript' src='<@ofbizContentUrl>/images/calendar1.js</@ofbizContentUrl>' type='text/javascript'></script>
	<link rel='stylesheet' href='<@ofbizContentUrl>/images/maincss.css</@ofbizContentUrl>' type='text/css'>
	<link rel='stylesheet' href='<@ofbizContentUrl>/images/tabstyles.css</@ofbizContentUrl>' type='text/css'> 
</head>

<body>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
			<#if layoutSettings.headerImageUrl?has_content>
            <td align=left width='1%'><IMG src='<@ofbizContentUrl>${layoutSettings.headerImageUrl}</@ofbizContentUrl>'></TD>
          	</#if>        
        
          <TD align=right width='1%' nowrap <#if layoutSettings.headerRightBackgroundUrl?has_content>background='<@ofbizContentUrl>${layoutSettings.headerRightBackgroundUrl}</@ofbizContentUrl>'</#if>>
              <#if person?has_content>              
                <div class="insideHeaderText">Welcome&nbsp;${person.firstName}&nbsp;${person.lastName}!</div>
              <#elseif partyGroup?has_content>
                  <div class="insideHeaderText">Welcome&nbsp;${partyGroup.groupName}!</div>
               <#else>
                  <div class="insideHeaderText">Welcome!</div>
               </#if>
            <div class="insideHeaderText">&nbsp;${nowTimestamp.toString()}</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
${pages.get("/includes/appbar.ftl")}

<div class="centerarea">

<div class="apptitle">&nbsp;WorkEfforts/Projects/Requests&nbsp;</div>
<div class="row">
  <div class="col"><a href="<@ofbizUrl>/main</@ofbizUrl>" class="headerButtonLeft">Main</a></div>   
  <div class="col"><a href="<@ofbizUrl>/mytasks</@ofbizUrl>" class="headerButtonLeft">Task&nbsp;List</a></div>
  <div class="col"><a href="<@ofbizUrl>/month</@ofbizUrl>" class="headerButtonLeft">Calendar</a></div>
  <div class="col"><a href="<@ofbizUrl>/projectlist</@ofbizUrl>" class="headerButtonLeft">Projects</a></div>
  <div class="col"><a href="<@ofbizUrl>/requestlist</@ofbizUrl>" class="headerButtonLeft">Requests</a></div>  
  <#if userLogin?has_content>
  <div class="col-right"><a href="<@ofbizUrl>/logout</@ofbizUrl>" class="headerButtonRight">Logout</a></div>
  <#else>
    <div class="col-right"><a href='<@ofbizUrl>${loginUrl}</@ofbizUrl>' class='headerButtonRight'>Login</a></div>
  </#if>
  <div class="col-fill">&nbsp;</div>
</div>

