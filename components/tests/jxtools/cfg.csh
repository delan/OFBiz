#!/bin/csh

set thispath=$0
if ($thispath == $thispath:h) then
    set thispath="."
else
    set thispath=$thispath:h
endif
source $thispath/quickClasspath.csh
java -cp $quickClasspath com.jxml.quick.config.Main $*
