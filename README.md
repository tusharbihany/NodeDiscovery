# Key Value Distributed Data Store
Pre-requisites:

1. JDK/JRE - 1.8
2. Maven 3.0+
3. Node Discoverer Up and Running

Steps :
1. Pull the code
2. mvn clean install
3. java -jar target/DataStore-1.0-SNAPSHOT.jar server configuration.yml (Spawns on port 25320)
4. java -jar target/DataStore-1.0-SNAPSHOT.jar server configuration1.yml (Spawns on port 25330)
5. java -jar target/DataStore-1.0-SNAPSHOT.jar server configuration2.yml (Spawns on port 25340)