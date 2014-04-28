TFS
===
Tiny File System Project
Programming language : java
Run with Eclipse IDE

HOW TO TEST:
1.)Run TFSMaster.java
2.)Run Chunkserver1.java
3.)Run Chunkserver2.java
4.)Run Chunkserver3.java
5.)Run Client1.java
6.)Run Client2.java

*All IP Address are hardcoded right now.
*If you want to run everything on one server, change everything to "localhost" instead of the IP address
*The Client will ask user for input (which test to run) and the parameters as well.
*There is only test 1-7. If you want to try test 8, you have to use multiple computers and run test 6 at the same time.


We also have Restart.java, Sequence.java, TFSChunkserver.java, TFSMaster.java and TFSClient.java. 
After running the test files, chconfig.csv, config.csv and dirconfig.csv will be populated with log records. 
You can clear all the .csv files by running Restart.java.
Please do so whenever you want to start over.