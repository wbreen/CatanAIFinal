/*
 * SOCShowStatistics.java
 *
 * Created on February 16, 2005, 1:36 PM
 */

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
package soc.message;

import soc.server.genericServer.Connection;

import java.util.Enumeration;
import java.util.StringTokenizer;


/**
 * This message returns statistics for humans or robots
 *
 * @author Jim Browan
 **/
public class SOCShowStatistics extends SOCMessage
{
    /**
     * Type of statistics
     **/
    private String stype;
    
    /**
     * The statistics
     **/
    private String[][] statistics;

    /**
     * Create a ShowStats message.
     *
     * @param st     type of statistics(human|robot)
     * @param stats  statistics array
     **/
    public SOCShowStatistics(String st, String[][] stats)
    {
        messageType = SHOWSTATS;
        stype = st;
        statistics = stats;
    }

    /**
     * @return the type of statistics
     **/
    public String getStype()
    {
        return stype;
    }
    
    /**
     * @return the statistics
     **/
    public String[][] getStatistics()
    {
        return statistics;
    }

    /**
     * SHOWSTATS sep stype sep2 statistics
     *
     * @return the command String
     **/
    public String toCmd()
    {
        return toCmd(stype, statistics);
    }

    /**
     * SHOWSTATS sep stype sep2 statistics
     *
     * @param st     the type of statistics
     * @param stats  the statistics array
     * @return       the type and statistics as a string
     **/
    public static String toCmd(String st, String[][] stats)
    {
        int cols = stats[0].length;     // Number of columns in stats
        int rows = stats.length;        // Number of rows in stats
        String[][] statistics = null;
        statistics = stats;
        String data = SHOWSTATS + sep + st;

        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                data += (sep2 + statistics[row][col]);
            }
        }
                
        return data;
    }

    /**
     * Parse the data String into a Show Stats message
     *
     * @param s   the String to parse
     * @return    a Show Stats message, or null of the data is garbled
     */
    public static SOCShowStatistics parseDataStr(String s)
    {
        int cols = 7;
        String st;
        
        StringTokenizer stk = new StringTokenizer(s, sep2);
        String[][] statistics = new String[stk.countTokens()/cols][cols];
        
        try
        {
            int row = 0;
            
            st = stk.nextToken();

            while (stk.hasMoreTokens())
            {
                for (int col = 0; col < 7; col++)
                {
                    //System.err.println("row = " + row + ", col = " + col);
                    statistics[row][col] = (stk.nextToken());
                    //System.err.println("Token = " + statistics[row][col]);
                }
                row ++;
            }
        }
        catch (Exception e)
        {
            System.err.println("SOCShowStatistics.parseDataStr error: " + e);
            return null;
        }

        return new SOCShowStatistics(st, statistics);
    }

    /**
     * @return a human readable form of the message
     */
    public String toString()
    {
        int cols = statistics[0].length;     // Number of columns in stats
        int rows = statistics.length;        // Number of rows in stats
        String s = "SOCShowStatistics:stype=" + stype + "|statistics=";

        try
        {
            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < cols; col++)
                {
                    s += (sep2 + statistics[row][col]);
                }
            }
        }
        catch (Exception e) {}

        return s;
    }
}