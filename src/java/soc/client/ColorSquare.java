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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * This is a square box with a background color and
 * possibly a number or X in it.  This box can be
 * interactive, or non-interactive.  The possible
 * colors of the box correspond to resources in SoC.
 *
 * @author Robert S Thomas
 */
public class ColorSquare extends Canvas implements MouseListener
{
    public final static Color CLAY = new Color(204, 102, 102);
    public final static Color ORE = new Color(153, 153, 153);
    public final static Color SHEEP = new Color(51, 204, 51);
    public final static Color WHEAT = new Color(204, 204, 51);
    public final static Color WOOD = new Color(204, 153, 102);
    public final static Color GREY = new Color(204, 204, 204);
    public final static int NUMBER = 0;
    public final static int YES_NO = 1;
    public final static int CHECKBOX = 2;
    public final static int BOUNDED_INC = 3;
    public final static int BOUNDED_DEC = 4;
    public final static int WIDTH = 16;
    public final static int HEIGHT = 16;
    Color color;
    int intValue;
    boolean boolValue;
    boolean valueVis;
    int numW;
    int numH;
    int numA;
    int kind;
    int upperBound;
    int lowerBound;
    boolean interactive;

    /**
     * Creates a new ColorSquare object.
     */
    public ColorSquare()
    {
        super();

        setFont(new Font("Geneva", Font.PLAIN, 10));

        color = GREY;

        valueVis = false;
        intValue = 0;
        kind = NUMBER;
        interactive = false;
    }

    /**
     * Creates a new ColorSquare object.
     *
     * @param c DOCUMENT ME!
     */
    public ColorSquare(Color c)
    {
        super();

        setFont(new Font("Geneva", Font.PLAIN, 10));

        color = c;

        valueVis = false;
        intValue = 0;
        kind = NUMBER;
        interactive = false;

        this.addMouseListener(this);
    }

    /**
     * Creates a new ColorSquare object.
     *
     * @param c DOCUMENT ME!
     * @param v DOCUMENT ME!
     */
    public ColorSquare(Color c, int v)
    {
        super();

        setFont(new Font("Geneva", Font.PLAIN, 10));

        color = c;

        valueVis = true;
        intValue = v;
        kind = NUMBER;
        interactive = false;

        this.addMouseListener(this);
    }

    /**
     * Creates a new ColorSquare object.
     *
     * @param k DOCUMENT ME!
     * @param in DOCUMENT ME!
     * @param c DOCUMENT ME!
     */
    public ColorSquare(int k, boolean in, Color c)
    {
        super();

        setFont(new Font("Geneva", Font.PLAIN, 10));

        color = c;
        kind = k;
        interactive = in;

        switch (k)
        {
        case NUMBER:
            valueVis = true;
            intValue = 0;

            break;

        case YES_NO:
            valueVis = true;
            boolValue = false;

            break;

        case CHECKBOX:
            valueVis = true;
            boolValue = false;

            break;

        case BOUNDED_INC:
            valueVis = true;
            boolValue = false;
            upperBound = 99;
            lowerBound = 0;

            break;

        case BOUNDED_DEC:
            valueVis = true;
            boolValue = false;
            upperBound = 99;
            lowerBound = 0;

            break;
        }

        this.addMouseListener(this);
    }

    /**
     * Creates a new ColorSquare object.
     *
     * @param k DOCUMENT ME!
     * @param in DOCUMENT ME!
     * @param c DOCUMENT ME!
     * @param upper DOCUMENT ME!
     * @param lower DOCUMENT ME!
     */
    public ColorSquare(int k, boolean in, Color c, int upper, int lower)
    {
        super();

        setFont(new Font("Geneva", Font.PLAIN, 10));

        color = c;
        kind = k;
        interactive = in;

        switch (k)
        {
        case NUMBER:
            valueVis = true;
            intValue = 0;

            break;

        case YES_NO:
            valueVis = true;
            boolValue = false;

            break;

        case CHECKBOX:
            valueVis = true;
            boolValue = false;

            break;

        case BOUNDED_INC:
            valueVis = true;
            boolValue = false;
            upperBound = upper;
            lowerBound = lower;

            break;

        case BOUNDED_DEC:
            valueVis = true;
            boolValue = false;
            upperBound = upper;
            lowerBound = lower;

            break;
        }

        this.addMouseListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public void setColor(Color c)
    {
        color = c;
        draw();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getPreferedSize()
    {
        return new Dimension(WIDTH, HEIGHT);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getMinimumSize()
    {
        return new Dimension(WIDTH, HEIGHT);
    }

    /**
     * DOCUMENT ME!
     */
    public void addNotify()
    {
        super.addNotify();
        measure();
    }

    protected void measure()
    {
        FontMetrics fm = this.getFontMetrics(this.getFont());

        if (fm == null)
        {
            return;
        }

        numH = fm.getHeight();

        switch (kind)
        {
        case NUMBER:
        case BOUNDED_INC:
        case BOUNDED_DEC:
            numW = fm.stringWidth(Integer.toString(intValue));

            break;

        case YES_NO:

            if (boolValue)
            {
                numW = fm.stringWidth("Y");
            }
            else
            {
                numW = fm.stringWidth("N");
            }

            break;

        case CHECKBOX:
            break;
        }

        numA = fm.getAscent();
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    public void paint(Graphics g)
    {
        if (g != null)
        {
            g.setPaintMode();
            g.setColor(color);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.black);
            g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

            int x;
            int y;

            if (valueVis)
            {
                switch (kind)
                {
                case NUMBER:
                case BOUNDED_INC:
                case BOUNDED_DEC:
                    x = (WIDTH - numW) / 2;

                    // y = numA + (HEIGHT - numH) / 2; // proper way
                    y = 12; // way that works
                    g.drawString(Integer.toString(intValue), x, y);

                    break;

                case YES_NO:
                    x = (WIDTH - numW) / 2;

                    // y = numA + (HEIGHT - numH) / 2; // proper way
                    y = 12; // way that works

                    if (boolValue)
                    {
                        g.drawString("Y", x, y);
                    }
                    else
                    {
                        g.drawString("N", x, y);
                    }

                    break;

                case CHECKBOX:

                    if (boolValue)
                    {
                        int checkX = WIDTH / 5;
                        int checkY = HEIGHT / 4;
                        g.drawLine(checkX, 2 * checkY, 2 * checkX, 3 * checkY);
                        g.drawLine(2 * checkX, 3 * checkY, 4 * checkX, checkY);
                    }

                    break;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void draw()
    {
        paint(this.getGraphics());
    }

    /**
     * DOCUMENT ME!
     *
     * @param v DOCUMENT ME!
     */
    public void addValue(int v)
    {
        intValue += v;
        measure();
        draw();
    }

    /**
     * DOCUMENT ME!
     *
     * @param v DOCUMENT ME!
     */
    public void subtractValue(int v)
    {
        intValue -= v;
        measure();
        draw();
    }

    /**
     * DOCUMENT ME!
     *
     * @param v DOCUMENT ME!
     */
    public void setIntValue(int v)
    {
        intValue = v;
        measure();
        draw();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getIntValue()
    {
        return intValue;
    }

    /**
     * DOCUMENT ME!
     *
     * @param v DOCUMENT ME!
     */
    public void setBoolValue(boolean v)
    {
        boolValue = v;
        measure();
        draw();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getBoolValue()
    {
        return boolValue;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseEntered(MouseEvent e)
    {
        ;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseExited(MouseEvent e)
    {
        ;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseClicked(MouseEvent e)
    {
        ;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseReleased(MouseEvent e)
    {
        ;
    }

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    public void mousePressed(MouseEvent evt)
    {
        if (interactive)
        {
            switch (kind)
            {
            case YES_NO:
            case CHECKBOX:
                boolValue = !boolValue;
                measure();
                draw();

                break;

            case NUMBER:
                intValue++;
                measure();
                draw();

                break;

            case BOUNDED_INC:

                if (intValue < upperBound)
                {
                    intValue++;
                }

                measure();
                draw();

                break;

            case BOUNDED_DEC:

                if (intValue > lowerBound)
                {
                    intValue--;
                }

                measure();
                draw();

                break;
            }
        }
    }
}
