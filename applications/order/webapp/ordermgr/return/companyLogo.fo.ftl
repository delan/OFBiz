<fo:block white-space-collapse="false" border-style="solid" border-width="0.1pt">
<#-- company information goes here -->
<#-- <@ofbizContentUrl> does not work here by default: a browser understands what /images/... means but a FOP does not.  So, you have to use an explicit
URL for your content -->
<fo:external-graphic alignment-adjust="before-edge" src="http://127.0.0.1:8080/images/ofbiz_powered.gif" overflow="hidden" width="88pt" height="31pt"/>
Company Header Information Here
put addresses, etc. etc.
</fo:block>
 
