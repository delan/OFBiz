Hier are some hints:

0) You will need jdom package. If you do not have some, try jdom.org Version
jdom-b7 is the latest one.

1) build.bat and run.bat: You have to define some Envirnoment variables, like
JAVA_HOME, ANT_HOME and JDOM_HOME. 
Please use command: build.bat dist

2) Another script run.bat run the designer. The only (hopefully) setting,
that you will need to change is the Variable DESIGNER_ROOT_DIR

3) The Main Class and the Parameter, that I used are:
org.ofbiz.designer.newdesigner.NetworkEditor DEMO2level DEMO2level DefaultDomain

4) The Stuff that I tested and that hopefully will function also on your
computer:

o  In Menu Tools->Data editor. In Data editor i could select all the files,
but not the file Demo2level itself (exception). When you select other files,
then the methods, fields and exception are changed.

o No other Editors from Menu functions

o In Advanced Tab of Task Editor->Click Button Edit Realisation, Then you
will see (hopefully) the Realization.
  Hint: Try to play with the task type around (on the general tab): you will
see different realization (Transctional/Not Transactional and so on)

o Tab Network: in Output Arcs List select some arc, then click on
  Output Task Editor, Output Operator Editor, Output Arc Editor: everything
have to function.
