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

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.TextField;

import java.util.StringTokenizer;
import java.util.Vector;


/** The chat channel window
 *  @version 2.0 (no GridbagLayout) with textwrapping and customized window
 *  @author <A HREF="http://www.nada.kth.se/~cristi">Cristian Bogdan</A>
 */
public class ChannelFrame extends Frame
{
    public SnippingTextArea ta;
    public TextField tf;
    public java.awt.List lst;
    public Canvas cnvs;
    public int ncols;
    public int npix = 1;
    SOCPlayerClient cc;
    String cname;
    Vector history = new Vector();
    int historyCounter = 1;
    boolean down = false;

    /** build a frame with the given title, belonging to the given applet*/
    public ChannelFrame(String t, SOCPlayerClient ccp)
    {
        super("Channel: " + t);
        setBackground(ccp.getBackground());
        setForeground(ccp.getForeground());

        ta = new SnippingTextArea("", 100);
        tf = new TextField();
        lst = new java.awt.List(0, false);
        cc = ccp;
        cname = t;
        ta.setEditable(false);
        tf.setEditable(false);
        tf.setText("Please wait...");
        cnvs = new Canvas();
        cnvs.resize(5, 200);
        lst.resize(180, 200);
        setFont(new Font("Helvetica", Font.PLAIN, 12));
        add(ta);
        add(cnvs);
        add(lst);
        add(tf);

        setLayout(null);

        resize(650, 340);
        move(200, 200);
        history.addElement("");
    }

    /** add some text*/
    public void print(String s)
    {
        StringTokenizer st = new StringTokenizer(s, " \n", true);
        String row = "";

        while (st.hasMoreElements())
        {
            String tk = st.nextToken();

            if (tk.equals("\n"))
            {
                continue;
            }

            if ((row.length() + tk.length()) > ncols)
            {
                ta.append(row + "\n");
                row = tk;

                continue;
            }

            row += tk;
        }

        if (row.trim().length() > 0)
        {
            ta.append(row + "\n");
        }
    }

    /** an error occured, stop editing */
    public void over(String s)
    {
        tf.setEditable(false);
        tf.setText(s);
    }

    /** start */
    public void began()
    {
        tf.setEditable(true);
        tf.setText("");
    }

    /** add a member to the group */
    public void addMember(String s)
    {
        int i;

        for (i = lst.countItems() - 1; i >= 0; i--)
        {
            if (lst.getItem(i).compareTo(s) < 0)
            {
                break;
            }
        }

        lst.addItem(s, i + 1);
    }

    /** delete a member from the channel */
    public void deleteMember(String s)
    {
        int i;

        for (i = lst.countItems() - 1; i >= 0; i--)
        {
            if (lst.getItem(i).equals(s))
            {
                lst.delItem(i);

                break;
            }
        }
    }

    /** send the message that was just typed in, or start editing a private message */
    public boolean action(Event e, Object o)
    {
        if (e.target == tf)
        {
            String s = tf.getText().trim();

            if (s.length() == 0)
            {
                return super.action(e, o);
            }

            tf.setText("");
            cc.chSend(cname, s + "\n");

            history.setElementAt(s, history.size() - 1);
            history.addElement("");
            historyCounter = 1;

            return true;
        }

        /*
           if(e.target==lst)
             {
               cc.select(cname, (String)o);
               return super.action(e,o);
             }
         */
        return super.action(e, o);
    }

    /** when the window is destroyed, tell the applet to leave the group */
    public boolean handleEvent(Event e)
    {
        if ((e.id == Event.MOUSE_ENTER) && (e.target == cnvs) && !down)
        {
            setCursor(W_RESIZE_CURSOR);

            return false;
        }

        if ((e.id == Event.MOUSE_EXIT) && (e.target == cnvs) && !down)
        {
            setCursor(DEFAULT_CURSOR);

            return false;
        }

        if ((e.id == Event.MOUSE_DOWN) && (e.target == cnvs))
        {
            down = true;

            return false;
        }

        if ((e.id == Event.MOUSE_UP) && (e.target == cnvs))
        {
            setCursor(DEFAULT_CURSOR);

            Dimension d = ta.size();
            int diff = e.x - 5 - d.width;
            d.width += diff;
            ta.resize(d);
            ncols = (int) ((((float) d.width) * 100.0) / ((float) npix)) - 2;

            d = lst.size();
            d.width -= diff;
            lst.resize(d);

            Point p = cnvs.location();
            p.x += diff;
            cnvs.move(p.x, p.y);

            p = lst.location();
            p.x += diff;
            lst.move(p.x, p.y);

            down = false;

            return false;
        }

        int hs = history.size();

        if ((e.id == Event.KEY_ACTION) && (e.target == tf))
        {
            if ((e.key == Event.UP) && (hs > historyCounter))
            {
                if (historyCounter == 1)
                {
                    history.setElementAt(tf.getText(), hs - 1);
                }

                historyCounter++;
                tf.setText((String) history.elementAt(hs - historyCounter));
            }
            else if ((e.key == Event.DOWN) && (historyCounter > 1))
            {
                historyCounter--;
                tf.setText((String) history.elementAt(hs - historyCounter));
            }
            else
            {
                ;
            }
        }
        else if (e.id == Event.WINDOW_DESTROY)
        {
            cc.leaveChannel(cname);
            dispose();
        }

        return super.handleEvent(e);
    }

    /**
     * DOCUMENT ME!
     */
    public void layout()
    {
        Insets i = insets();
        Dimension dim = size();
        dim.width -= (i.left + i.right);
        dim.height -= (i.top + i.bottom);

        int h = dim.height - 30;
        int lw = lst.size().width;
        int cw = cnvs.size().width;
        int w = dim.width - lw - cw;

        tf.resize(dim.width, 30);
        tf.move(i.left, i.top + h);

        ta.resize(w, h);
        ta.move(i.left, i.top);

        cnvs.resize(cw, h);
        cnvs.move(i.left + w, i.top);

        lst.resize(lw, h);
        lst.move(w + cw + i.left, i.top);

        ncols = (int) ((((float) w) * 100.0) / ((float) npix)) - 2;
    }

    /**
     * DOCUMENT ME!
     */
    public void init()
    {
        pack();
        resize(640, 480);
        show();
        npix = ta.preferredSize(100, 100).width;
        ncols = (int) ((((float) ta.size().width) * 100.0) / ((float) npix)) - 2;
        lst.resize(npix / 4, lst.size().height);
        layout();
    }
}
