@REM   Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
@REM   reserved.

@REM Begin all REM lines with '@' in case JSETTLERS_BATCH_ECHO is 'on'
@echo off
@REM enable echoing my setting JSETTLERS_BATCH_ECHO to 'on'
@if "%JSETTLERS_BATCH_ECHO%" == "on"  echo %JSETTLERS_BATCH_ECHO%

@REM Execute a user defined script before this one
if exist "%HOME%\jsettlersrc_pre.bat" call "%HOME%\jsettlersrc_pre.bat"

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM ==== START VALIDATION ====
@REM %~dp0 is expanded pathname of the current script under NT
set DEFAULT_JSETTLERS_HOME=%~dp0..

if "%JSETTLERS_HOME%"=="" set JSETTLERS_HOME=%DEFAULT_JSETTLERS_HOME%
set DEFAULT_JSETTLERS_HOME=

@REM Slurp the command line arguments. This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set JSETTLERS_CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
set JSETTLERS_CMD_LINE_ARGS=%JSETTLERS_CMD_LINE_ARGS% %1
shift
goto setupArgs
@REM This label provides a place for the argument list loop to break out 
@REM and for NT handling to skip to.

:doneStart
@REM find JSETTLERS_HOME if it does not exist due to either an invalid value
@REM passed by the user or the %0 problem on Windows 9x
if exist "%JSETTLERS_HOME%\JSettlersServer.jar" goto checkJava

@REM check for JSettlers in Program Files on system drive
if not exist "%SystemDrive%\Program Files\JSettlers" goto checkSystemDrive
set JSETTLERS_HOME=%SystemDrive%\Program Files\JSettlers
goto checkJava

:checkSystemDrive
@REM check for JSettlers in root directory of system drive
if not exist %SystemDrive%\JSettlers\JSettlersServer.jar goto checkCDrive
set JSETTLERS_HOME=%SystemDrive%\JSettlers
goto checkJava

:checkCDrive
@REM check for jsettlers in C:\JSettlers for Win9X users
if not exist C:\JSettlers\JSettlers.jar goto noJSettlersHome
set JSETTLERS_HOME=C:\JSettlers
goto checkJava

:noJSettlersHome
echo JSETTLERS_HOME is set incorrectly or JSetttlers could not be located. Please set JSETTLERS_HOME.
goto end

:checkJava
set _JAVACMD=%JAVACMD%
set LOCALCLASSPATH=%JSETTLERS_HOME%\JSettlers.jar;%CLASSPATH%
set LOCALCLASSPATH=%JSETTLERS_HOME%\JSettlersServer.jar;%LOCALCLASSPATH%
for %%i in ("%JSETTLERS_HOME%\lib\*.jar") do call "%JSETTLERS_HOME%\bin\lcp.bat" %%i

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
if exist "%JAVA_HOME%\lib\tools.jar" set LOCALCLASSPATH=%JAVA_HOME%\lib\tools.jar;%LOCALCLASSPATH%
if exist "%JAVA_HOME%\lib\classes.zip" set LOCALCLASSPATH=%JAVA_HOME%\lib\classes.zip;%LOCALCLASSPATH%
goto runJSettlers

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe
echo.
echo Warning: JAVA_HOME environment variable is not set.
echo   If build fails because sun.* classes could not be found
echo   you will need to set the JAVA_HOME environment variable
echo   to the installation directory of java.
echo.

:runJSettlers
set MAIN_CLASS=soc.server.SOCServer

"%_JAVACMD%" %JSETTLERES_OPTS% -classpath "%LOCALCLASSPATH%" "-Djsettlers.home=%JSETTLERS_HOME%" %MAIN_CLASS% %JSETTLERS_ARGS% %JSETTLERS_CMD_LINE_ARGS%
goto end

goto end

:end
set LOCALCLASSPATH=
set _JAVACMD=
set JSETTLERS_CMD_LINE_ARGS=

if "%OS%"=="Windows_NT" @endlocal

:mainEnd
if exist "%HOME%\jsettlersrc_post.bat" call "%HOME%\jsettlersrc_post.bat"

