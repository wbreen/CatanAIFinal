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
import soc.game.SOCResourceSet;
import soc.game.SOCTradeOffer;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TradeOfferPanel extends Panel implements ActionListener
{
    protected static final int[] zero = { 0, 0, 0, 0, 0 };
    static final String OFFER = "counter";
    static final String ACCEPT = "accept";
    static final String REJECT = "reject";
    static final String SEND = "send";
    static final String CLEAR = "clear";
    static final String CANCEL = "cancel";
    static final Color insideBGColor = new Color(255, 230, 162);
    SpeechBalloon balloon;
    Label toWhom1;
    Label toWhom2;
    String names1;
    String names2;
    Label giveLab;
    Label getLab;
    SquaresPanel squares;
    Button offerBut;
    Button acceptBut;
    Button rejectBut;
    ShadowedBox offerBox;
    SquaresPanel offerSquares;
    Label giveLab2;
    Label getLab2;
    Button sendBut;
    Button clearBut;
    Button cancelBut;
    Label msg;
    int from;
    SOCHandPanel hp;
    SOCPlayerInterface pi;
    boolean offered;
    SOCResourceSet give;
    SOCResourceSet get;
    int[] giveInt = new int[5];
    int[] getInt = new int[5];
    boolean counterOfferMode = false;

    /**
     * Creates a new TradeOfferPanel object.
     */
    public TradeOfferPanel()
    {
        ;
    }

    /**
     * Creates a new TradeOfferPanel object.
     *
     * @param hp DOCUMENT ME!
     * @param from DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public TradeOfferPanel(SOCHandPanel hp, int from, String m)
    {
        this.hp = hp;
        pi = hp.getPlayerInterface();
        this.from = from;
        setBackground(pi.getPlayerColor(from));
        setForeground(Color.black);
        setFont(new Font("Helvetica", Font.PLAIN, 18));

        msg = new Label(m, Label.CENTER);
        msg.setBackground(insideBGColor);
        add(msg);
        balloon = new SpeechBalloon(pi.getPlayerColor(from));
        add(balloon);

        offered = false;
        setLayout(null);
    }

    /**
     * Creates a new TradeOfferPanel object.
     *
     * @param hp DOCUMENT ME!
     * @param from DOCUMENT ME!
     * @param give DOCUMENT ME!
     * @param get DOCUMENT ME!
     * @param offerList DOCUMENT ME!
     */
    public TradeOfferPanel(SOCHandPanel hp, int from, SOCResourceSet give, SOCResourceSet get, boolean[] offerList)
    {
        this.hp = hp;
        pi = hp.getPlayerInterface();
        this.from = from;
        this.give = give;
        this.get = get;
        setBackground(pi.getPlayerColor(from));
        setForeground(Color.black);
        setFont(new Font("Helvetica", Font.PLAIN, 10));

        SOCGame ga = hp.getGame();
        SOCPlayer player = hp.getGame().getPlayer(hp.getClient().getNickname());

        if (player != null)
        {
            offered = offerList[player.getPlayerNumber()];
        }
        else
        {
            offered = false;
        }

        int cnt = 0;
        names1 = "Offered to: ";

        for (; cnt < SOCGame.MAXPLAYERS; cnt++)
        {
            if (offerList[cnt])
            {
                names1 += ga.getPlayer(cnt).getName();

                break;
            }
        }

        cnt++;

        int len = names1.length();

        for (; cnt < SOCGame.MAXPLAYERS; cnt++)
        {
            if (offerList[cnt])
            {
                String name = ga.getPlayer(cnt).getName();
                len += name.length();

                if (len < 25)
                {
                    names1 += ", ";
                    names1 += name;
                }
                else
                {
                    if (names2 == null)
                    {
                        names1 += ",";
                        names2 = new String(name);
                    }
                    else
                    {
                        names2 += ", ";
                        names2 += name;
                    }
                }
            }
        }

        toWhom1 = new Label(names1);
        toWhom1.setBackground(insideBGColor);
        add(toWhom1);
        toWhom2 = new Label(names2);
        toWhom2.setBackground(insideBGColor);
        add(toWhom2);
        squares = new SquaresPanel(false);
        add(squares);
        giveLab = new Label("I Give: ");
        giveLab.setBackground(insideBGColor);
        add(giveLab);
        getLab = new Label("I Get: ");
        getLab.setBackground(insideBGColor);
        add(getLab);

        giveInt = new int[5];
        getInt = new int[5];

        /**
         * Note: this only works if SOCResourceConstants.CLAY == 1
         */
        for (int i = 0; i < 5; i++)
        {
            giveInt[i] = give.getAmount(i + 1);
            getInt[i] = get.getAmount(i + 1);
        }

        if (offered)
        {
            acceptBut = new Button("Accept");
            add(acceptBut);
            acceptBut.setActionCommand(ACCEPT);
            acceptBut.addActionListener(this);

            rejectBut = new Button("Reject");
            add(rejectBut);
            rejectBut.setActionCommand(REJECT);
            rejectBut.addActionListener(this);

            offerBut = new Button("Counter");
            add(offerBut);
            offerBut.setActionCommand(OFFER);
            offerBut.addActionListener(this);
        }

        balloon = new SpeechBalloon(pi.getPlayerColor(from));
        add(balloon);

        setLayout(null);
    }

    /**
     * DOCUMENT ME!
     */
    public void doLayout()
    {
        FontMetrics fm = this.getFontMetrics(this.getFont());
        Dimension dim = getSize();
        int w = dim.width;
        int h = dim.height;
        int inset = 10;

        if (w > 175)
        {
            w = 175;
        }

        if (h > 124)
        {
            h = 124;
        }

        int top = (h / 8) + 5;

        if (counterOfferMode)
        {
            // show the counter offer controls
            if (h > 92)
            {
                h = 92;
            }

            top = (h / 8) + 5;

            int lineH = 16;
            int giveW = fm.stringWidth(new String("I Give: ")) + 2;
            int buttonW = 48;
            int buttonH = 18;

            toWhom1.setBounds(inset, top, w - 20, 14);
            toWhom2.setBounds(inset, top + 14, w - 20, 14);
            giveLab.setBounds(inset, top + 32, giveW, lineH);
            getLab.setBounds(inset, top + 32 + lineH, giveW, lineH);
            squares.setLocation(inset + giveW, top + 32);
            squares.doLayout();

            int squaresHeight = squares.getBounds().height + 24;
            giveLab2.setBounds(inset, top + 32 + squaresHeight, giveW, lineH);
            getLab2.setBounds(inset, top + 32 + lineH + squaresHeight, giveW, lineH);
            offerSquares.setLocation(inset + giveW, top + 32 + squaresHeight);
            offerSquares.doLayout();

            sendBut.setBounds(inset, top + 12 + (2 * squaresHeight), buttonW, buttonH);
            clearBut.setBounds(inset + 5 + buttonW, top + 12 + (2 * squaresHeight), buttonW, buttonH);
            cancelBut.setBounds(inset + (2 * (5 + buttonW)), top + 12 + (2 * squaresHeight), buttonW, buttonH);

            balloon.setBounds(0, 0, w, h);
            balloon.repaint();

            offerBox.setBounds(0, top + 22 + squaresHeight, w, squaresHeight + 15);
            offerBox.repaint();
        }
        else
        {
            if (msg != null)
            {
                // This is only a message
                msg.setBounds(inset, ((h - 18) / 2), w - (2 * inset), 18);
                balloon.setBounds(0, 0, w, h);
                balloon.repaint();
            }
            else
            {
                // This is an offer to trade
                int lineH = 16;
                int giveW = fm.stringWidth(new String("I Give: ")) + 2;
                int buttonW = 48;
                int buttonH = 18;

                toWhom1.setBounds(inset, top, w - 20, 14);
                toWhom2.setBounds(inset, top + 14, w - 20, 14);
                giveLab.setBounds(inset, top + 32, giveW, lineH);
                getLab.setBounds(inset, top + 32 + lineH, giveW, lineH);
                squares.setLocation(inset + giveW, top + 32);
                squares.doLayout();

                if (offered)
                {
                    int squaresHeight = squares.getBounds().height + 8;
                    acceptBut.setBounds(inset, top + 32 + squaresHeight, buttonW, buttonH);
                    rejectBut.setBounds(inset + 5 + buttonW, top + 32 + squaresHeight, buttonW, buttonH);
                    offerBut.setBounds(inset + (2 * (5 + buttonW)), top + 32 + squaresHeight, buttonW, buttonH);
                }

                balloon.setBounds(0, 0, w, h);
                balloon.repaint();
                squares.setValues(giveInt, getInt);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e)
    {
        String target = e.getActionCommand();

        if (target == OFFER)
        {
            Color ourPlayerColor = pi.getPlayerColor(hp.getGame().getPlayer(pi.getClient().getNickname()).getPlayerNumber());

            if (sendBut == null)
            {
                sendBut = new Button("Send");
                add(sendBut);
                sendBut.setActionCommand(SEND);
                sendBut.addActionListener(this);
            }
            else
            {
                sendBut.setVisible(true);
            }

            if (clearBut == null)
            {
                clearBut = new Button("Clear");
                add(clearBut);
                clearBut.setActionCommand(CLEAR);
                clearBut.addActionListener(this);
            }
            else
            {
                clearBut.setVisible(true);
            }

            if (cancelBut == null)
            {
                cancelBut = new Button("Cancel");
                add(cancelBut);
                cancelBut.setActionCommand(CANCEL);
                cancelBut.addActionListener(this);
            }
            else
            {
                cancelBut.setVisible(true);
            }

            if (offerSquares == null)
            {
                offerSquares = new SquaresPanel(true);
                add(offerSquares);
            }
            else
            {
                offerSquares.setVisible(true);
            }

            if (giveLab2 == null)
            {
                giveLab2 = new Label("I Give: ");
                giveLab2.setBackground(ourPlayerColor);
                add(giveLab2);
            }
            else
            {
                giveLab2.setVisible(true);
            }

            if (getLab2 == null)
            {
                getLab2 = new Label("I Get: ");
                getLab2.setBackground(ourPlayerColor);
                add(getLab2);
            }
            else
            {
                getLab2.setVisible(true);
            }

            acceptBut.setVisible(false);
            rejectBut.setVisible(false);
            offerBut.setVisible(false);

            if (offerBox == null)
            {
                offerBox = new ShadowedBox(pi.getPlayerColor(from), ourPlayerColor);
                add(offerBox);
            }
            else
            {
                offerBox.setVisible(true);
            }

            counterOfferMode = true;
            doLayout();
        }

        if (target == CLEAR)
        {
            offerSquares.setValues(zero, zero);
        }

        if (target == SEND)
        {
            SOCGame game = hp.getGame();
            SOCPlayer ourPlayerData = game.getPlayer(pi.getClient().getNickname());

            if (game.getGameState() == SOCGame.PLAY1)
            {
                int[] give = new int[5];
                int[] get = new int[5];
                int giveSum = 0;
                int getSum = 0;
                offerSquares.getValues(give, get);

                for (int i = 0; i < 5; i++)
                {
                    giveSum += give[i];
                    getSum += get[i];
                }

                SOCResourceSet giveSet = new SOCResourceSet(give[0], give[1], give[2], give[3], give[4], 0);
                SOCResourceSet getSet = new SOCResourceSet(get[0], get[1], get[2], get[3], get[4], 0);

                if (!ourPlayerData.getResources().contains(giveSet))
                {
                    pi.print("*** You can't offer what you don't have.");
                }
                else if ((giveSum == 0) || (getSum == 0))
                {
                    pi.print("*** A trade must contain at least one resource card from each player.");
                }
                else
                {
                    boolean[] to = new boolean[SOCGame.MAXPLAYERS];

                    for (int i = 0; i < SOCGame.MAXPLAYERS; i++)
                    {
                        to[i] = false;
                    }

                    // offer to the player that made the original offer						
                    to[from] = true;

                    SOCTradeOffer tradeOffer = new SOCTradeOffer(game.getName(), ourPlayerData.getPlayerNumber(), to, giveSet, getSet);
                    hp.getClient().offerTrade(game, tradeOffer);
                    remove(offerBox);
                    remove(getLab2);
                    remove(giveLab2);
                    remove(offerSquares);
                    remove(clearBut);
                    remove(sendBut);
                    remove(cancelBut);
                }
            }
        }

        if (target == CANCEL)
        {
            giveLab2.setVisible(false);
            getLab2.setVisible(false);
            offerSquares.setVisible(false);
            sendBut.setVisible(false);
            clearBut.setVisible(false);
            cancelBut.setVisible(false);
            offerBox.setVisible(false);

            acceptBut.setVisible(true);
            rejectBut.setVisible(true);
            offerBut.setVisible(true);
            counterOfferMode = false;
            doLayout();
        }

        if (target == REJECT)
        {
            rejectBut.setVisible(false);
            hp.getClient().rejectOffer(hp.getGame());
        }

        if (target == ACCEPT)
        {
            int[] tempGive = new int[5];
            int[] tempGet = new int[5];
            squares.getValues(tempGive, tempGet);
            hp.getClient().acceptOffer(hp.getGame(), from);
        }
    }
}
