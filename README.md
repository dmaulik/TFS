TFS
===

HOW TO TEST RIGHT NOW:
1.)Run TFSMaster.java
2.)Go to line 62 on TFSChunkserver.java
3.)Change the Port# into 7501, then run it
4.)Change the Port# into 7502, then run it
5.)Change the Port# into 7503, then run it
6.)Go to line 46 on TFSClient.java
7.)Change the Port# into 7499, then run it
8.)Change the Port# into 7498, then run it
9.)Enter the test #: 1,2,3,1,4,5,6,6,7


Tiny File System Project

Programming language : java
Run with Eclipse IDE

We created individual files for respective test cases such as 
	-Test1.java
	-Test2.java
	-Test3.java
	-Test4.java
	-Test5.java
	-Test6.java 
	-Test7.java 
because the tests are expected to be RUN INDIVIDUALLY. 
We also have Restart.java, Sequence.java, TFSChunkserver.java, TFSMaster.java and TFSClient.java. 
After running the test files, chconfig.csv, config.csv and dirconfig.csv will be populated with log records. 
You can clear all the .csv files by running Restart.java.
Please do so whenever you want to start over.



*****Purpose of chconfig.csv : it stores the mapping from chunkID to chunkLoc and both of them are integer(the first is the ID of the chunk, the second integer is the ID of the chunk location)

*****Purpose of config.csv : it maps the filename to the chunkID

*****Purpose of dirconfig.csv : its the directory name and the chunk location





TESTING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

To make things easier, here is the order in which you can test our file system.
Run (...java) : input

0) Restart.java
1) Test1.java : 7
2) Test2.java : 1\2 and 5
3) Test3.java : 1\2
4) Test1.java : 7
5) Test4.java : src\test123.txt and 1\2\5\File5
6) Test5.java : 1\2\5\File5 and src\test.txt
7) Test6.java : src\img.png and 1\2\5\File3
8) Test6.java : src\img.png and 1\2\5\File3 (same as 7)
9) Test7.java : 1\2\5\File3

You can try to do stuff in different orders. Below are the description for each test.
Keep in mind if you want to start over, please run Restart.java



*****Test1.java :
Create a hierarchical directory structure.  Its input is the number of directories to create and is a value greater than 1.  This test program creates a directory named "1" and two subdirectories underneath it, 2 and 3.  It repeats the process for these subdirectories recursively creating a subdirectory for each leaf directory until it has created the total number of specified directories. 
   
Input:  an integer denoting the number of directories
Note:  When an adversary invokes Test1 twice (or more) in a row, the application should return the meaningful error messages produced by TFS.

Example:  input >> 7
With the input value 7, the resulting directory structure would be
1
1\2
1\3
1\2\4
1\2\5
1\3\6
1\3\7

*******Test2.java :
Create N files in a directory and its subdirectories until the leaf subdirectories.  Each file in a directory is named File1, File2, ..., FileN

Input: >> String Path, integer N
Functionality:  The Path identifies the root directory and its subdirectories that should have X files.  It might be "1\2" in the above example.
N is the number of files to create in path and each of its subdirectories

Note:  When an adversary invokes Test2 twice (or more) in a row, the application should return the meaningful error messages produced by TFS.

Example:  >> 1\2 and 5
Assuming the directory structure from the Test1 example above, this Test would create 5 files in each directory 1\2, 1\2\4 and 1\2\5.  The files in each directory would be named File1, File2, and File3.

*******Test3.java :
Delete a hierarchical directory structure including the files in those directories.
Input: String Path 
Functionality:  The input path identifies the directory whose content along with itself must be deleted. 
Note:  When an adversary invokes Test3 twice (or more) in a row, the application should return the meaningful error messages produced by TFS.

Example:  >> 1\2
Assuming the directory sturcture from Test2 above, this test would delete 3 directories and 9 files.
The deleted directories are 1\2, 1\2\4 and 1\2\5.  The fires deleted are:
1\2
1\2\File1
1\2\File2
1\2\File3
1\2\File4
1\2\File5
1\4
1\2\4\File1
1\2\4\File2
1\2\4\File3
1\2\4\File4
1\2\4\File5
1\5
1\2\5\File1
1\2\5\File2
1\2\5\File3
1\2\5\File4
1\2\5\File5

*******Test4.java :
Store a file on the local machine in a target TFS file specified by its path. 
Input: String localFilePath, String TFSFile

Functionality:  If the TFS file exists then return an error message.  Otherwise, create the TFS file, read the conent of the local file and store it in the TFS File.

Example:  src\test123.txt and 1\2\5\File5


*******Test5.java :
Read the content of a TFS file and store it on the specified file on the local machine.

Input: String TFSFile, String localFilePath
Functionality:  If the TFS file does not exist then return an error message.  Otherwise, open the TFS file, read the content of the file, write the content of the file to the local filesystem file.

Example:  >> 1\2\5\File5 and src\test.txt

*******Test6.java :
Append the size and content of a file stored on the local machine in a target TFS file specified by its path.

Input: String localFilePath,String TFSFile
Functionality:  If the TFS file does not exists then create the specified file.  Read the content of the local file, compute the number of bytes read, seek to the end of the TFS file, append a 4 byte integer pertaining to the image size, append the content in memory after these four bytes, and close the TFS file.

Example:  src\img.png and 1\2\5\File3

*******Test7.java :
Count the number of logical files stored in a TFS file using Test6 and printout the results.

Input:  String TFSFilePath generated using Test6
Functionality:  If the input TFS file does not exist then return an error.  Otherwise, counts the number of logical files stored in a TFS file (generated using Test6) by reading the size and payload pairs in the specified file name.

Example:  1\2\5\File3

Assumption:  Input file, 1/File1.haystack, is generated using Test6.

*******Purpose of App.java : it has the main function that allows us to run the program and function to make directories

*******Purpose of Sequence.java : it has a function that increments the counter for atomic integer

*******Purpose of TFSChunkserver.java : it has functions that create folder, create files, write into files, read from files remove chunks and get file name 

*******Purpose of TFSMaster.java : it has functions that populate files, populate folders, populate chunkID->chunkLoc mapping, allocate folder, allocate file, allocate chunk, get location of uuid, get folder location, check if file exists, check if folder exists, delete directory, delete file  

*******Purpose of TFSClient.java : it has functions that create files, create directories, delete directories, write into files, write into chunks, count the number of chunks, append to the file, check if the file exists, check if the folder exists, read bytes into files

