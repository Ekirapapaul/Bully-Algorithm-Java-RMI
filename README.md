# Bully Algorithm : Java RMI
This is an implementation of [Bully Algoritm](https://en.wikipedia.org/wiki/Bully_algorithm) using Java RMI.
The implementation assumes a node to be a server implementation in Java RMI.

## Steps to Run
1. Move to your project directory
```sh
$ cd [path to your directory]
```
2. Compile the source files. Replace destDir with your directory path
```sh
$ javac -d destDir BullyInterface.java Node1.java Node2.java Node3.java Node4.java
```
3. If no error message, Start the rmi registry
```sh
$ start rmiregistry
```
4. Start the nodes. Replace destDir with the path of your Directory
```sh
start java -classpath destDir -Djava.rmi.server.codebase=file:classDir/ bully.algorithm.Node1
start java -classpath destDir -Djava.rmi.server.codebase=file:classDir/ bully.algorithm.Node2
start java -classpath destDir -Djava.rmi.server.codebase=file:classDir/ bully.algorithm.Node3
start java -classpath destDir -Djava.rmi.server.codebase=file:classDir/ bully.algorithm.Node4
```
## Managing Nodes
When a node is initiated it gives some output as well as start a fresh election.
### Terminate a node
When on the node terminal windo press ctrl + C to terminate a node.

Note : Dont close the terminal window of a node by pressing the close window. This will generate errors

## Further Reading 
* [Java RMI Tutorial](https://docs.oracle.com/javase/tutorial/rmi/)
* [Java RMI server client example](http://docs.oracle.com/javase/6/docs/technotes/guides/rmi/hello/hello-world.html#52)