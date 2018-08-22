# Simple_File_Transfer_Protocol
RFC 913 - Simple File Transfer Protocol

# TO DO:
- MXT message
- Fix directory path for RETR and STOR commands
- Make Server and Clinet able to read \n characters
- Comment code
- Tets
- Write readme with test process

# Starting the code
- Open two command windows
- In one of them anvigate to _server.jar_
- Run the server by typing: ....
- You should get back: _Server started_
- On the other command window navigate to _Client.jar_
- Run the client by typing: ....
- You should see: 
  - __+MIT-XX SFTP Service__
  - __Enter command:__

- In case you run client before you run server the following message will apear:
  - __-MIT-XX Out to Lunch__
  - __Error: java.net.ConnectException: Connection refused: connect__
- and the connection will close. So __make sure your server is running before you start the client__

# Testing

- Most of the testing is done through the client. So assume that all of the commands listed in the tests are entered on the cliend command window
- Client will require you to enter the command you wish to send to the server.
- The response will be printed back on the screen
- The following test cases use:
  - __Enter Command:__ This is when you enter the command you wish to send to the server
  - __From Server__ This indicates what came back from the server
- Anything that is in brackets and italic act as a comment, do not type that as the part of the command

# General Commands
- On the client side the user is required to enter the commands.
- The command must be 4 characters long, otherwise the server will return an error.

### Wrong Command
- Enter command: __COMMAND__ _(Or anything that is not a standard command)_
- From server: __-invalid command__
- Enter Command: __USR__ _(Or anything that is shorter than 4 characters)_
- From Server: __-invalid command__
- Enter Command: __USER__ _(Note that there is no space after you type USER)_
- From Server: __-invalid command__
- Enter Command: __DONE__
- From Server: __+Thank you for choosing SFTP 1984__

# User Credentials
- Please ensure that on the server side there is a directory called '_admin_'.
- Inside that directory there should be a text file _users.txt_ which contains all the usernames, accounts and passwords.
- Each line represents the user details where each info is separated by '__|__' sign as follows:
  - USER | ACCT | PASS
- For example: 
  - __umih874|888-888|hello__ _where umih874 is username, 888-888 is an account and hello is password._
- In some cases the user does not need a password or account in which case those fields are indicated by SPACE.
- For example: 
  - __root| | .__
  
## TEST CASES:
- Each user must have a username, it is not possible to login into a system without one.
- If you examine the user.txt you can see that there are 4 types of users:
  - No account nor password required (i.e. username: root)
  - No account required (i.e. username: admin)
  - No password required (i.e. username: user123)
  - Must provide username, password and account (i.e. username: umih874, or blah634)

- __Before starting the next set of tests please ensure you have started the server and client__
- __Note that you need to start the server and client every-time you are performing the test__ as the new user cannot login until the connection is closed.
- __Refer to "Starting the code" section for more detailed description of this step__

#### Making testing easier: if you do not wish to close the connection for each test you can simply use __SKIP__ command.
- __NOTE that this is not a standard command, it is only used to make testing easier__
- The user should always close the connection before new user can log-in.

### SKIP command
#### Typically
- Enter Command: __USER root__ _(Typically you specify the user first)_
- From Server: __!root logged in__ _(Indicates the user is logged-in)_
- Enter Command: __USER admin__ _(If you try to signin with a different account server will not allow you to do so)_
- From Server: __-root is currently signed in__ 
- Enter Command: __DONE__ _(so you have to close the connection and then start the server and client again)_.
- From Server: __+Thank you for choosing SFTP 1984__

#### To make testing easier use SKIP command
_NOTE: this is purelly for testing purposes, user should close the connection when they want to sign in with a different account_
- Enter Command: __USER root__ _(specify the user first)_
- From Server: __!root logged in__ _(Indicates the user is logged-in)_
- Enter Command: __USER admin__ _(If you try to sign-ng with a different account, server will not allow you to do so)_
- From Server: __-root is currently signed in__ 
- Enter Command: __SKIP__ _(so you can skip the conenction closing process)_.
- From Server: __+Skip having to close connection. User cleared__
- Enter Command: __USER admin__ _(Now you can use a different username)_
- From Server: __+User-id valid, send password__ 

#### The following tests all instruct to use DONE command, but if you wish to do so, in place of DONE use SKIP and just go to the next test instructions without restarting your server or client
#### In case you wish to abort testing and do a different test, you can jsut use SKIP command again. SKIP command just clears the fields used to store the currently verified username, password and account

### No account and password
- Enter Command: __USER root__
- From Server: __!root logged in__
- Enter Command: __DONE__ _(to close the connection or use SKIP)_.
- From Server: __+Thank you for choosing SFTP 1984__

### No account needed
- Enter Command: __USER admin__
- From Server: __+User-id valid, send password__
- Enter Command: __PASS a123__
- From Server: __! Logged in__
- Enter Command: __DONE__ _(or use SKIP)_
- From Server: __+Thank you for choosing SFTP 1984__

### No password needed
- Enter Command: __USER user123__
- From Server: __+User-id valid, send account__
- Enter Command: __ACCT 123-456__
- From Server: __! Account valid, logged-in__
- Enter Command: __DONE__ _(or SKIP)_
- From Server: __+Thank you for choosing SFTP 1984__

### All credentials needed
- For this test there are two scenarios:
#### Password first
- Enter Command: __USER umih874__
- From Server: __+User-id valid, send account and password__
- Enter Command: __PASS hello__
- From Server: __+Send Account__
- Enter Command: __ACCT 888-888__
- From Server: __! Account valid, logged-in__
- Enter Command: __DONE__
- From Server: __+Thank you for choosing SFTP 1984__
#### Account first
- Enter Command: __USER umih874__
- From Server: __+User-id valid, send account and password__
- Enter Command: __ACCT 888-888__
- From Server: __+Account valid, send password__
- Enter Command: __PASS hello__
- From Server: __! Logged in__
- Command: __DONE__
- From Server: __+Thank you for choosing SFTP 1984__

### Wrong credentials
#### Secenario #1:
- Enter Command: __USER user__ _(Or anything that is not listed in users.txt)_
- From Server: __-Invalid user-id, try again__
- Enter Command: __USER umih874__ _(Provide correct username)_
- From Server: __+User-id valid, send account and password__
- Enter Command: __PASS bla__ _(Provide wrong password)_
- From Server: __-Wrong password, try again__
- Enter Command: __PASS hello__ _(Provide correct password)_
- From Server: __+Send Account__
- Enter Commnad: __ACCT 456-789__ _(Provide wrong account)_
- From Server: __-Invalid account, try again__
- Enter Command: __ACCT 888-888__ _(Provide correct account)_
- From Server: __! Account valid, logged-in__
- Enter Command: __DONE__ _(or SKIP)_
- From Server: __+Thank you for choosing SFTP 1984__
#### Secenario #2:
- Enter Command: __USER user__ _(Or anything that is not listed in users.txt)_
- From Server: __-Invalid user-id, try again__
- Enter Command: __USER umih874__ _(Provide correct username)_
- From Server: __+User-id valid, send account and password__
- Enter Command: __PASS bla__ _(Provide wrong password)_
- From Server: __-Wrong password, try again__
- Enter Commnad: __ACCT 456-789__ _(Provide wrong account)_
- From Server: __-Invalid account, try again__
- Enter Command: __ACCT 888-888__ _(Provide correct account)_
- From Server: __+Account valid, send password__
- Enter Command: __PASS hello__ _(Provide correct password)_
- From Server: __! Logged in__
- Enter Command: __DONE__ _(or SKIP)_
- From Server: __+Thank you for choosing SFTP 1984__

### User already logged in
- Enter Command: __USER root__
- From Server: __!root logged in__
- Enter Command: __USER admin__
- From Server: __-root is currently signed in__
- Enter Command: __DONE__ _(or SKIP)_
- From Server: __+Thank you for choosing SFTP 1984__

### Change user
You can change the user at any moment until the user is logged in.
- Enter Command: __USER umih874__ _(Specify 1st user)_
- From Server: __+User-id valid, send account and password__
- Enter Command: __PASS hello__ _(Provide password for that user and wait for account)_
- From Server: __+Send Account__ _(Expecting account next)_
- Enter Command: __USER admin__ _(But we change user to admin)_
- From Server: __+User-id valid, send password__ _(Password for umih874 is cleared, so now we are waiting on the admin password)_
- Enter Command: __PASS a123__ _(Provide the password for admin)_
- From Server: __! Logged in__
- Enter Command: __USER umih874__ _(Provide new username)_
- From Server: __-admin is currently signed in__ _(Now umih874 cannot log-in anymore)_
- Enter Command: __DONE__
- From Server: __+Thank you for choosing SFTP 1984__

# NOTE
- For the following tests the user must be looged in.
- If you try to use any of these command without being logged in you will get a bad response.
- Exmple:
  - Enter Command: __TYPE A__
  - From Server: __-Access denied, please login__
- I recommed just logging in as root:
  - Enter Command: __USER root__
  - From Server: __!root logged in__
  
- __Now continue the following test. Do not close conenction after you finish the test just move on to the next one__

# TYPE 
- Changes the way the files are sent between the server and client.
- Valid commands and types:
  - A = Ascii
  - B = Binary
  - C = Continuous
  
### Change to Ascii
- Enter Command: __TYPE A__ 
- From Server: __+Using ASCII mode__

### Change to Binary
- Enter Command: __TYPE B__ 
- From Server: __+Using BINARY mode__

### Change to Continuous
- Enter Command: __TYPE C__ 
- From Server: __+Using CONTINUOUS mode__

# LIST
- Lists all the files from the specified directory
- If no directory is specified used the current working directory of the server.
- Two modes:
  - Standard: __F__ = lists just the names of the files
  - Verbose: __V__ = lists all the details of the files. It shows:
    - Size, Protection, Last Modification date (in days).

### Standard List NO specified path
- _You should recieve the path where your Server.jar is being stored at the moment. In this example my Server.jar is stored in "C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server"_
- _You should be able to see the same listed files, unless you were testing __KILL__ or __NAME__ prior to this_
- _You can open the directory in which you placed Server.jar and observe that the listed files are the same as the ones listed in this example._
- ___NOTE this does not list directories, only files__.
- Enter Command: __LIST R__ 
- From Server: __+C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server__
  - __bob.jpg
  - franky.jpg
  - franky.zip
  - Lecture 4.ppt
  - one.jpg
  - server.iml
  - test_1.txt
  - test_2.txt
  - test_3.txt
  - test_4.txt__

### Standard List with specified path
- _You can specify any path you want. For this example I used: "C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\cs705"_
- _But please ensure it is a full path_
- Enter Command: __LIST R C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\cs705__ 
- From Server: __+C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\cs705__
  - __Lecture 4.ppt
  - lecture1-2.ppt
  - lecture1.ppt__
  
### Verbose List NO specified path
- Enter Command: __LIST V__ 
- From Server: __+C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server__
  - __bob.jpg: Size: 7967; Protection: can Execute,can Read,can Write; Last modified 17764 days ago; 
  - __franky.jpg: Size: 0; Protection: can Execute,can Read,can Write; Last modified 17763 days ago; 
  - franky.zip: Size: 30805; Protection: can Execute,can Read,can Write; Last modified 17763 days ago; 
  - Lecture 4.ppt: Size: 402944; Protection: can Execute,can Read,can Write; Last modified 17757 days ago; 
  - one.jpg: Size: 60438; Protection: can Execute,can Read,can Write; Last modified 17764 days ago; 
  - server.iml: Size: 423; Protection: can Execute,can Read,can Write; Last modified 17743 days ago; 
  - test_1.txt: Size: 0; Protection: can Execute,can Read,can Write; Last modified 17765 days ago; 
  - test_2.txt: Size: 0; Protection: can Execute,can Read,can Write; Last modified 17754 days ago; 
  - test_3.txt: Size: 0; Protection: can Execute,can Read,can Write; Last modified 17754 days ago; 
  - test_4.txt: Size: 82; Protection: can Execute,can Read,can Write; Last modified 17764 days ago;__

### Verbose List NO specified path
- _You can specify any path you want. For this example I used: "C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\cs705"_
- _But please ensure it is a full path_
- Enter Command: __LIST V C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\cs705__ 
- From Server: __+C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server__
  - Lecture 4.ppt: Size: 402944; Protection: can Execute,can Read,can Write; Last modified 17754 days ago; 
  - lecture1-2.ppt: Size: 857600; Protection: can Execute,can Read,can Write; Last modified 17754 days ago; 
  - lecture1.ppt: Size: 4320256; Protection: can Execute,can Read,can Write; Last modified 17754 days ago; 

### Invalid path specified
- _You can specify any invalid path. In this example I used: "C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\noDirectory"_
- _You can either R or V the outcome would be identical_
- Enter command: __LIST R C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\noDirectory__
- From server: __-Directory does not exist__

# CDIR
- Change the current working directory to a specified directory
- Requires the password and account.
- In case the logged in user does not need those then it just changes the working directory.

## NOTE
- Before you start these tests eaither reset the connection or use SKIP command to logout user.
- This command is different for each user so we need to test for multiple accounts

- _You can use any directory you want. For these examples I used: "C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\myDir"_
- _Please make sure you specify the full path_

### No password or account required
- Enter Command: __USER root__  _(log-in as root)_
- From Server: __!root logged in__
- Enter command: __CDIR C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\myDir__
- From server: __!Changed working dir to C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\myDir__
- Command: __DONE__ _(or SKIP)_
- From Server: __+Thank you for choosing SFTP 1984__

### No password required
- Enter Command: __USER admin__
- From Server: __+User-id valid, send password__
- Enter Command: __PASS a123__
- From Server: __! Logged in__
- Enter command: __CDIR C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\myDir__
- From Server: __+directory ok, send account/password__
- Enter Command: __PASS a123__
- From server: __!Changed working dir to C:\Users\Uros\Documents\#Projects\Simple_File_Transfer_Protocol\server\myDir__
- Command: __DONE__
- From Server: __+Thank you for choosing SFTP 1984__

# KILL
- Deletes the specifile file
- File must be in the current working directory
- I recommed just logging in as root first:
  - Enter Command: __USER root__
  - From Server: __!root logged in__
  
### Delete file
- _Before you use KILL command got to the Server folder and make sure that __test_4.txt__ is there_
- _Alternatively you can use LIST R command and see that __test_4.txt__ is listed_
- Enter Command: __KILL test_4.txt__
- From Server: __+test_4.txt deleted__
- _you can go and checkout the server side (i.e. folder where Server.jar is placed) and see that __test_4.txt__ is not there anymore_
- _you can also use LIST R and see that __test_4.txt__ is not listed anymore_
- _Or you can try and delete it again and server will inform you that it is not there anymore:_
- Enter Command: __KILL test_4.txt__
- From Server: __-Not deleted because: the file you specified does not exists __

# NAME
- Renames the specified file. First checks if the file exists and then allows user to rename it.
- If you are not already logged in then login as root:
  - Enter Command: __USER root__
  - From Server: __!root logged in__
  
### File Exists
- _Make sure that Server side has the file name __test_3.txt___
- Enter Command: __NAME test_3.txt__
- From Server: __+File exists__
- Enter Command: __TOBE test_4.txt__ _(Rename the file to test_4.txt)
- From Server: __+test_3.txt renamed to test_4.txt__
- _You can go to Server folder and see that there is a file named __test_4.txt__
- _Or you can use LISR R and you will be able to see __test_4.txt__ being listed_
- _Also you can try and rename that file again and server will tell that it does not exist anymore_
- Enter Command: __NAME test_3.txt__
- From Server: __-Can't find test_3.txt__

# RETR
- Server send the file to the Client.
- Client will store that file in its local directory.
- Sends the file based on the type:
  - Ascii files
  - Binary files 
- Continuous and Binary mode are very much the same.
- If you are not already logged in then login as root:
  - Enter Command: __USER root__
  - From Server: __!root logged in__
  
### Send Ascii
- _Make sure you are using Ascii type:_
- Enter Command: __TYPE A__ 
- From Server: __+Using ASCII mode__
- Enter Command: __RETR bob.jpg__ _(You can try and specify the binary file, but server will not send it to you)_
- From Server: __-Can't send BINARY file as ASCII__
- Enter Command: __RETR test_2.txt__ _(Now You can specify ascii file)_
- From Server: __96__
- Enter Command: __SEND__
- From Server: Some text to be sent _(Server will printout the text you sent)_
  - This is more text
  - And this here is also some text
  - Here is some text
  - Bye
-  _You can now see the __test_2.txt__ on your clint side (i.e. folder where your Client.jar is)_
- _The file content should be the same on both sides (i.e. server and client side)_

### Send Binar
- _Make sure you are using Binary type:_
- Enter Command: __TYPE B__ 
- From Server: __+Using BINARY mode__
- Enter Command: __RETR test_2.txt__ _(You can try and specify the ascii file, but server will not send it to you)_
- From Server: __-Can't send ASCII file as BINARY__
- Enter Command: __RETR hello.jpg__ _(Now You can try and specify non existent file)_
- From Server: __-File doesn't exists__
- Enter Command: __RETR bob.jpg__
- From Server: __7967__
- Enter Command: __SEND__
- From Server: __read 7967 bytes.__
-  _You can now see the __bob.jpg__ on your clint side (i.e. folder where your Client.jar is)_
- _The file content should be the same on both sides (i.e. server and client side)_

### Send Continuous
- _Very identical to Continuous
- _Make sure you are using Continuous type:_
- Enter Command: __TYPE C__ 
- From Server: __+Using CONTINUOUS mode__
- Enter Command: __RETR test_2.txt__ _(You can try and specify the ascii file, but server will not send it to you)_
- From Server: __-Can't send ASCII file as CONTINUOUS
- Enter Command: __RETR one.jpg__
- From Server: __60438__
- Enter Command: __SEND__
- From Server: __read 60438 bytes.__
-  _You can now see the __one.jpg__ on your clint side (i.e. folder where your Client.jar is)_
- _The file content should be the same on both sides (i.e. server and client side)_

### STOP
- _Work for anymode_
- _For this example I am using Ascii, but you can use any mode_
- Enter Command: __TYPE A__ 
- From Server: __+Using ASCII mode__
- Enter Command: __RETR test_1.txt__
- From Server: __88__
- Enter Command: __STOP__
- From Server: __+ok, RETR aborted__
- _The RETR is now aborted_
- _You can try and use SEND but server will ask you to specify file first_
- Enter Command: __SEND__
- From Server: __-Please specify the file first__
- _Now you have to specify the file again using RETR_

# STOR
- Send the specified file from the Client to the Server.
- Has 3 modes:
  - NEW - creates a new generation of the file if specified file exists or creates a new one if file does not exist.
  - OLD - overwrites the file or creates a new one if pecified file does not exist.
  - APP - Adds to the existing file or creates a new one
- Can be used for all 3 modes:
  - Ascii
  - Binary
  - Continuous
- For this example please ensure that client side has files:
  - franky.jpg
  - hello.txt
- If you are not already logged in then login as root:
  - Enter Command: __USER root__
  - From Server: __!root logged in__

## Ascii NEW
- _Work for anymode_
- _For this example I am using Ascii, but you can use any mode_
- Enter Command: __TYPE A__ 
- From Server: __+Using ASCII mode__
- Enter Command: __RETR test_1.txt__
- From Server: __88__
- Enter Command: __STOP__
- From Server: __+ok, RETR aborted__
- _The RETR is now aborted_
- _You can try and use SEND but server will ask you to specify file first_
- Enter Command: __SEND__
- From Server: __-Please specify the file first__
- _Now you have to specify the file again using RETR_
