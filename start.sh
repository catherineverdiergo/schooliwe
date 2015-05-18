#!/bin/sh

CLASSPATH=.:./schooliwe.jar:./lib/apache-log4j-extras-1.1.jar:./lib/commons-configuration-1.9.jar:./lib/commons-lang-2.6.jar:./lib/commons-logging-1.1.1.jar:./lib/jcalendar-1.4.jar:./lib/jgoodies-common-1.2.0.jar:./lib/jgoodies-looks-2.4.1.jar:./lib/log4j-1.2.13.jar:./lib/org.xmind.core_3.3.0.201208102038.jar:./lib/poi-3.8-20120326.jar:./lib/poi-3.8-20120326.jar:./lib/poi-scratchpad-3.9-20121203.jar

java -cp $CLASSPATH -splash:"images/hp-schoolbag-icon.png" -Duser.language=fr com.schooliwe.tests.SchoolIWE&
