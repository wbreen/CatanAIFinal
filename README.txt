Java Settlers - A web-based client-server version of Settlers of Catan

Introduction
------------

JSettlers is a web-based version of the board game Settlers of Catan
written in Java. This client-server system supports multiple
simultaneous games between people and computer-controlled
opponents. Initially created as an AI research project.

The client may be run as a Java application, accessing a server on the
localhost, or as an applet when accessed from a web site which also
hosts a server.

The server may be set up to access a MySQL database to store usernames
and passwords.  A client applet to create user accounts is also
provided.

JSettlers is an open-source project licensed under the GPL. The
software is maintained as a SourceForge project at
http://sourceforge.net/projects/jsettlers.

Forums for discussions and community based support are provided at
SourceForge.


Contents
--------

  Documentation
  Setting up and testing
  Shutting down the server
  Hosting a JSettlers Server
  Database Setup
  Development and Compiling


Documentation
-------------

User documentation for playing the game is available as .html pages
located in "docs/users" directory. These can be put on a JSettlers
server for its users using the applet.

Currently, this README is the only technical documentation for running
the client or server, setup and other issues. Over time other more
will be made. If you are interested in helping write this
documentation please contact the development team from the SourceForge
site.


Setting up and testing
----------------------

From the command line, make sure you are in the jsettlers directory.
Start the server with the following command:

  java soc.server.SOCServer 8880 10 dbUser dbPass

You will see the following message:

  Problem connecting to database: java.sql.SQLException: java.lang.ClassNotFoundException: org.gjt.mm.mysql.Driver

This simply means that you don't have the MySQL database setup
correctly.  The server will function normally except that user accounts
cannot be maintained without it.

Now start the player client with the following command:

  java soc.client.SOCPlayerClient localhost 8880

If you are using windows, you will need to open a new command line
window for each command.

In the player client window, enter "debug" in the Nickname field and
create a new game.

Type *STATS* into the chat part of the game window.  You should see
something like the following in the chat display:

* > Uptime: 0:0:26
* > Total connections: 1
* > Current connections: 1
* > Total Users: 1
* > Games started: 0
* > Games finished: 0
* > Total Memory: 2031616
* > Free Memory: 1524112

If you do not, you might not have entered your nickname correctly.  It
must be "debug" in order to use the administrative commands.

Now you can add some robot players.  Enter the following commands in
separate command shells

  java soc.robot.SOCRobotClient localhost 8880 robot1 password

  java soc.robot.SOCRobotClient localhost 8880 robot2 password

  java soc.robot.SOCRobotClient localhost 8880 robot3 password

Now click on the "Sit Here" button and press "Start Game".  The robot
players should automatically join the game and start playing.

If you want other people to access your server, tell them your server
IP address and port number (in this case 8880).  They will enter the
following command:

  java soc.client.SOCPlayerClient host port_number

Where host is the IP address and port_number is the port number.


Shutting down the server
------------------------

To shut down the server enter *STOP* in the chat area of a game
window.  This will stop the server and all connected clients will be
disconnected.


Hosting a JSettlers server
--------------------------

To host a JSettlers server, start the server as described above.
Remote users can simply start their clients as described above and
point to your server.

To provide a web page which users can access to run the applet, you
will need to set up an html server such as Apache.  We assume you have
installed it correctly, and will refer to "${docroot}" as a directory
your web server is configured to provide.

Sample .html pages for such a server are located in the "www"
directory. Copy these files into ${docroot}. Edit these to make sure
the port numbers in "index.html" and "account.html" for the applets,
match those of your JSettlers server.

Next copy the client files to the server. Do this by copying the the
"soc" directory (recursively) to the same directory as the sample
.html pages.  You may delete the "soc/robot" and "soc/server"
directories from the server, as they are not needed for the client.

You may also copy the "doc/users" directory (recursively) to the same
directory as the sample .html pages to provide user documentation.

Your web server directory structure should now contain:
  ${docroot}/index.html
  ${docroot}/*.html
  ${docroot}/soc/...
  ${docroot}/users/...

Users should now be able to visit your web site to run the client
version of JSettlers.


Database Setup
--------------

If you want to maintain user accounts, you will need to set up a MySQL
database. This will eliminate the "Problem connecting to database"
errors from the server. We assume you have installed it correctly. 

Run the following commands to create the database and configure it's
tables.

CREATE DATABASE socdata;

USE socdata;

CREATE TABLE users (nickname VARCHAR(20), host VARCHAR(50), password VARCHAR(20), email VARCHAR(50), lastlogin DATE);

CREATE TABLE logins (nickname VARCHAR(20), host VARCHAR(50), lastlogin DATE);
CREATE TABLE games (gamename VARCHAR(20), player1 VARCHAR(20), player2 VARCHAR(20), player3 VARCHAR(20), player4 VARCHAR(20), score1 TINYINT, score2 TINYINT, score3 TINYINT, score4 TINYINT, starttime TIMESTAMP);

CREATE TABLE robotparams (robotname VARCHAR(20), maxgamelength INT, maxeta INT, etabonusfactor FLOAT, adversarialfactor FLOAT, leaderadversarialfactor FLOAT, devcardmultiplier FLOAT, threatmultiplier FLOAT, strategytype INT, starttime TIMESTAMP, endtime TIMESTAMP, gameswon INT, gameslost INT, tradeFlag BOOL);


To create accounts, run the simpler account creation client with the
following command:

  java soc.client.SOCAccountClient localhost 8880


Development and Compiling
-------------------------

Source code for JSettlers is available via anonymous CVS. Source code
tarballs are also made available.  See the project website at
http://sourceforge.net/projects/jsettlers/ for details.

Before building, make sure you have at least version 1.4 of the Java
development kit installed.  If you simply want to run the client and
server, you only need the Java runtime environment. If you wish to
maintain a user database for your server, you need MySQL installed,
and configured.

This package was designed to use the ANT tool available from
http://ant.apache.org tools.  We assume you have installed it
correctly.

Take some time to edit "build.properties".  There may be build
variables you may want to change locally. These can also be changed
from the command line when calling ant by passing a
"-Dvar.name=var-value" parameter to ant.

Now you are ready to invoke ant.  There are many possible targets, here
are the most useful ones:

 build      Create project jar files. (default)
 clean      Cleans the project of all generated files
 compile    Compile class files into 'target/classes'
 dist       Build distribution tarballs and zips.
 javadoc    Creates JavaDoc files in 'target/docs/api'
 resources  Copy resources to classes directory
 src        Create a tarball of the source tree

All generated files are created in the 'target' directory, including
Java .class files, and JavaDoc files.
