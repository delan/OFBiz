//Copyright © 2005, John Goodman - john.goodman(at)unverse.net  *date 051117

//This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
//This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//A copy of the GNU General Public License can be obtained at: http://www.gnu.org/licenses/gpl.html

var nlTag='|div|p|table|tbody|tr|td|th|title|head|body|script|comment|li|meta|h1|h2|h3|h4|h5|h6|hr|ul|ol|option|select|'; 
var tagNl='|html|head|body|p|th|style|'; 
var regCmt=new RegExp(); 
regCmt.compile("^<!--(.*)-->$"); 
var regHyph=new RegExp(); 
regHyph.compile("-$"); 
function get_xhtml(node,lang,encoding,ndNl,inPre){
 var i; 
 var tx=''; 
 var kids=node.childNodes; 
 var kidsL=kids.length; 
 var tagNm; 
 var doNl=ndNl?true:false; 
 var pagMode=true; 
 for(i=0; i<kidsL; i++){
  var kid=kids[i]; 
  switch(kid.nodeType){
   case 1:{
    var tagNm=String(kid.tagName).toLowerCase(); 
    if(tagNm=='')break; 
    if(tagNm=='meta'){
     var metaNm=String(kid.name).toLowerCase(); 
     if(metaNm=='generator')break; 
    }
    if(!ndNl&&tagNm=='body'){ pagMode=false; }
    if(tagNm=='!'){
     var bits=regCmt.exec(kid.tx); 
     if(bits){
      var innerTx=bits[1]; 
      tx+=tidyCmt(innerTx); 
     }
    }else{
     if(tagNm=='html'){
      tx='<?xml version="1.0" encoding="'+encoding+'"?>\n<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">\n'; 
     }
     if(nlTag.indexOf('|'+tagNm+'|')!=-1){
      if((doNl||tx!='')&&!inPre)tx+='\n'; 
      else doNl=true; 
     }
     tx+='<'+tagNm; 
     var attr=kid.attributes; 
     var atLn=attr.length; 
     var atVal; 
     var atLang=false; 
     var atXml=false; 
     var atXmlns=false; 
     var isAlt=false; 
     for(j=0; j<atLn; j++){
      var atNm=attr[j].nodeName.toLowerCase(); 
      if(!attr[j].specified&&(atNm!='selected'||!kid.selected)
         && (atNm!='style'||kid.style.cssText=='')
         && atNm!='value')
       continue; 
      if(atNm=='_moz_dirty'||atNm=='_moz_resizing'||tagNm=='br'&&atNm=='type'&&kid.getAttribute('type')=='_moz')
       continue; 
      var valid_attr=true;
      switch(atNm){
       case "style":atVal=kid.style.cssText.toLowerCase(); 
        break; 
       case "class":atVal=kid.className; 
        break; 
       case "http-equiv":atVal=kid.httpEquiv; 
        break; 
       case "noshade":
       case "checked":
       case "selected":
       case "multiple":
       case "nowrap":
       case "disabled":atVal=atNm;
        break; 
       default:try{
        atVal=kid.getAttribute(atNm,2); 
       }catch(e){ valid_attr=false; }
      }
      if(atNm=='lang'){
       atLang=true; 
       atVal=lang; 
      }
      if(atNm=='xml:lang'){
       atXml=true; 
       atVal=lang; 
      } 
      if(atNm=='xmlns') atXmlns=true; 
      if(valid_attr){
       if(!(tagNm=='li'&&atNm=='value')){
        tx+=' '+atNm+'="'+tidyAt(atVal)+'"';  
       }
      }
      if(atNm=='alt')isAlt=true; 
     }
     if(tagNm=='img'&&!isAlt) tx+=' alt=""';
     if(tagNm=='html'){
      if(!atLang)tx+=' lang="'+lang+'"'; 
      if(!atXml)tx+=' xml:lang="'+lang+'"'; 
      if(!atXmlns)tx+=' xmlns="http://www.w3.org/1999/xhtml"'; 
     }
    if(kid.canHaveChildren||kid.hasChildNodes()){
     tx+='>'; 
     if(tagNl.indexOf('|'+tagNm+'|')!=-1){}
     tx+=get_xhtml(kid,lang,encoding,true,inPre||tagNm=='pre'?true:false); 
     tx+='</'+tagNm+'>'; 
    }else{ 
     if(tagNm=='style'||tagNm=='title'||tagNm=='script'){
      tx+='>';  
      var innerTx; 
      if(tagNm=='script'){
       innerTx=kid.tx; 
      }else innerTx=kid.innerHTML; 
      if(tagNm=='style'){
       innerTx=String(innerTx).replace(/[\n]+/g,'\n'); 
      }
      tx+=innerTx+'</'+tagNm+'>'; 
     }else{ tx+=' />'; }
    }
   }
   break; 
  }
  case 3:{
   if(!inPre){
    if(kid.nodeValue!='\n'){ tx+=tidyTxt(kid.nodeValue); }
   }
   else tx+=kid.nodeValue; 
   break; 
  }
  case 8:{
   tx+=tidyCmt(kid.nodeValue); 
   break; 
  }
  default: break; 
  }
 }
 if(!ndNl&&!pagMode){
  tx=tx.replace(/<\/?head>[\n]*/gi,""); 
  tx=tx.replace(/<head \/>[\n]*/gi,""); 
  tx=tx.replace(/ <\/?body>[\n]*/gi,""); 
 }
 return tx; 
}
function tidyCmt(tx){
 tx=tx.replace(/--/g,"__"); 
 if(regHyph.exec(tx)){
  tx+=" "; 
 }return "<!--"+tx+"-->"; 
}
function tidyTxt(tx){
 return String(tx).replace(/\n{2,}/g,"\n").replace(/\&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/\u00A0/g,"&nbsp;");
}
function tidyAt(tx){
 return String(tx).replace(/\&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/\"/g,"&quot;");
}