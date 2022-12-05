# COMP 490 Project 2

## Contributions
- Amir Roochi: Testing and debugging, some utility methods, and dispalying
- Nancy Phung: Documentation of classes, refactoring, and initial testing
- Jarod Wilson: Server communication and handling of routing table/nodes
- Farid Koushaneh: User input handling, helper methods, and refactoring
For more details visit the github at: https://github.com/Jwil200/429project2

## Running
To run the project use the launch.bat file (will automatically use an unused topology) or run with the following commands inside of src:
- javac -d ../bin distance_vector_routing.java
- java -cp ../bin distance_vector_routing
You will then be prompted for a port and interval to run on, after inputting those the program will begin. For the topology file, use the path ../topology/file_name, as the program's initial file path will be in src.