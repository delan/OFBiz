<#import "bloglib.ftl" as blog/>

<@blog.renderBlog contentId=forumId />
<@blog.nextPrev requestURL=requestURL queryString=queryString listSize=context.listSize lowIndex=context.lowIndex highIndex=context.highIndex viewSize=viewSize viewIndex=viewIndex/>
