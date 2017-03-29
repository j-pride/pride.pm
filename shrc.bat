set J2EE_HOME=D:\Programme\j2sdkee1.4
set JAVA_HOME=D:\programme\Java\jdk1.6.0_20
set ANT_HOME=D:\programme\apache-ant-1.7.0
set PRIDE_HOME=D:\work\%USERNAME%\strategie\trunk\06_Development\pride

set MAKEFLAGS=--win32

set CLASSPATH=%PRIDE_HOME%\src
set CLASSPATH=%CLASSPATH%;%PRIDE_HOME%\test
set CLASSPATH=%CLASSPATH%;%PRIDE_HOME%\classes
set CLASSPATH=%CLASSPATH%;%PRIDE_HOME%\pride.jar
set CLASSPATH=%CLASSPATH%;%PRIDE_HOME%\lib\junit.jar
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\jre\lib\rt.jar
set CLASSPATH=%CLASSPATH%;%J2EE_HOME%\lib\j2ee.jar
set CLASSPATH=%CLASSPATH%;%J2EE_HOME%\lib\cloudscape\cloudclient.jar
set CLASSPATH=%CLASSPATH%;%J2EE_HOME%\lib\cloudscape\RmiJdbc.jar
set CLASSPATH=%CLASSPATH%;%PRIDE_HOME%\lib\db2java.zip
set CLASSPATH=%CLASSPATH%;%PRIDE_HOME%\lib\db2jcc.jar
set CLASSPATH=%CLASSPATH%;C:\mysql\mysql-connector-java-2.0.14\mysql-connector-java-2.0.14-bin.jar
set CLASSPATH=%CLASSPATH%;%PRIDE_HOME%\lib\ojdbc14.jar
set CLASSPATH=%CLASSPATH%;%ANT_HOME%\lib\ant.jar

set PATH=%J2EE_HOME%\bin;%JAVA_HOME%\bin;%ANT_HOME%\bin;D:\Programme\cygwin\bin;%PATH%
title PriDE
