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

import soc.game.SOCGame;
import soc.game.SOCPlayer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * This is a component that can display a face.
 * When you click on the face, it changes to another face.
 *
 * @author Robert S. Thomas
 */
public class SOCFaceButton extends Canvas implements MouseListener
{
    private static String IMAGEDIR = "soc/client/images";

    /**
     * number of face images
     */
    public static final int NUM_FACES = 74;
    private Image[] images;
    private int currentImageNum;
    private int panelx;
    private int panely;
    private SOCGame game;
    private SOCPlayer player;
    private SOCPlayerClient client;
    private Color pColor;

    /**
     * offscreen buffer
     */
    private Image buffer;

    /**
     * create a new SOCFaceButton
     *
     * @param pi  the interface that this button is attached to
     * @param pn  the number of the player that owns this button
     */
    public SOCFaceButton(SOCPlayerInterface pi, int pn)
    {
        super();

        client = pi.getClient();
        game = pi.getGame();
        player = game.getPlayer(pn);
        pColor = pi.getPlayerColor(pn);

        setBackground(pColor);

        panelx = 40;
        panely = 40;

        images = new Image[NUM_FACES + 1];

        currentImageNum = 1;

        /**
         * set up the mouse listeners
         */
        /**
         * load the images
         */
        if (client.isStandalone())
        {
            images[0] = getToolkit().getImage(IMAGEDIR + "/robot.gif");

            for (int i = 1; i <= NUM_FACES; i++)
            {
                images[i] = getToolkit().getImage(IMAGEDIR + "/face" + i + ".gif");
            }
        }
        else
        {
            images[0] = client.getImage(client.getCodeBase(), IMAGEDIR + "/robot.gif");

            for (int i = 1; i <= NUM_FACES; i++)
            {
                images[i] = client.getImage(client.getCodeBase(), IMAGEDIR + "/face" + i + ".gif");
            }
        }

        this.addMouseListener(this);
    }

    /**
     * set which image is shown
     *
     * @param id  the id for the image
     */
    public void setFace(int id)
    {
        currentImageNum = id;
        forceRedraw();
    }

    /**
     * Needed because it's a component
     */
    public void addNotify()
    {
        super.addNotify();

        if (buffer == null)
        {
            buffer = this.createImage(panelx, panely);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getPreferedSize()
    {
        return new Dimension(panelx, panely);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getMinimumSize()
    {
        return new Dimension(panelx, panely);
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    public void paint(Graphics g)
    {
        g.drawImage(buffer, 0, 0, this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    public void update(Graphics g)
    {
        draw();
        paint(g);
    }

    /**
     * draw method
     */
    public final void draw()
    {
        drawFace(buffer.getGraphics());
    }

    /**
     * force a redraw
     */
    public final void forceRedraw()
    {
        drawFace(buffer.getGraphics());
        buffer.flush();
        paint(this.getGraphics());
    }

    /**
     * draw the face
     */
    public void drawFace(Graphics g)
    {
        g.clearRect(0, 0, panelx, panely);
        g.drawImage(images[currentImageNum], 0, 0, pColor, this);
    }

    /*********************************
     * Handle Events
     *********************************/
    public void mouseClicked(MouseEvent e)
    {
        ;
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
        /**
         * only change the face if it's the owners button
         */
        if (player.getName().equals(client.getNickname()))
        {
            if (evt.getX() < 20)
            {
                /**
                 * if the click is on the left side, decrease the number
                 */
                currentImageNum--;

                if (currentImageNum <= 0)
                {
                    currentImageNum = NUM_FACES - 1;
                }
            }
            else
            {
                /**
                 * if the click is on the right side, increase the number
                 */
                currentImageNum++;

                if (currentImageNum == NUM_FACES)
                {
                    currentImageNum = 1;
                }
            }

            client.changeFace(game, currentImageNum);
            forceRedraw();
        }
    }
}
