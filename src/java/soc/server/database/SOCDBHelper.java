/**
 * Java Settlers - An online multiplayer version of the game Settlers of Catan
 * Copyright (C) 2003  Robert S. Thomas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * The author of this program can be reached at thomas@infolab.northwestern.edu
 **/
package soc.server.database;

import soc.game.SOCGame;
import soc.game.SOCPlayer;

import soc.util.SOCRobotParameters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.Calendar;


/**
 * This class contains methods for connecting to a database
 * and for manipulating the data stored there.
 *
 * Based on jdbc code found at www.javaworld.com
 *
 * @author Robert S. Thomas
 */
/**
 * This code assumes that you're using mySQL as your database.
 * It uses a database created with the following commands:
 * CREATE DATABASE socdata;
 * USE socdata;
 * CREATE TABLE users (nickname VARCHAR(20), 
 *                     host VARCHAR(50), 
 *                     password VARCHAR(20),
 *                     email VARCHAR(50), 
 *                     lastlogin DATE, 
 *                     wins INT DEFAULT 0,
 *                     losses INT DEFAULT 0,
 *                     face SMALLINT DEFAULT 1,
 *                     totalpoints INT DEFAULT 0);
 *
 * CREATE TABLE logins (nickname VARCHAR(20), 
 *                      host VARCHAR(50),
 *                      lastlogin DATE);
 *
 * CREATE TABLE games (gamename VARCHAR(20), 
 *                     player1 VARCHAR(20),
 *                     player2 VARCHAR(20),
 *                     player3 VARCHAR(20), 
 *                     player4 VARCHAR(20),
 *                     score1 SMALLINT,
 *                     score2 SMALLINT,
 *                     score3 SMALLINT,
 *                     score4 SMALLINT,
 *                     starttime TIMESTAMP);
 *
 * CREATE TABLE robotparams (robotname VARCHAR(20),
 *                           maxgamelength INT,
 *                           maxeta INT,
 *                           etabonusfactor FLOAT,
 *                           adversarialfactor FLOAT,
 *                           leaderadversarialfactor FLOAT,
 *                           devcardmultiplier FLOAT,
 *                           threatmultiplier FLOAT,
 *                           strategytype INT,
 *                           starttime TIMESTAMP,
 *                           endtime TIMESTAMP,
 *                           wins INT DEFAULT 0,
 *                           losses INT DEFAULT 0,
 *                           tradeFlag BOOL,
 *                           totalpoints DEFAULT 0);
 *
 */
public class SOCDBHelper
{
    private static Connection connection = null;

    /**
     * This flag indicates that the connection should be valid, yet the last
     * operation failed. Methods will attempt to reconnect prior to their
     * operation if this is set.
     */
    private static boolean errorCondition = false;

    /** Cached username used when reconnecting on error */
    private static String userName;

    /** Cached password used when reconnecting on error */
    private static String password;
    
    private static String CREATE_ACCOUNT_COMMAND =  "INSERT INTO users VALUES (?,?,?,?,?,?,?,?,?);";
    
    private static String HAS_ACCOUNT_QUERY =       "SELECT * FROM users WHERE nickname = ? AND password = ? ;";
    
    private static String HOST_QUERY =              "SELECT nickname FROM users WHERE ( users.host = ? );";
    
    private static String HUMAN_ROW_COUNT =         "SELECT count(*) FROM users WHERE wins + losses > 0;";
    
    private static String HUMAN_STATS_QUERY =       "SELECT nickname, wins, losses, totalpoints, totalpoints/(wins+losses) AS avg, 100 * (wins/(wins+losses)) AS pct FROM users WHERE (wins+losses) > 0 ORDER BY pct desc, avg desc, totalpoints desc;";
    
    private static String LASTLOGIN_UPDATE =        "UPDATE users SET lastlogin = ? WHERE nickname = ? ;";
    
    private static String RECORD_LOGIN_COMMAND =    "INSERT INTO logins VALUES (?,?,?);";
    
    private static String RESET_HUMAN_STATS =       "UPDATE users SET wins = 0, losses = 0, totalpoints = 0 WHERE nickname = ? AND password = ?;";
    
    private static String ROBOT_PARAMS_QUERY =      "SELECT * FROM robotparams WHERE robotname = ?;";
    
    private static String ROBOT_ROW_COUNT =         "SELECT count(*) FROM robotparams WHERE wins + losses > 0;";
    
    private static String ROBOT_STATS_QUERY =       "SELECT robotname, wins, losses, totalpoints, totalpoints/(wins+losses) AS avg, (100*(wins/(wins+losses))) AS pct FROM robotparams WHERE (wins+losses) > 0 ORDER BY pct desc, avg desc, totalpoints desc;";
    
    private static String SAVE_GAME_COMMAND =       "INSERT INTO games VALUES (?,?,?,?,?,?,?,?,?,?);";
    
    private static String UPDATE_ROBOT_LOSSES =     "UPDATE robotparams SET losses = losses + 1 WHERE robotparams.robotname = ?;";
    
    private static String UPDATE_ROBOT_POINTS =     "UPDATE robotparams SET totalpoints = totalpoints + ? WHERE robotparams.robotname = ?;";
    
    private static String UPDATE_ROBOT_WINS =       "UPDATE robotparams SET wins = wins + 1 WHERE robotparams.robotname = ?;";
    
    private static String UPDATE_STANDINGS_LOSSES = "UPDATE users SET losses = losses + 1 WHERE users.nickname = ?;";
    
    private static String UPDATE_STANDINGS_WINS =   "UPDATE users SET wins = wins + 1 WHERE users.nickname = ?;";
    
    private static String UPDATE_TOTAL_POINTS =     "UPDATE users SET totalpoints = totalpoints + ? WHERE users.nickname = ?;";
    
    private static String USER_FACE_QUERY =         "SELECT face FROM users WHERE users.nickname = ?;";
    
    private static String USER_FACE_UPDATE =        "UPDATE users SET face = ? WHERE nickname = ?;";
    
    private static String USER_PASSWORD_QUERY =     "SELECT password FROM users WHERE ( users.nickname = ? );";
    
    private static PreparedStatement createAccountCommand = null;    
    private static PreparedStatement hasAccountQuery = null;
    private static PreparedStatement hostQuery = null;
    private static PreparedStatement lastloginUpdate = null;
    private static PreparedStatement recordLoginCommand = null;
    private static PreparedStatement resetHumanStats = null;
    private static PreparedStatement robotParamsQuery = null;
    private static PreparedStatement saveGameCommand = null;
    private static PreparedStatement updateRobotLosses = null;
    private static PreparedStatement updateRobotPoints = null;
    private static PreparedStatement updateRobotWins = null;
    private static PreparedStatement updateStandingsLosses = null;
    private static PreparedStatement updateStandingsWins = null;
    private static PreparedStatement updateTotalPoints = null;
    private static PreparedStatement userFaceQuery = null;
    private static PreparedStatement userFaceUpdate = null;
    private static PreparedStatement userPasswordQuery = null;    

    /**
     * This makes a connection to the database
     * and initializes the prepared statements.
     *
     * @param user  the user name for accessing the database
     * @param pswd  the password for the user
     * @return true if the database was initialized
     * @throws SQLException if an SQL command fails, or the db couldn't be
     * initialied
     */
    public static void initialize(String user, String pswd) throws SQLException
    {
        try
        {
            // Load the mysql driver. Revisit exceptions when /any/ JDBC allowed
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();

            connect(user, pswd);
        }
        catch (ClassNotFoundException x)
        {
            SQLException sx =
                new SQLException("MySQL driver is unavailable");
            sx.initCause(x);
            throw sx;
        }
        catch (Exception x) // everything else
        {
            // InstantiationException & IllegalAccessException
            // should not be possible  for org.gjt.mm.mysql.Driver
            // ClassNotFound
            SQLException sx = new SQLException("Unable to initialize user database");
            sx.initCause(x);
            throw sx;
        }
    }

    /**
     * Checks if connection is supposed to be present and attempts to reconnect
     * if there was previously an error.  Reconnecting closes the current
     * conection, opens a new one, and re-initializes the prepared statements.
     *
     * @return true if the connection is established upon return
     */
    private static boolean checkConnection() throws SQLException
    {
        if (connection != null)
        {
            return (! errorCondition) || connect(userName, password);
        }

        return false;
    }

    /**
     * initialize and checkConnection use this to get ready.
     */
    private static boolean connect(String user, String pswd)
        throws SQLException
    {
        String url = "jdbc:mysql://localhost/socdata";

        connection = DriverManager.getConnection(url, user, pswd);

        errorCondition = false;
        userName = user;
        password = pswd;
        
        // prepare PreparedStatements for queries
        createAccountCommand = connection.prepareStatement(CREATE_ACCOUNT_COMMAND);
        hasAccountQuery = connection.prepareStatement(HAS_ACCOUNT_QUERY);
        hostQuery = connection.prepareStatement(HOST_QUERY);
        lastloginUpdate = connection.prepareStatement(LASTLOGIN_UPDATE);
        recordLoginCommand = connection.prepareStatement(RECORD_LOGIN_COMMAND);
        resetHumanStats = connection.prepareStatement(RESET_HUMAN_STATS);
        robotParamsQuery = connection.prepareStatement(ROBOT_PARAMS_QUERY);
        saveGameCommand = connection.prepareStatement(SAVE_GAME_COMMAND);
        userFaceQuery = connection.prepareStatement(USER_FACE_QUERY);
        userFaceUpdate = connection.prepareStatement(USER_FACE_UPDATE);
        userPasswordQuery = connection.prepareStatement(USER_PASSWORD_QUERY);
        updateRobotLosses = connection.prepareStatement(UPDATE_ROBOT_LOSSES);
        updateRobotPoints = connection.prepareStatement(UPDATE_ROBOT_POINTS);
        updateRobotWins = connection.prepareStatement(UPDATE_ROBOT_WINS);
        updateStandingsLosses = connection.prepareStatement(UPDATE_STANDINGS_LOSSES);
        updateStandingsWins = connection.prepareStatement(UPDATE_STANDINGS_WINS);
        updateTotalPoints = connection.prepareStatement(UPDATE_TOTAL_POINTS);
        
        return true;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param sUserName DOCUMENT ME!
     *
     * @return null if user account doesn't exist
     *
     * @throws SQLException DOCUMENT ME!
     */
    public static String getUserPassword(String sUserName) throws SQLException
    {
        String password = null;

        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                // fill in the data values to the Prepared statement
                userPasswordQuery.setString(1, sUserName);

                // execute the Query
                ResultSet resultSet = userPasswordQuery.executeQuery();

                // if no results, user is not authenticated
                if (resultSet.next())
                {
                    password = resultSet.getString(1);
                }

                resultSet.close();
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }

        return password;
    }

    /**
     * returns default face for player
     *
     * @param sUserName username of player
     *
     * @return 1 if user account doesn't exist
     *
     * @throws SQLException if database is horked
     */
    public static int getUserFace(String sUserName) throws SQLException
    {
        int face = 1;

        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                // fill in the data values to the Prepared statement
                userFaceQuery.setString(1, sUserName);

                // execute the Query
                ResultSet resultSet = userFaceQuery.executeQuery();

                // if no results, user is not authenticated
                if (resultSet.next())
                {
                    face = resultSet.getInt(1);
                }

                resultSet.close();
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }

        return face;
    }

    /**
     * DOCUMENT ME!
     *
     * @param host DOCUMENT ME!
     *
     * @return  null if user is not authenticated
     *
     * @throws SQLException DOCUMENT ME!
     */
    public static String getUserFromHost(String host) throws SQLException
    {
        String nickname = null;

        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                // fill in the data values to the Prepared statement
                hostQuery.setString(1, host);

                // execute the Query
                ResultSet resultSet = hostQuery.executeQuery();

                // if no results, user is not authenticated
                if (resultSet.next())
                {
                    nickname = resultSet.getString(1);
                }

                resultSet.close();
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }

        return nickname;
    }

    /**
     * DOCUMENT ME!
     *
     * @param userName DOCUMENT ME!
     * @param host DOCUMENT ME!
     * @param password DOCUMENT ME!
     * @param email DOCUMENT ME!
     * @param time DOCUMENT ME!
     *
     * @return true if the account was created
     *
     * @throws SQLException DOCUMENT ME!
     */
    public static boolean createAccount(String userName, String host, String password, String email, long time) throws SQLException
    {
        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                java.sql.Date sqlDate = new java.sql.Date(time);
                Calendar cal = Calendar.getInstance();

                // fill in the data values to the Prepared statement
                createAccountCommand.setString(1, userName);
                createAccountCommand.setString(2, host);
                createAccountCommand.setString(3, password);
                createAccountCommand.setString(4, email);
                createAccountCommand.setDate(5, sqlDate, cal);
                createAccountCommand.setInt(6, 0); // wins
                createAccountCommand.setInt(7, 0); // losses
                createAccountCommand.setInt(8, 1); // face
                createAccountCommand.setInt(9, 0); // totalpoints

                // execute the Command
                createAccountCommand.executeUpdate();

                return true;
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param userName DOCUMENT ME!
     * @param host DOCUMENT ME!
     * @param time DOCUMENT ME!
     *
     * @return true if the login was recorded
     *
     * @throws SQLException DOCUMENT ME!
     */
    public static boolean recordLogin(String userName, String host, long time) throws SQLException
    {
        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                java.sql.Date sqlDate = new java.sql.Date(time);
                Calendar cal = Calendar.getInstance();

                // fill in the data values to the Prepared statement
                recordLoginCommand.setString(1, userName);
                recordLoginCommand.setString(2, host);
                recordLoginCommand.setDate(3, sqlDate, cal);

                // execute the Command
                recordLoginCommand.executeUpdate();

                // update the last login time
                updateLastlogin(userName, time);
                return true;
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param userName DOCUMENT ME!
     * @param time DOCUMENT ME!
     *
     * @return true if the save succeeded
     *
     * @throws SQLException DOCUMENT ME!
     */
    public static boolean updateLastlogin(String userName, long time) throws SQLException
    {
        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                java.sql.Date sqlDate = new java.sql.Date(time);
                Calendar cal = Calendar.getInstance();

                // fill in the data values to the Prepared statement
                lastloginUpdate.setDate(1, sqlDate, cal);
                lastloginUpdate.setString(2, userName);

                // execute the Command
                lastloginUpdate.executeUpdate();

                return true;
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }

        return false;
    }

    /**
     * Saves faceId to the database 
     *
     * @param ga game to be saved
     *
     * @return true if the save succeeded
     *
     * @throws SQLException if the database isn't available
     */
    public static boolean saveFaces(SOCGame ga) throws SQLException
    {
        // Insure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                // Record face for humans
                for (int i = 0; i < SOCGame.MAXPLAYERS; i++)
                {
                    SOCPlayer pl = ga.getPlayer(i);
                    
                    // If the player is human
                    if (!pl.isRobot())
                    {
                        // Store the faceId in the database
                        userFaceUpdate.setInt(1, pl.getFaceId());
                        userFaceUpdate.setString(2, pl.getName());
                        userFaceUpdate.executeUpdate();
                    }
                }
                
                return true;
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }

        return false;
    }

    /**
     * Saves game scores to the database (both user and games tables)
     *
     * @param ga game to be saved
     *
     * @return true if the save succeeded
     *
     * @throws SQLException if the database isn't available
     */
    public static boolean saveGameScores(SOCGame ga) throws SQLException
    {
        int sGCindex = 1;
        SOCPlayer pl;

        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                // fill in the data values to the Prepared statement
                saveGameCommand.setString(sGCindex++, ga.getName());

		// iterate through the players
                for (int i = 0; i < SOCGame.MAXPLAYERS; i++)
                {
                    pl = ga.getPlayer(i);

		    saveGameCommand.setString(sGCindex++, pl.getName());
                }
                for (int i = 0; i < SOCGame.MAXPLAYERS; i++)
                {
                    pl = ga.getPlayer(i);
                    
                    saveGameCommand.setInt(sGCindex++, pl.getTotalVP());
                }
                
                saveGameCommand.setTimestamp(sGCindex++, new Timestamp(ga.getStartTime().getTime()));

                // execute the Command
                saveGameCommand.executeUpdate();

		// iterate through the players
                for (int i = 0; i < SOCGame.MAXPLAYERS; i++)
                {
                    pl = ga.getPlayer(i);
                    
                    // Choose the table to update
                    if (pl.isRobot())
                    {
                        // Update totalpoints
                        updateRobotPoints.setString(2, pl.getName());
                        updateRobotPoints.setInt(1, pl.getTotalVP());
                        updateRobotPoints.executeUpdate();
                        
                        // Update wins or losses
                        if (pl.getTotalVP() >= 10)
                        {
                            updateRobotWins.setString(1, pl.getName());
                            updateRobotWins.executeUpdate();
                        }
                        else
                        {
                            updateRobotLosses.setString(1, pl.getName());
                            updateRobotLosses.executeUpdate();
                        }
                    }
                    else // The player is human
                    {
                        // Update totalpoints
                        updateTotalPoints.setString(2, pl.getName());
                        updateTotalPoints.setInt(1, pl.getTotalVP());
                        updateTotalPoints.executeUpdate();
                        
                        // Update wins or losses
                        if (pl.getTotalVP() >= 10)
                        {
                            updateStandingsWins.setString(1, pl.getName());
                            updateStandingsWins.executeUpdate();
                        }
                        else
                        {
                            updateStandingsLosses.setString(1, pl.getName());
                            updateStandingsLosses.executeUpdate();
                        }
                    }
                }
                
                return true;
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param robotName DOCUMENT ME!
     *
     * @return null if robotName not in database
     *
     * @throws SQLException DOCUMENT ME!
     */
    public static SOCRobotParameters retrieveRobotParams(String robotName) throws SQLException
    {
        SOCRobotParameters robotParams = null;

        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                // fill in the data values to the Prepared statement
                robotParamsQuery.setString(1, robotName);

                // execute the Query
                ResultSet resultSet = robotParamsQuery.executeQuery();

                // if no results, user is not authenticated
                if (resultSet.next())
                {
                    // retrieve the resultset
                    int mgl = resultSet.getInt(2);
                    int me = resultSet.getInt(3);
                    float ebf = resultSet.getFloat(4);
                    float af = resultSet.getFloat(5);
                    float laf = resultSet.getFloat(6);
                    float dcm = resultSet.getFloat(7);
                    float tm = resultSet.getFloat(8);
                    int st = resultSet.getInt(9);
                    int tf = resultSet.getInt(14);
                    robotParams = new SOCRobotParameters(mgl, me, ebf, af, laf, dcm, tm, st, tf);
                }
                
                resultSet.close();
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }

        return robotParams;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type either "human" or "robot"
     *
     * @return array of robot data
     *
     * @throws SQLException DOCUMENT ME!
     */
    public static String[][] getStatistics(String type) throws SQLException
    {
        int count = 0;
        String[][] statistics = null;
        Statement stmt = connection.createStatement();
        
        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {                
                // Get the expected number of rows in the result
                if (type.equals("robot"))
                {
                    try
                    {
                        ResultSet crs = stmt.executeQuery(ROBOT_ROW_COUNT);
                        if (crs.next())
                        {
                            count = crs.getInt("count(*)");
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.println("SOC.server.database.SOCDBHelper.getStatistics - " +
                                "Error getting count of " + type + "s: " + e);
                    }
                }
                else if (type.equals("human"))
                {
                    try
                    {
                        ResultSet crs = stmt.executeQuery(HUMAN_ROW_COUNT);
                        if (crs.next())
                        {
                            count = crs.getInt("count(*)");
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println("SOC.server.database.SOCDBHelper.getStatistics - " +
                                "Error getting count of " + type + "s: " + e);
                    }
                }
                else
                {
                    System.err.println("SOCDBHelper.getStatistics - Unknown type: " + type);
                    return statistics;
                }
 
                if (count == 0)  // There is no data to return
                {
                    statistics = new String[1][1];
                    statistics[0][0] = "None";
                    return statistics;
                }
                else // There is data to return
                {            
                    ResultSet resultSet = null;
                    
                    // Create the array of statistics to return
                    statistics = new String[count][7];

                    // Execute the appropriate query
                    if (type.equals("robot"))
                    {
                        resultSet = stmt.executeQuery(ROBOT_STATS_QUERY);
                    }
                    else
                    {
                        resultSet = stmt.executeQuery(HUMAN_STATS_QUERY);
                    }

                    // Transfer results to statistics array
                    for (int row = 0; row < count; row++)
                    {
                        if (resultSet.next())
                        {
                            // Add the six columns returned by the query
                            statistics[row][0] = resultSet.getString(1);    // Name
                            statistics[row][1] = Integer.toString(row + 1); // Rank
                            statistics[row][2] = resultSet.getString(2);    // Wins
                            statistics[row][3] = resultSet.getString(3);    // Losses
                            statistics[row][4] = resultSet.getString(4);    // Total Points
                            statistics[row][5] = resultSet.getString(5);    // Average Points
                            statistics[row][6] = resultSet.getString(6);    // Percent Wins
                        }
                        else
                        {
                            System.err.println("SOCDBHelper.retrieveStats - Unexpected end of resultSet at row: " + row);
                        }
                    }
                    resultSet.close();
                }
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }        
        return statistics;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param userName for account to be reset
     * @param password for account to be reset
     *
     * @return true if the account was reset
     *
     * @throws SQLException DOCUMENT ME!
     */
    public static String resetStatistics(String userName, String password) throws SQLException
    {
        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                // Fill in the data for the hasAccount prepared statement
                hasAccountQuery.setString(1, userName);
                hasAccountQuery.setString(2, password);
                
                // Execute query
                ResultSet rs = hasAccountQuery.executeQuery();
                
                if (rs.next())
                {                
                    // Fill in the data values to the Prepared statement
                    resetHumanStats.setString(1, userName);
                    resetHumanStats.setString(2, password);

                    // execute the Command
                    resetHumanStats.executeUpdate();
                    
                    return "Statistics have been successfully reset ";
                }
                else
                {
                    return "Password given does not match password ";
                }
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }
        return "Connection failed.";
    }

    /**
     * DOCUMENT ME!
     */
    public static void cleanup() throws SQLException
    {
        if (checkConnection())
        {
            try
            {
                createAccountCommand.close();
                hasAccountQuery.close();
                hostQuery.close();
                lastloginUpdate.close();                
                recordLoginCommand.close();
                resetHumanStats.close();
                robotParamsQuery.close();
                saveGameCommand.close();                
                updateStandingsWins.close();
                updateStandingsLosses.close();
                updateTotalPoints.close();
                updateRobotWins.close();
                updateRobotLosses.close();
                updateRobotPoints.close();
                userFaceQuery.close();
                userPasswordQuery.close();                                
                connection.close();
            }
            catch (SQLException sqlE)
            {
                errorCondition = true;
                sqlE.printStackTrace();
                throw sqlE;
            }
        }
    }

    //-------------------------------------------------------------------
    // dispResultSet
    // Displays all columns and rows in the given result set
    //-------------------------------------------------------------------
    private static void dispResultSet(ResultSet rs) throws SQLException
    {
        System.out.println("dispResultSet()");

        int i;

        // used for the column headings
        ResultSetMetaData rsmd = rs.getMetaData();

        // Get the number of columns in the result set
        int numCols = rsmd.getColumnCount();

        // Display column headings
        for (i = 1; i <= numCols; i++)
        {
            if (i > 1)
            {
                System.out.print(",");
            }

            System.out.print(rsmd.getColumnLabel(i));
        }

        System.out.println("");

        // Display data, fetching until end of the result set

        boolean more = rs.next();

        while (more)
        {
            // Loop through each column, getting the
            // column data and displaying
            for (i = 1; i <= numCols; i++)
            {
                if (i > 1)
                {
                    System.out.print(",");
                }

                System.out.print(rs.getString(i));
            }

            System.out.println("");

            // Fetch the next result set row
            more = rs.next();
        }
    }
}
