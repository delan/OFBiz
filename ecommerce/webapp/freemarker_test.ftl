<#assign products = delegator.findAll("Product")>
<#list products as product>
${product.description?if_exists} - ${product.comments?default("N/A")}<br>
</#list>
<transform ofbizUrl>/main</transform>