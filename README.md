#Readme for mic3 demo project as per BRIEF file

##Requirements
JVM 1.7 or higher
MAVEN 3.0.0 or higher
MongoServer 2.4.x and above
GIT client

##Optional
ActiveMQ 5.10.0 or higher

##Preperation
git clone https://github.com/rafelbev/netty-collector
cd netty-collector
mvn clean install

##Run demo embedded version
mvn -pl demo-embedded exec:java 

##Run demo projection version
###Prepare production version
* Install AciveMQ on a redundant cluster of servers
* Prepare 2 (min) machines to act as HTTP collectors
* Prepare 2 (min) machines to act as UDP collectors
* Prepare 2 (min) machines to act as engine-receivers
* Prepare 2 (min) machines to act as a MongoServer using Replica Sets

###Run production version
####HTTP collector
mvn -pl collector-http exec:java
####UDP collector
mvn -pl collector-udp exec:java
####Engine Receiver
mvn -pl engine-receiver exec:java