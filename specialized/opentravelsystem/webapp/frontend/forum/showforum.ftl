<#import "bloglib.ftl" as blog/>

<@blog.renderBlog contentId=forumId />
<@blog.nextPrev requestURL=requestURL queryString=queryString listSize=listSize lowIndex=lowIndex highIndex=highIndex viewSize=viewSize viewIndex=viewIndex/>
