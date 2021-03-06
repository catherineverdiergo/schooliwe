# schooliwe (Numeric schoolbag)

SchoolIWE means school integrated working enviroment.<br>
SchoolIWE is a Java Swing rich application highly customizable and dedicated to easily organize school data (such as timetable, homework agenda, numeric doduments) for pupils using a computer at school.<br>
This application has been particularly thought to help children who have organizational troubles.

## Starting application
Note: default configuration is french locale compliant.<br>
Application is uploaded on gitHub with an eclipse project.<br>
An Ant script (build.xml) is available to generate an install .zip package: schooliwe.zip<br>
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

### Define application topics
Application main tree holds a list of topics which are defined in the rsc/topics.xml file.<br>
The two first topics (timetable and homework agenda) can be kept as permanent topics.<br>
Other topics can be defined regarding to subjects studied by pupil.<br>
Each topic is related with an actionObject (java singeton which manages the topic).<br>
Any new educational topic is always managed by the 'lessonsManager' actionObject.<br>
ActionObjects to be created by application are defined in the rsc/actionobjects.xml file.<br>
For developers, application could be extended defining new topics and action objects.<br>

### Define applications used to store lesson's notes
LessonsManager actionObject allow user to create documents with external applications (such as office writer, office calc, geogebra...etc...).<br>
User favorite applications features should be defined in the  rsc/softwares.xml file.<br>
To create an empty document for a dedicated application, schooliwe uses documentdrivers (see rsc/documentdrivers.xml file).<br>
If you want use a software which is not referenced in the rsc/softwares.xml file, it will be necessary to define a new documentdriver.<br>
The easer way to create a new documentdriver is to use the com.schooliwe.driver.CopyModelDocumentDriver with a realted document model corresponding to an empty software file.
