# Simple_File_Transfer_Protocol
RFC 913 - Simple File Transfer Protocol

# User Credentials
- Please ensure that on the server side there is a directory called '_admin_'.
- Inside that directory there shuld be a text file _users.txt_ which contains all the usernames, accounts and passwords.
- Each line represents the user details where each detail is separated by '__|__' sign as follows:
  - USER | ACCT | PASS
- For example: __umih874|888-888|hello__ where umih874 is username, 888-888 is an account and hello is password.
- In some cases the user does not need a password or account in which case those fields are indicated by SPACE.
- For example: __root| | .__
- Command
## USER
- Each user must have a username, it is not possible to login into a system without one.
- If you examine the user.txt you can see that there are 4 types of users:
  - No account nor password required (i.e. username: root)
  - No account required (i.e. username: admin)
  - No password required (i.e. username: user123)
  - Must provide username, password and account (i.e. username: umih874, or blah634)

- __Before starting the next set of tests please ensure you have started the server and client__
- __Note that you need to start the server and client everytime you are performing the test__ as the new user cannot login until the connection is closed.
- __Refere to section 1 for more detailed description of this step__

### No account and password Test
- Command: _USER root_.
- Server: _!root logged in_
- Command: _DONE_ to close the connection.
- Server: _Bye_

### No account needed
- Type _USER admin_
- Back: _+User ok, send password_
- Type _PASS a123_
- Back: _+ logged in_
- Type: _DONE_
- Back: _Bye_

### No password needed
- Command: _USER user123_
- Server:
- Command: _ACCT 123-456_
- Server:
- Command: _DONE_
- Server: _Bye_

### All credentials needed
- For this test there are two scenarios:
#### Password first
- Command: _USER umih874_
- Server:
- Command: _PASS hello_
- Server: 
- Command: _ACCT 888-888_
- Server:
- Command: _DONE_
- Server: _Bye_
#### Account first
- Command: _USER umih874_
- Server:
- Command: _ACCT 888-888_
- Server:
- Command: _PASS hello_
- Command: _DONE_
- Server: _Bye_

### Wrong credentials
#### Secenario #1:
- Command: _USER user_ (Or anything that is not listed in users.txt)
- Server: 
- Command: _USER umih874_ (Provide correct username)
- Server:
- Command: _PASS bla_ (Provide wrong password)
- Server: 
- Command: _PASS hello_ (Provide correct password)
- Server:
- Commnad: _ACCT 456-789_ (Provide wrong account)
- Server:
- Command: _ACCT 888-888_ (Provide correct account
- Server:
- Command: _DONE_
- Server: _Bye_
#### Secenario #2:
- Command: _USER user_ (Or anything that is not listed in users.txt)
- Server: 
- Command: _USER umih874_ (Provide correct username)
- Server:
- Command: _PASS bla_ (Provide wrong password)
- Server:
- Commnad: _ACCT 456-789_ (Provide wrong account)
- Server:
- Command: _ACCT 888-888_ (Provide correct account)
- Server:
- Command: _PASS hello_ (Provide correct password)
- Server:
- Command: _DONE_
- Server: _Bye_

### User already logged in
- Command: _USER root_
- Server:
- Command: _USER admin_
- Server:
- Command: _DONE_
- Server: _Bye_

### 
### Change user 

## ACCT

## PASS
