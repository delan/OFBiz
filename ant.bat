ECHO OFF
REM #####################################################################
REM # Copyright 2001-2006 The Apache Software Foundation
REM #
REM # Licensed under the Apache License, Version 2.0 (the "License"); you may not
REM # use this file except in compliance with the License. You may obtain a copy of
REM # the License at
REM #
REM # http://www.apache.org/licenses/LICENSE-2.0
REM #
REM # Unless required by applicable law or agreed to in writing, software
REM # distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
REM # WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
REM # License for the specific language governing permissions and limitations
REM # under the License.
REM #####################################################################
ECHO ON

"%JAVA_HOME%\bin\java" -jar framework/base/lib/ant-launcher.jar %1 %2 %3 %4 %5 %6

