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
package soc.client;

import soc.disableDebug.D;

import soc.message.SOCCreateAccount;
import soc.message.SOCMessage;
import soc.message.SOCRejectConnection;
import soc.message.SOCStatusMessage;

import java.applet.Applet;
import java.applet.AppletContext;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;


/**
 * Applet/Standalone client for connecting to the SOCServer and
 * making user accounts.
 * If you want another connection port, you have to specify it as the "port"
 * argument in the html source. If you run this as a stand-alone, you have to
 * specify the port.
 *
 * @author Robert S Thomas
 */
public class SOCAccountClient extends Applet implements Runnable, ActionListener
{
    protected TextField nick;
    protected TextField pass;
    protected TextField pass2;
    protected TextField email;
    protected TextField status;
    protected Button submit;
    protected AppletContext ac;
    protected int bk;
    protected int fg;
    protected boolean submitLock;

    /**
     * true if this is an application
     */
    protected boolean standalone;
    protected String host;
    protected int port;
    protected Socket s;
    protected DataInputStream in;
    protected DataOutputStream out;
    protected Thread reader = null;
    protected Exception ex = null;
    protected boolean connected = false;

    /**
     * the nickname
     */
    protected String nickname = null;

    /**
     * the password
     */
    protected String password = null;

    /**
     * the second password
     */
    protected String password2 = null;

    /**
     * the email address
     */
    protected String emailAddress = null;

    /**
     * Create a SOCAccountClient
     */
    public SOCAccountClient()
    {
        host = null;
        port = 8889;
        standalone = false;
    }

    /**
     * Constructor for connecting to the specified host, on the specified port
     *
     * @param h  host
     * @param p  port
     * @param visual  true if this client is visual
     */
    public SOCAccountClient(String h, int p, boolean visual)
    {
        host = h;
        port = p;
        standalone = true;

        if (visual)
        {
            initVisualElements();
        }
    }

    /**
     * init the visual elements
     */
    protected void initVisualElements()
    {
        nick = new TextField(20);
        pass = new TextField(10);
        pass.setEchoChar('*');
        pass2 = new TextField(10);
        pass2.setEchoChar('*');
        email = new TextField(50);
        status = new TextField(50);
        status.setEditable(false);
        submit = new Button("Create Account");
        submitLock = false;
        bk = -1;
        fg = -1;
    }

    /**
     * Translate a hex string into an integer
     *
     * @param s  String to be converted
     * @return   a hex number represting the color
     */
    static int color(String s)
    {
        if (s == null)
        {
            return -1;
        }

        char[] c = s.trim().toLowerCase().toCharArray();
        int rez = 0;

        if (c.length > 6)
        {
            return -1;
        }

        for (int i = 0; i < c.length; i++)
        {
            rez <<= 4;

            if ((c[i] >= '0') && (c[i] <= '9'))
            {
                rez += (c[i] - '0');
            }
            else if ((c[i] >= 'a') && (c[i] <= 'f'))
            {
                rez += ((10 + c[i]) - 'a');
            }
            else
            {
                return -1;
            }
        }

        return rez;
    }

    /**
     * Initialize the applet
     */
    public synchronized void init()
    {
        initVisualElements();

        try
        {
            ac = getAppletContext();
            System.out.println("SOC Account Client 0.1, (c) 2001 Robb Thomas.");
            System.out.println("Network layer based on code by Cristian Bogdan.");
            bk = color(getParameter("background"));
            fg = color(getParameter("foreground"));
        }
        catch (Exception exc)
        {
            ;
        }

        if (host == null)
        {
            System.out.println("Getting host...");
            host = getCodeBase().getHost();
            System.out.println("HOST = " + host);

            try
            {
                port = Integer.parseInt(getParameter("PORT"));
            }
            catch (Exception e) {}
        }

        if (bk != -1)
        {
            setBackground(new Color(bk));
        }

        if (fg != -1)
        {
            setForeground(new Color(fg));
        }

        try
        {
            s = new Socket(host, port);
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
            connected = true;
            (reader = new Thread(this)).start();
        }
        catch (Exception e)
        {
            ex = e;
            System.err.println("Could not connect to the server: " + ex);
        }

        setFont(new Font("Monaco", Font.PLAIN, 12));

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        Label l;

        if (ex != null)
        {
            l = new Label("Could not connect to the server: " + ex);
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.gridheight = GridBagConstraints.REMAINDER;
            gbl.setConstraints(l, c);
            add(l);

            return;
        }

        if (connected)
        {
            l = new Label("Your Nickname:");
            c.gridwidth = 1;
            gbl.setConstraints(l, c);
            add(l);

            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(nick, c);
            add(nick);

            l = new Label();
            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(l, c);
            add(l);

            l = new Label("Password:");
            c.gridwidth = 1;
            gbl.setConstraints(l, c);
            add(l);

            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(pass, c);
            add(pass);

            l = new Label();
            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(l, c);
            add(l);

            l = new Label("Password (again):");
            c.gridwidth = 1;
            gbl.setConstraints(l, c);
            add(l);

            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(pass2, c);
            add(pass2);

            l = new Label();
            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(l, c);
            add(l);

            l = new Label("Email (optional):");
            c.gridwidth = 1;
            gbl.setConstraints(l, c);
            add(l);

            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(email, c);
            add(email);

            l = new Label();
            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(l, c);
            add(l);

            l = new Label();
            c.gridwidth = 1;
            gbl.setConstraints(l, c);
            add(l);

            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(submit, c);
            add(submit);
            submit.addActionListener(this);

            l = new Label();
            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(l, c);
            add(l);

            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(status, c);
            add(status);

            nick.requestFocus();
            resize(600, 300);
        }
    }

    /**
     * Handle mouse clicks and keyboard
     */
    public void actionPerformed(ActionEvent e)
    {
        Object target = e.getSource();

        if (target == submit)
        {
            String n = nick.getText().trim();

            if (n.length() > 20)
            {
                nickname = n.substring(1, 20);
            }
            else
            {
                nickname = n;
            }

            String p1 = pass.getText().trim();

            if (p1.length() > 20)
            {
                password = p1.substring(1, 20);
            }
            else
            {
                password = p1;
            }

            String p2 = pass2.getText().trim();

            if (p2.length() > 20)
            {
                password2 = p2.substring(1, 20);
            }
            else
            {
                password2 = p2;
            }

            emailAddress = email.getText().trim();

            //
            // make sure all the info is ok
            //
            if (nickname.length() == 0)
            {
                status.setText("You must enter a nickname.");
                nick.requestFocus();
            }
            else if (password.length() == 0)
            {
                status.setText("You must enter a password.");
                pass.requestFocus();
            }
            else if (!password.equals(password2))
            {
                pass.requestFocus();
                status.setText("Your passwords don't match.");
            }
            else if (!submitLock)
            {
                submitLock = true;
                status.setText("Creating account ...");
                put(SOCCreateAccount.toCmd(nickname, password, host, emailAddress));
            }
        }
    }

    /**
     * continuously read from the net in a separate thread
     */
    public void run()
    {
        try
        {
            while (connected)
            {
                String s = in.readUTF();
                treat((SOCMessage) SOCMessage.toMsg(s));
            }
        }
        catch (IOException e)
        {
            if (!connected)
            {
                return;
            }

            ex = e;
            System.out.println("could not read from the net: " + ex);
            destroy();
        }
    }

    /**
     * write a message to the net
     *
     * @param s  the message
     * @return true if the message was sent, false if not
     */
    public synchronized boolean put(String s)
    {
        D.ebugPrintln("OUT - " + s);

        if ((ex != null) || !connected)
        {
            return false;
        }

        try
        {
            out.writeUTF(s);
        }
        catch (IOException e)
        {
            ex = e;
            System.err.println("could not write to the net: " + ex);
            destroy();

            return false;
        }

        return true;
    }

    /**
     * Treat the incoming messages
     *
     * @param mes    the message
     */
    public void treat(SOCMessage mes)
    {
        D.ebugPrintln(mes.toString());

        try
        {
            switch (mes.getType())
            {
            /**
             * status message
             */
            case SOCMessage.STATUSMESSAGE:
                handleSTATUSMESSAGE((SOCStatusMessage) mes);

                break;

            /**
             * handle the reject connection message
             */
            case SOCMessage.REJECTCONNECTION:
                handleREJECTCONNECTION((SOCRejectConnection) mes);

                break;
            }
        }
        catch (Exception e)
        {
            System.out.println("SOCAccountClient treat ERROR - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * handle the "reject connection" message
     * @param mes  the message
     */
    protected void handleREJECTCONNECTION(SOCRejectConnection mes)
    {
        removeAll();
        invalidate();

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        Label l = new Label(mes.getText());
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = GridBagConstraints.REMAINDER;
        gbl.setConstraints(l, c);
        add(l);
        doLayout();
        disconnect();
    }

    /**
     * handle the "status message" message
     * @param mes  the message
     */
    protected void handleSTATUSMESSAGE(SOCStatusMessage mes)
    {
        status.setText(mes.getStatus());
        submitLock = false;
    }

    /**
     * disconnect from the net
     */
    protected synchronized void disconnect()
    {
        connected = false;

        if ((Thread.currentThread() != reader) && (reader != null) && reader.isAlive())
        {
            reader.stop();
        }

        try
        {
            s.close();
        }
        catch (Exception e)
        {
            ex = e;
        }
    }

    /**
     * applet info
     */
    public String getAppletInfo()
    {
        return "SOCAccountClient 0.1 by Robert S. Thomas.";
    }

    /** destroy the applet */
    public void destroy()
    {
        String err = "Sorry, the applet has been destroyed. " + ((ex == null) ? "Load the page again." : ex.toString());

        status.setText(err);
        disconnect();
    }

    /**
     * for stand-alones
     */
    public static void main(String[] args)
    {
        Frame f = new Frame("SOCAccountClient");

        // Add a listener for the close event
        f.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent evt)
                {
                    // Exit the application
                    System.exit(0);
                }
            });

        if (args.length < 2)
        {
            System.err.println("usage: java soc.client.SOCAccountClient host port_number");

            return;
        }

        Applet ex1 = new SOCAccountClient(args[0], Integer.parseInt(args[1]), true);
        ex1.init();
        f.add("Center", ex1);
        f.pack();
        f.setSize(600, 500);
        f.show();
    }

    /**
     * @return true if this is an application
     */
    public boolean isStandalone()
    {
        return standalone;
    }
}
