Java Settlers - A web-based client-server version of Settlers of Catan

Introduction
------------

JSettlers is a web-based version of the board game Settlers of Catan
written in Java. This client-server system supports multiple
simultaneous games between people and computer-controlled
opponents. Initially created as an AI research project.

The client may be run as a Java application, or as an applet when
accessed from a web site which also hosts a JSettlers server.

The server may be configured to use a MySQL database to store account
information.  A client applet to create user accounts is also
provided.

JSettlers is an open-source project licensed under the GPL. The
software is maintained as a SourceForge project at
http://sourceforge.net/projects/jsettlers.

Forums for discussions and community based support are provided at
SourceForge.

                          -- The JSettlers Development Team


Contents
--------

  Documentation
  Requirements
  Setting up and testing
  Shutting down the server
  Hosting a JSettlers Server
  Database Setup
  Development and Compiling


Documentation
-------------

User documentation for game play is available as .html pages located
in "docs/users" directory. These can be put on a JSettlers server for
its users using the applet.

Currently, this README is the only technical documentation for running
the client or server, setup and other issues. Over time other more
will be written. If you are interested in helping write documentation
please contact the development team from the SourceForge site.


Requirements
------------

To play JSettlers by connecting to a remote server you will need the
Java Runtime Version 1.1 or above (1.4 recommended). To connect as an
applet, use any browser which is Java enabled (again, we recommend
Java 1.4 using the browser plug-in). Note that if your server uses a
user database, the statistics viewer applet does not work on clients
prior to Java 1.2.

To Play JSettlers locally you need the Java Runtime 1.4 (or
later). Remote clients started on the command line can connect
directly to this server. To host a JSettlers server and provide a web
applet for clients, you will need an http server such as Apache's
httpd, available from http://httpd.apache.org.

To build JSettlers from source, you will need Apache Ant, available from
http://ant.apache.org.


Distribution Contents
---------------------

JSettlers distributes as an archive which unpacks to the directory
"jsettlers-<version>".  In this documentation we refer to that
directory as "$JS".

This documentation refers to the JSettlers distribution directory as
"$JS", which should contain:

$JS/
  COPYING.txt
  TODO.txt
  VERSIONS.txt
  JSettlers.jar
  JSettlersServer.jar
  bin/
  docs/
  jsettlers.properties
  jsettlers.properties.default
  lib/
  web/
  
[TODO: finish?]


Setting up and testing
----------------------

The $JS directory can be put anywhere on your system.  If you put
$JS/bin on your PATH, you can omit the path in the examples that
follow.

Start by editing the properties file for your system. For the server,
edit $JS/jsettlersd.properties. This is a Java properties file and is
commented to describe the parameters. If, later, you wish to revert to
the default settings, replace that file with a copy of
$JS/jsettlersd.properties.default.

To start the server, from the command line enter:

  $ $JS/bin/jsettlersd

If the Java executable is not on your PATH, you may set the JAVA_HOME
to specify it. This can also be used to start with a different version
of Java than your systems default.

By default, use of a database to store user information is
disabled. See the "Database Setup" section for more information.

Now, from another command line, start the player client with
the following command:

  $ $JS/bin/jsettlers localhost <port>

Where port is the same as specified in your jsettlersd.properties file
(8880 by default).

If you are using Java 1.1 you will need to unpack the Java archive
(Java could not run directly from jar files until version 1.2). The
commands to unpack, then start the client are: [TODO: revisit]

  $ jar -xf JSettlers.jar
  $ java soc.client.SOCPlayerClient localhost 8880

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
separate command line windows [TODO: fix!].

  $ java -cp JSettlersServer.jar soc.robot.SOCRobotClient localhost 8880 robot1 passwd

  $ java -cp JSettlersServer.jar soc.robot.SOCRobotClient localhost 8880 robot2 passwd

  $ java -cp JSettlersServer.jar soc.robot.SOCRobotClient localhost 8880 robot3 passwd

Now click on the "Sit Here" button and press "Start Game".  The robot
players should automatically join the game and start playing.

If you want other people to access your server, tell them your server
IP address and port number.  They will enter the following command:

  $ $JS/bin/jsettlers <host> <port>

Where host is your IP address and port is the port number you put in
jsettlersd.properties.

If you would like to maintain accounts for your JSettlers server,
start the database prior to starting the JSettlers Server. See the
directions in "Database Setup".


Shutting down the server
------------------------

To shut down the server enter *STOP* in the chat area of a game
window.  This will stop the server and all connected clients will be
disconnected. You must be logged on as "debug" for this to work.


Hosting a JSettlers server
--------------------------
  - Start MySQL server (optional)
  - Start JSettlers Server
  - Start http server (optional)
  - Copy JSettlers.jar jar and "web/*.html" server directory (optional)
    - Extract JSettlers.jar to allow Java 1.1 clients (optional)
  - Copy "docs/users" to the server directory (optional)

To host a JSettlers server, start the server as described in "Setup
and Testing". To maintain user accounts, be sure to start the database
first. Remote users can simply start their clients as described there,
and specify your server as host.

To provide a web page from which users can run the applet, you will
need to set up an html server, such as Apache.  We assume you have
installed it correctly, and will refer to "${docroot}" as a directory
your web server is configured to provide.

Copy the sample .html pages from "web" to ${docroot}. Edit them, to
make sure the PORT parameter in "index.html" "statistics.html" and
"account.html" applet tags match the port of your JSettlers server.

Next copy the client files to the server. Copy JSettlers.jar to
${docroot}. This will allow users with Java version 1.2 or later
installed to use the browser plug-in. Using the .jar file allows for
faster downloads, and startup times, but does not allow browsers with
Java version 1.1 to start the client.

To allow browsers with old versions of Java (1.1) to use the applet,
unpack JSettlers.jar and copy (recursively) the extracted "soc" and
"resources" directories to ${docroot}. To unpack, use: [TODO: revisit]

  $ jar -xf JSettlers.jar

You may also copy the "doc/users" directory (recursively) to the same
directory as the sample .html pages to provide user documentation.

Your web server directory structure should now contain:
  ${docroot}/index.html
  ${docroot}/<other>.html
  ${docroot}/JSettlers.jar
  ${docroot}/resources/...
  ${docroot}/soc/...
  ${docroot}/users/...

Users should now be able to visit your web site to run the client
version of JSettlers.


Database Setup
--------------

If you want to maintain user accounts, you will need to set up a MySQL
database. We assume you have installed it correctly.

Edit the $JS/jsettlersd.properties file for your database management
system (dbms). You can specify your Java driver, which must be
installed as a Java extension [TODO: elaborate], database name
(default is socdata), and user/password values (defaults are
socuser/socpass).

Note that testing is only done on MySQL. Please share any experience
you have with other databases us at the SourceForge Site.


An example SQL script has been included to initialize a clean system.
Edit the script for your system (using the same values as in your
jsettlersd.properties file).

To create the default database and user, execute the SQL db script:

  $ mysql -u root -p -e "SOURCE $JS/bin/sql/jsettlers-db.sql"

This will connect as root, prompt for the root password, create the
'socdata' database, create a 'socuser' user with the password
'socpass'. To build the empty tables, execute the SQL tables script:

  $ mysql -u root -p -e "SOURCE $JS/bin/sql/jsettlers-tables.sql"

This script will fail if the tables already exist.

To create accounts in the socdata database, run the simple account
creation client with the following command: [TODO: revisit]

  $ java -cp JSettlers.jar soc.client.SOCAccountClient localhost 8880


Development and Compiling
-------------------------

Source code for JSettlers is available via anonymous CVS. Source code
tarballs are also made available.  See the project website at
http://sourceforge.net/projects/jsettlers/ for details. Patches
against CVS may be submitted there.

Before building, make sure you have at least version 1.4 of the Java
development kit installed.  If you simply want to run the client and
server, you only need the Java. If you wish to maintain a user
database for your server, you need MySQL installed, and configured.

This package was designed to use the ANT tool available from
http://ant.apache.org tools.  We assume you have installed it
correctly.

Check the "build.properties" file. There may be build variables you
may want to change locally. These can also be changed from the command
line when calling ant, by passing a "-Dname=value" parameter to ant.

Now you are ready to invoke ant. There are several targets, here are
the most useful ones:

 build      Create project jar files. (default)
 clean      Cleans the project of all generated files
 compile    Compile class files into "target/classes"
 dist       Build distribution tarballs and zips.
 javadoc    Creates JavaDoc files in "target/docs/api"
 src        Create a tarball of the source tree

All files created by building are in the "target" directory, including
Java .class files, and JavaDoc files. Distribution tarballs, zip
files, and installation files are placed in "dist".
