# schooliwe (Numeric schoolbag)

SchoolIWE means school integrated working enviroment.
SchoolIWE is a Java Swing rich application highly customizable and dedicated to easily organize school data (such as timetable, homework agenda, numeric doduments) for pupils using a computer at school.
This application has been particularly thought to help children who have organizational troubles.

## Starting application
Note: default configuration is french locale compliant.
Application is uploaded on gitHub with an eclipse project.
An Ant script (build.xml) is available to generate an install .zip package: schooliwe.zip
To install application, process as follow:
* generate schooliwe.zip;
* unzip previous archive in a directory on your disk;
* start application running start.sh or start.bat script depending on which OS you use.

## Customize application
### Change locale
* conf/messages.properties file should be translated in the target locale
* In the rsc/homework.xml file, homework tag's "label" attribute should be translated in target locale
* In the rsc/softwares.xml file, application tag's "labelType" attribute should be translated in target locale
* In the rsc/timetable.xml file, day tag's "name" attribute should be translated in target locale
* In the rsc/topics.xml file, topic tag's "name" and "timetableLabel" attributes should be translated in target locale
* In the rsc/vacation.xml file, vacation tag's "label" attribute should be translated in target locale
* In the start.sh and start.bat files, parameter locale should be customized
