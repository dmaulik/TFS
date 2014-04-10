TFS
===

Tiny File System Project

Programming language : java
Run with Eclipse IDE

We basically created individual files for respective test cases such as Test1.java, Test2.java, Test3.java, Test4.java, Test5.java, Test6.java and Test7.java so that the tests can be run individually. We also have App.java, Sequence.java, TFSChunkserver.java, TFSMaster.java and TFSClient.java. 

**Purpose of Test1.java :
Create a hierarchical directory structure.  Its input is the number of directories to create and is a value greater than 1.  This test program creates a directory named "1" and two subdirectories underneath it, 2 and 3.  It repeats the process for these subdirectories recursively creating a subdirectory for each leaf directory until it has created the total number of specified directories. 
   
Input:  an integer denoting the number of directories

Note:  When an adversary invokes Test1 twice (or more) in a row, the application should return the meaningful error messages produced by TFS.

Example:  Test1 7
With the input value 7, the resulting directory structure would be
1
1\2
1\3
1\2\4
1\2\5
1\3\6
1\3\7

**Purpose of Test2.java :
Create N files in a directory and its subdirectories until the leaf subdirectories.  Each file in a directory is named File1, File2, ..., FileN

Input:  Path, N
Functionality:  The Path identifies the root directory and its subdirectories that should have X files.  It might be "1\2" in the above example.
N is the number of files to create in path and each of its subdirectories

Note:  When an adversary invokes Test2 twice (or more) in a row, the application should return the meaningful error messages produced by TFS.

Example:  Test2 1\2 3
Assuming the directory structure from the Test1 example above, this Test would create 5 files in each directory 1\2, 1\2\4 and 1\2\5.  The files in each directory would be named File1, File2, and File3.

**Purpose of Test3.java :
Delete a hierarchical directory structure including the files in those directories.
Input:  Path 
Functionality:  The input path identifies the directory whose content along with itself must be deleted. 

Note:  When an adversary invokes Test3 twice (or more) in a row, the application should return the meaningful error messages produced by TFS.

Example:  Test3 1\2
Assuming the directory sturcture from Test2 above, this test would delete 3 directories and 9 files.
The deleted directories are 1\2, 1\2\4 and 1\2\5.  The fires deleted are:
1\2\File1
1\2\File2
1\2\File3
1\2\4\File1
1\2\4\File2
1\2\4\File3
1\2\5\File1
1\2\5\File2
1\2\5\File3

**Purpose of Test4.java :
Store a file on the local machine in a target TFS file specified by its path. 

Input:  local file path, TFS file

Functionality:  If the TFS file exists then reutrn an error message.  Otherwise, create the TFS file, read the conent of the local file and store it in the TFS File.

Example:  Test4 C:\MyDocuments\Image.png 1\File1.png
If 1\File1.png exists then reutrn error.  Otherwise, create 1/File1.png, read the content of C:\MyDocument\Image.png, write the retrieved content into 1\File1.png

**Purpose of Test5.java :
 Read the content of a TFS file and store it on the specified file on the local machine.
Input:  TFS file, local file path

Functionality:  If the TFS file does not exist then return an error message.  Otherwise, open the TFS file, read the content of the file, write the content of the file to the local filesystem file.

Example:  Test5 1\File1.png C:\MyDocument\Pic.png
If either 1\File1.png does not exist or C:\MyDocument\Pic.png exists then return the appropriate error message.  Otherwise, open the TFS file 1\File1.png and read its content into memory.  Create and open C:\MyDocument\Pic.png, write the retrieved content into it, and close this file.

**Purpose of Test6.java :
Append the size and content of a file stored on the local machine in a target TFS file specified by its path.
Input: local file path, TFS file

Functionality:  If the TFS file does not exists then create the specified file.  Read the content of the local file, compute the number of bytes read, seek to the end of the TFS file, append a 4 byte integer pertaining to the image size, append the content in memory after these four bytes, and close the TFS file.

Example:  Test6 C:\MyDocuments\Image.png 1\File1.png
If 1\File1.png exists then create the TFS file 1\File1.png.  Read the content of C:\MyDocument\Image.png into an array of bytes named B and perform the following steps: 1) Let s denote the number of bytes retrieved, i.e., size of B, 2) Let delta=0, 3) Seek to offset delta of 1\File1.png, 4) Try to read 4 bytes, 5)  If EOF then append 4 bytes corresponding to s followed with the array of bytes B, otherwise interpret the four bytes as the integer k and set delta=delta+4+k and goto Step 3.  (The objective of the iteration is to reach the EOF.)

**Purpose of Test7.java :
Count the number of logical files stored in a TFS file using Test6 and printout the results.

Input:  A TFS file generated using Test6

Functionality:  If the input TFS file does not exist then return an error.  Otherwise, counts the number of logical files stored in a TFS file (generated using Test6) by reading the size and payload pairs in the specified file name.

Example:  Test7 1/File1.haystack

Assumption:  Input file, 1/File1.haystack, is generated using Test6.

**Purpose of App.java : it has the main function that allows us to run the program and function to make directories

**Purpose of Sequence.java : it has a function that increments the counter for atomic integer

**Purpose of TFSChunkserver.java : it has functions that create folder, create files, write into files, read from files remove chunks and get file name 

**Purpose of TFSMaster.java : it has functions that populate files, populate folders, populate chunkID->chunkLoc mapping, allocate folder, allocate file, allocate chunk, get location of uuid, get folder location, check if file exists, check if folder exists, delete directory, delete file  

**Purpose of TFSClient.java : it has functions that create files, create directories, delete directories, write into files, write into chunks, count the number of chunks, append to the file, check if the file exists, check if the folder exists, read bytes into files

