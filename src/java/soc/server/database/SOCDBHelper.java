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
import java.util.Iterator;
import java.util.Vector;


/**
 * This class contains methods for connecting to a database
 * and for manipulating the data stored there.
 *
 * Based on jdbc code found at www.javaworld.com
 *
 * @author Robert S. Thomas
 */
/**
 * This code assumes that you're using mySQL as your database. The schema for
 * JSettlers tables can be found in the distribution
 * <code>$JSETTLERS/bin/sql/jsettlers-tables.sql</code>.
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
    
    private static String HUMAN_STATS_QUERY =       "SELECT nickname, wins, losses, totalpoints, totalpoints/(wins+losses) AS avg, 100 * (wins/(wins+losses)) AS pct FROM users WHERE (wins+losses) > 0 ORDER BY pct desc, avg desc, totalpoints desc;";
    
    private static String LASTLOGIN_UPDATE =        "UPDATE users SET lastlogin = ? WHERE nickname = ? ;";
    
    private static String RECORD_LOGIN_COMMAND =    "INSERT INTO logins VALUES (?,?,?);";
    
    private static String RESET_HUMAN_STATS =       "UPDATE users SET wins = 0, losses = 0, totalpoints = 0 WHERE nickname = ? AND password = ?;";
    
    private static String ROBOT_PARAMS_QUERY =      "SELECT * FROM robotparams WHERE robotname = ?;";
    
    private static String ROBOT_STATS_QUERY =       "SELECT robotname, wins, losses, totalpoints, totalpoints/(wins+losses) AS avg, (100*(wins/(wins+losses))) AS pct FROM robotparams WHERE (wins+losses) > 0 ORDER BY pct desc, avg desc, totalpoints desc;";
    
    private static String SAVE_GAME_COMMAND =       "INSERT INTO games VALUES (?,?,?,?,?,?,?,?,?,?);";
    
    private static String UPDATE_ROBOT_STATS =      "UPDATE robotparams SET wins = wins + ?, losses = losses + ?, totalpoints = totalpoints + ? WHERE robotname = ?;";
    
    private static String UPDATE_USER_STATS = "UPDATE users SET wins = wins + ?, losses = losses + ?, totalpoints = totalpoints + ? WHERE nickname = ?;";
    
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
    private static PreparedStatement updateRobotStats = null;
    private static PreparedStatement updateUserStats = null;
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
            SQLException sx = new SQLException("MySQL driver is unavailable");
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
        updateRobotStats = connection.prepareStatement(UPDATE_ROBOT_STATS);
        updateUserStats = connection.prepareStatement(UPDATE_USER_STATS);
        
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
                handleSQLException(sqlE);
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
                handleSQLException(sqlE);
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
                handleSQLException(sqlE);
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
                handleSQLException(sqlE);
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
                handleSQLException(sqlE);
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
                handleSQLException(sqlE);
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
                handleSQLException(sqlE);
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
                    SOCPlayer pl = ga.getPlayer(i);

		    saveGameCommand.setString(sGCindex++, pl.getName());
                }
                for (int i = 0; i < SOCGame.MAXPLAYERS; i++)
                {
                    SOCPlayer pl = ga.getPlayer(i);
                    
                    saveGameCommand.setInt(sGCindex++, pl.getTotalVP());
                }
                
                saveGameCommand.setTimestamp(sGCindex++, new Timestamp(ga.getStartTime().getTime()));

                // execute the Command
                saveGameCommand.executeUpdate();

		// iterate through the players
                for (int i = 0; i < SOCGame.MAXPLAYERS; i++)
                {
                    SOCPlayer pl = ga.getPlayer(i);
                    int points = pl.getTotalVP();
                    boolean isWinner = points >= 10;
                    
                    // Choose the table to update
                    if (pl.isRobot())
                    {
                        updateRobotStats.setInt(1, (isWinner ? 1 : 0)); // wins
                        updateRobotStats.setInt(2, (isWinner ? 0 : 1)); // losses
                        updateRobotStats.setInt(3, points); // totalpoints
                        updateRobotStats.setString(4, pl.getName());

                        updateRobotStats.executeUpdate();
                    }
                    else // The player is human
                    {
                        updateUserStats.setInt(1, (isWinner ? 1 : 0)); // wins
                        updateUserStats.setInt(2, (isWinner ? 0 : 1)); // losses
                        updateUserStats.setInt(3, points); // totalpoints
                        updateUserStats.setString(4, pl.getName());

                        updateUserStats.executeUpdate();
                    }
                }
                
                return true;
            }
            catch (SQLException sqlE)
            {
                handleSQLException(sqlE);
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
                handleSQLException(sqlE);
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
        // this belongs elsewhere
        final int STAT_COLUMNS = 7;

        String[][] statistics = null;
        Statement stmt = null;
        
        // ensure that the JDBC connection is still valid
        if (checkConnection())
        {
            try
            {
                Vector rows = new Vector();
                ResultSet resultSet = null;
                stmt = connection.createStatement();

                // Execute the appropriate query
                if (type.equals("robot"))
                {
                    resultSet = stmt.executeQuery(ROBOT_STATS_QUERY);
                }
                else
                {
                    resultSet = stmt.executeQuery(HUMAN_STATS_QUERY);
                }

                while (resultSet.next())
                {
                    String[] rowData = new String[STAT_COLUMNS];
                    
                    rowData[0] = resultSet.getString(1);               // Name
                    rowData[1] = Integer.toString(resultSet.getRow()); // Rank
                    rowData[2] = resultSet.getString(2);               // Wins
                    rowData[3] = resultSet.getString(3);               // Losses
                    rowData[4] = resultSet.getString(4);               // Total Points
                    rowData[5] = resultSet.getString(5);               // Average Points
                    rowData[6] = resultSet.getString(6);               // Percent Wins

                    rows.add(rowData);
                }
                resultSet.close();

                if (rows.isEmpty() )
                {
                    String[] rowData = new String[STAT_COLUMNS];
                    rowData[0] = "None";
                    rows.add(rowData);
                }
                
                // Create the array of statistics to return
                statistics = new String[rows.size()][];
                
                int index = 0;
                Iterator iter = rows.iterator();
                while (iter.hasNext())
                {
                    statistics[index++] = (String[]) iter.next();
                }
            }
            catch (SQLException sqlE)
            {
                handleSQLException(sqlE);
            }
            finally
            {
                try
                {
                    if (stmt != null)
                        stmt.close();
                }
                catch (SQLException sqlE)
                {
                    handleSQLException(sqlE);
                }
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
                handleSQLException(sqlE);
            }
        }
        return "Connection failed.";
    }

    /**
     * Common behavior for SQL Exceptions.
     */
    private static void handleSQLException(SQLException x) throws SQLException
    {
        errorCondition = true;
        x.printStackTrace();
        throw x;
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
                updateRobotStats.close();
                updateUserStats.close();
                userFaceQuery.close();
                userPasswordQuery.close();                                
                connection.close();
            }
            catch (SQLException sqlE)
            {
                handleSQLException(sqlE);
            }
        }
    }

    /**
     * Useful for debugging. Leave commented out of final build.
     *   /
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
    */
}
