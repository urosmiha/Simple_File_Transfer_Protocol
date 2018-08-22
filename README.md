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
- You should get back: 
  - _+MIT-XX SFTP Service_
  - _Enter command:_

- In case you run client before you run server the following message will apear:
  - _-MIT-XX Out to Lunch_
  - _Error: java.net.ConnectException: Connection refused: connect_
- and the connection will close. So __make sure your server is running before you start the client__

# Testing

- Most of the testing is done through the client.
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
- Enter Command: __USR__ _(Or anything that is shorter than4 characters)_
- From Server: __-invalid command__
- Enter Command: __USER__ _(Note that there is no space after you type USER)_
- From Server: __-invalid command__
- Enter Command: __DONE__
- From Server: __+Bye__

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
- __Refer to section 1 for more detailed description of this step__

### No account and password Test
- Enter Command: _USER root_.
- From Server: _!root logged in_
- Enter Command: _DONE_ to close the connection.
- From Server: _Bye_

### No account needed
- Enter Command: _USER admin_
- From Server: _+User ok, send password_
- Enter Command: _PASS a123_
- From Server: _+ logged in_
- Enter Command:: _DONE_
- From Server: _Bye_

### No password needed
- Enter Command: _USER user123_
- From Server:
- Enter Command: _ACCT 123-456_
- From Server:
- Enter Command: _DONE_
- From Server: _Bye_

### All credentials needed
- For this test there are two scenarios:
#### Password first
- Enter Command: _USER umih874_
- From Server:
- Enter Command: _PASS hello_
- From Server: 
- Enter Command: _ACCT 888-888_
- From Server:
- Enter Command: _DONE_
- From Server: _Bye_
#### Account first
- Enter Command: _USER umih874_
- From Server:
- Enter Command: _ACCT 888-888_
- From Server:
- Enter Command: _PASS hello_
- From Server: 
- Command: _DONE_
- From Server: _Bye_

### Wrong credentials
#### Secenario #1:
- Enter Command: _USER user_ (Or anything that is not listed in users.txt)
- From Server: 
- Enter Command: _USER umih874_ (Provide correct username)
- From Server:
- Enter Command: _PASS bla_ (Provide wrong password)
- From Server: 
- Enter Command: _PASS hello_ (Provide correct password)
- From Server:
- Enter Commnad: _ACCT 456-789_ (Provide wrong account)
- From Server:
- Enter Command: _ACCT 888-888_ (Provide correct account
- From Server:
- Enter Command: _DONE_
- From Server: _Bye_
#### Secenario #2:
- Enter Command: _USER user_ (Or anything that is not listed in users.txt)
- From Server: 
- Enter Command: _USER umih874_ (Provide correct username)
- From Server:
- Enter Command: _PASS bla_ (Provide wrong password)
- From Server:
- Enter Commnad: _ACCT 456-789_ (Provide wrong account)
- From Server:
- Enter Command: _ACCT 888-888_ (Provide correct account)
- From Server:
- Enter Command: _PASS hello_ (Provide correct password)
- From Server:
- Enter Command: _DONE_
- From Server: _Bye_

### User already logged in
- Enter Command: _USER root_
- From Server:
- Enter Command: _USER admin_
- From Server:
- Enter Command: _DONE_
- From Server: _Bye_

### Change user
You can change the user at any moment until user is logged in.For example:
- Enter Command: _USER umih874_ (Specify 1st user)
- From Server:
- Enter Command: _PASS hello_ (Provide password for that user)
- From Server: 
- Enter Command: _USER admin_ (Change user to admin)
- From Server: 
- Enter Command: _PASS 1_ (Provide the password for admin)
- From Server:
- Enter Command: _USER umih874_ (Provide new username)
- From Server: 
- Enter Command: _DONE_
- From Server: _Bye_

# NAME
Renames the specified file. First checks if the file exists and then allows user to rename it.
### File Exists

### File Does Not Exist

### 
