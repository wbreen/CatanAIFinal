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

import soc.game.SOCDevCardConstants;
import soc.game.SOCDevCardSet;
import soc.game.SOCGame;
import soc.game.SOCPlayer;
import soc.game.SOCPlayingPiece;
import soc.game.SOCResourceConstants;
import soc.game.SOCResourceSet;
import soc.game.SOCTradeOffer;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * This panel displays a player's information.
 * If the player is us, then more information is
 * displayed than in another player's hand panel.
 */
public class SOCHandPanel extends Panel implements ActionListener
{
    public static final int ROADS = 0;
    public static final int SETTLEMENTS = 1;
    public static final int CITIES = 2;
    public static final int NUMRESOURCES = 3;
    public static final int NUMDEVCARDS = 4;
    public static final int NUMKNIGHTS = 5;
    public static final int VICTORYPOINTS = 6;
    public static final int LONGESTROAD = 7;
    public static final int LARGESTARMY = 8;
    public static final int CLAY = 9;
    public static final int ORE = 10;
    public static final int SHEEP = 11;
    public static final int WHEAT = 12;
    public static final int WOOD = 13;
    protected static final int[] zero = { 0, 0, 0, 0, 0 };
    protected static final String SIT = "Sit Here";
    protected static final String START = "Start Game";
    protected static final String ROBOT = "Robot";
    protected static final String TAKEOVER = "Take Over";
    protected static final String LOCKSEAT = "Lock";
    protected static final String UNLOCKSEAT = "Unlock";
    protected static final String ROLL = "Roll";
    protected static final String QUIT = "Quit";
    protected static final String DONE = "Done";
    protected static final String CLEAR = "Clear";
    protected static final String SEND = "Send";
    protected static final String BANK = "Bank/Port";
    protected static final String CARD = "  Play Card  ";
    protected Button sitBut;
    protected Button robotBut;
    protected Button startBut;
    protected Button takeOverBut;
    protected Button seatLockBut;
    protected SOCFaceButton faceImg;
    protected Label pname;
    protected Label vpLab;
    protected ColorSquare vpSq;
    protected Label larmyLab;
    protected Label lroadLab;
    protected boolean larmy;
    protected boolean lroad;
    protected ColorSquare claySq;
    protected ColorSquare oreSq;
    protected ColorSquare sheepSq;
    protected ColorSquare wheatSq;
    protected ColorSquare woodSq;
    protected Label clayLab;
    protected Label oreLab;
    protected Label sheepLab;
    protected Label wheatLab;
    protected Label woodLab;
    protected ColorSquare settlementSq;
    protected ColorSquare citySq;
    protected ColorSquare roadSq;
    protected Label settlementLab;
    protected Label cityLab;
    protected Label roadLab;
    protected ColorSquare resourceSq;
    protected Label resourceLab;
    protected ColorSquare developmentSq;
    protected Label developmentLab;
    protected ColorSquare knightsSq;
    protected Label knightsLab;
    protected Label cardLab;
    protected List cardList;
    protected Button playCardBut;
    protected SquaresPanel sqPanel;
    protected Label giveLab;
    protected Label getLab;
    protected Button sendBut;
    protected Button clearBut;
    protected Button bankBut;
    protected ColorSquare[] playerSend;
    protected Button rollBut;
    protected Button doneBut;
    protected Button quitBut;
    protected SOCPlayerInterface playerInterface;
    protected SOCPlayerClient client;
    protected SOCGame game;
    protected SOCPlayer player;
    protected boolean inPlay;
    protected int[] playerSendMap;
    protected TradeOfferPanel offer;

    /**
     * When this flag is true, the panel is interactive.
     */
    protected boolean interactive;

    /**
     * make a new hand panel
     *
     * @param pi  the interface that this panel is a part of
     * @param pl  the player associated with this panel
     * @param in  the interactive flag setting
     */
    public SOCHandPanel(SOCPlayerInterface pi, SOCPlayer pl, boolean in)
    {
        super();
        creation(pi, pl, in);
    }

    /**
     * make a new hand panel
     *
     * @param pi  the interface that this panel is a part of
     * @param pl  the player associated with this panel
     */
    public SOCHandPanel(SOCPlayerInterface pi, SOCPlayer pl)
    {
        super();
        creation(pi, pl, true);
    }

    /**
     * Stuff to do when a SOCHandPanel is created
     *
     * @param pi   player interface
     * @param pl   the player data
     * @param in   the interactive flag setting
     */
    protected void creation(SOCPlayerInterface pi, SOCPlayer pl, boolean in)
    {
        playerInterface = pi;
        client = pi.getClient();
        game = pi.getGame();
        player = pl;
        interactive = in;

        setBackground(playerInterface.getPlayerColor(player.getPlayerNumber()));
        setForeground(Color.black);
        setFont(new Font("Helvetica", Font.PLAIN, 10));

        vpSq = null;
        larmyLab = null;
        lroadLab = null;
        claySq = null;
        oreSq = null;
        sheepSq = null;
        wheatSq = null;
        woodSq = null;
        settlementSq = null;
        roadSq = null;
        citySq = null;
        resourceSq = null;
        developmentSq = null;
        knightsSq = null;
        cardList = null;
        seatLockBut = null;

        larmy = false;
        lroad = false;

        /**
         * player hasn't sat down yet
         */
        inPlay = false;

        setLayout(null);
    }

    /**
     * @return the player interface
     */
    public SOCPlayerInterface getPlayerInterface()
    {
        return playerInterface;
    }

    /**
     * @return the player
     */
    public SOCPlayer getPlayer()
    {
        return player;
    }

    /**
     * @return the client
     */
    public SOCPlayerClient getClient()
    {
        return client;
    }

    /**
     * @return the game
     */
    public SOCGame getGame()
    {
        return game;
    }

    /**
     * handle interaction
     */
    public void actionPerformed(ActionEvent e)
    {
        String target = e.getActionCommand();

        SOCPlayerClient client = playerInterface.getClient();
        SOCGame game = playerInterface.getGame();

        if (target == LOCKSEAT)
        {
            client.lockSeat(game, player.getPlayerNumber());
        }
        else if (target == UNLOCKSEAT)
        {
            client.unlockSeat(game, player.getPlayerNumber());
        }
        else if (target == TAKEOVER)
        {
            client.sitDown(game, player.getPlayerNumber());
        }
        else if (target == SIT)
        {
            client.sitDown(game, player.getPlayerNumber());
        }
        else if (target == START)
        {
            client.startGame(game);
        }
        else if (target == ROBOT)
        {
            // cf.cc.addRobot(cf.cname, playerNum);
        }
        else if (target == ROLL)
        {
            client.rollDice(game);
        }
        else if (target == QUIT)
        {
            playerInterface.leaveGame();
        }
        else if (target == DONE)
        {
            // sqPanel.setValues(zero, zero);
            client.endTurn(game);
        }
        else if (target == CLEAR)
        {
            sqPanel.setValues(zero, zero);

            if (game.getGameState() == SOCGame.PLAY1)
            {
                client.clearOffer(game);
            }
        }
        else if (target == BANK)
        {
            if (game.getGameState() == SOCGame.PLAY1)
            {
                int[] give = new int[5];
                int[] get = new int[5];
                sqPanel.getValues(give, get);
                client.clearOffer(game);

                SOCResourceSet giveSet = new SOCResourceSet(give[0], give[1], give[2], give[3], give[4], 0);
                SOCResourceSet getSet = new SOCResourceSet(get[0], get[1], get[2], get[3], get[4], 0);
                client.bankTrade(game, giveSet, getSet);
            }
        }
        else if (target == SEND)
        {
            if (game.getGameState() == SOCGame.PLAY1)
            {
                int[] give = new int[5];
                int[] get = new int[5];
                int giveSum = 0;
                int getSum = 0;
                sqPanel.getValues(give, get);

                for (int i = 0; i < 5; i++)
                {
                    giveSum += give[i];
                    getSum += get[i];
                }

                SOCResourceSet giveSet = new SOCResourceSet(give[0], give[1], give[2], give[3], give[4], 0);
                SOCResourceSet getSet = new SOCResourceSet(get[0], get[1], get[2], get[3], get[4], 0);

                if (!player.getResources().contains(giveSet))
                {
                    playerInterface.print("*** You can't offer what you don't have.");
                }
                else if ((giveSum == 0) || (getSum == 0))
                {
                    playerInterface.print("*** A trade must contain at least one resource card from each player.");
                }
                else
                {
                    boolean[] to = new boolean[SOCGame.MAXPLAYERS];

                    for (int i = 0; i < SOCGame.MAXPLAYERS; i++)
                    {
                        to[i] = false;
                    }

                    if (game.getCurrentPlayerNumber() == player.getPlayerNumber())
                    {
                        for (int i = 0; i < (SOCGame.MAXPLAYERS - 1); i++)
                        {
                            if (playerSend[i].getBoolValue())
                            {
                                to[playerSendMap[i]] = true;
                            }
                        }
                    }
                    else
                    {
                        // can only offer to current player 
                        to[game.getCurrentPlayerNumber()] = true;
                    }

                    SOCTradeOffer tradeOffer = new SOCTradeOffer(game.getName(), player.getPlayerNumber(), to, giveSet, getSet);
                    client.offerTrade(game, tradeOffer);
                }
            }
        }
        else if ((e.getSource() == cardList) || (target == CARD))
        {
            String item;
            int itemNum;

            try
            {
                item = cardList.getSelectedItem();
                itemNum = cardList.getSelectedIndex();
            }
            catch (NullPointerException ex)
            {
                return;
            }

            if (item.length() == 0)
            {
                return;
            }

            if (game.getCurrentPlayerNumber() == player.getPlayerNumber())
            {
                if (item.equals("Knight"))
                {
                    if (game.canPlayKnight(player.getPlayerNumber()))
                    {
                        client.playDevCard(game, SOCDevCardConstants.KNIGHT);
                    }
                }
                else if (item.equals("Road Building"))
                {
                    if (game.canPlayRoadBuilding(player.getPlayerNumber()))
                    {
                        client.playDevCard(game, SOCDevCardConstants.ROADS);
                    }
                }
                else if (item.equals("Discovery"))
                {
                    if (game.canPlayDiscovery(player.getPlayerNumber()))
                    {
                        client.playDevCard(game, SOCDevCardConstants.DISC);
                    }
                }
                else if (item.equals("Monopoly"))
                {
                    if (game.canPlayMonopoly(player.getPlayerNumber()))
                    {
                        client.playDevCard(game, SOCDevCardConstants.MONO);
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void addSeatLockBut()
    {
        D.ebugPrintln("*** addSeatLockBut() ***");
        D.ebugPrintln("seatLockBut = " + seatLockBut);

        if (seatLockBut == null)
        {
            if (game.isSeatLocked(player.getPlayerNumber()))
            {
                seatLockBut = new Button(UNLOCKSEAT);
            }
            else
            {
                seatLockBut = new Button(LOCKSEAT);
            }

            add(seatLockBut);

            if (interactive)
            {
                seatLockBut.addActionListener(this);
            }

            D.ebugPrintln("added");
        }
        else
        {
            seatLockBut.setVisible(true);
            D.ebugPrintln("visable");
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void addTakeOverBut()
    {
        takeOverBut = new Button("Take over");
        add(takeOverBut);

        if (interactive)
        {
            takeOverBut.addActionListener(this);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void addSitButton()
    {
        sitBut = new Button("Sit Here");
        add(sitBut);

        if (interactive)
        {
            sitBut.addActionListener(this);
        }

        doLayout();
    }

    /**
     * DOCUMENT ME!
     */
    public void addRobotButton()
    {
        robotBut = new Button("Robot");
        add(robotBut);

        if (interactive)
        {
            robotBut.addActionListener(this);
        }

        doLayout();
    }

    /**
     * Change the face image
     *
     * @param id  the id of the image
     */
    public void changeFace(int id)
    {
        if (faceImg != null)
        {
            faceImg.setFace(id);
        }
    }

    /**
     * remove this player
     */
    public void removePlayer()
    {
        //D.ebugPrintln("REMOVE PLAYER");
        //D.ebugPrintln("NAME = "+player.getName());
        remove(vpLab);
        remove(vpSq);
        remove(faceImg);
        remove(pname);
        remove(roadSq);
        remove(roadLab);
        remove(settlementLab);
        remove(settlementSq);
        remove(cityLab);
        remove(citySq);
        remove(knightsSq);
        remove(knightsLab);

        if ((offer != null) && (offer.msg != null))
        {
            remove(offer);
            offer = null;
        }

        if (larmy)
        {
            remove(larmyLab);
        }

        if (lroad)
        {
            remove(lroadLab);
        }

        if (player.getName().equals(client.getNickname()))
        {
            /* This is our hand */
            remove(claySq);
            remove(clayLab);
            remove(oreSq);
            remove(oreLab);
            remove(sheepSq);
            remove(sheepLab);
            remove(wheatSq);
            remove(wheatLab);
            remove(woodSq);
            remove(woodLab);
            remove(cardLab);
            remove(cardList);
            remove(playCardBut);
            remove(giveLab);
            remove(getLab);
            remove(sqPanel);
            remove(sendBut);
            remove(clearBut);
            remove(bankBut);

            for (int i = 0; i < (SOCGame.MAXPLAYERS - 1); i++)
            {
                remove(playerSend[i]);
            }

            remove(rollBut);
            remove(doneBut);
            remove(quitBut);
        }
        else
        {
            /*
             * other player's hand
             */
            remove(resourceLab);
            remove(resourceSq);
            remove(developmentLab);
            remove(developmentSq);
        }

        removeTakeOverBut();
        removeSeatLockBut();

        inPlay = false;

        doLayout();
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void addPlayer(String name)
    {
        /* when the player sits add this stuff */
        larmyLab = new Label("L. Army", Label.CENTER);
        larmyLab.setForeground(new Color(142, 45, 10));
        lroadLab = new Label("L. Road", Label.CENTER);
        lroadLab.setForeground(new Color(142, 45, 10));

        faceImg = new SOCFaceButton(playerInterface, player.getPlayerNumber());

        add(faceImg);

        pname = new Label(name);
        pname.setFont(new Font("Serif", Font.PLAIN, 14));
        add(pname);

        roadSq = new ColorSquare(ColorSquare.GREY, 0);
        add(roadSq);
        roadLab = new Label("Roads:");
        add(roadLab);

        settlementSq = new ColorSquare(ColorSquare.GREY, 0);
        add(settlementSq);
        settlementLab = new Label("Stlmts:");
        add(settlementLab);

        citySq = new ColorSquare(ColorSquare.GREY, 0);
        add(citySq);
        cityLab = new Label("Cities:");
        add(cityLab);

        knightsLab = new Label("Knights: ");
        add(knightsLab);
        knightsSq = new ColorSquare(ColorSquare.GREY, 0);
        add(knightsSq);

        //if (true) {
        if (player.getName().equals(client.getNickname()))
        {
            /* This is our hand */
            if (game.getGameState() != SOCGame.NEW)
            {
                vpLab = new Label("Points: ");
                add(vpLab);
                vpSq = new ColorSquare(ColorSquare.GREY, 0);
                add(vpSq);
            }

            claySq = new ColorSquare(ColorSquare.CLAY, 0);
            add(claySq);
            clayLab = new Label("Clay:");
            add(clayLab);

            oreSq = new ColorSquare(ColorSquare.ORE, 0);
            add(oreSq);
            oreLab = new Label("Ore:");
            add(oreLab);

            sheepSq = new ColorSquare(ColorSquare.SHEEP, 0);
            add(sheepSq);
            sheepLab = new Label("Sheep:");
            add(sheepLab);

            wheatSq = new ColorSquare(ColorSquare.WHEAT, 0);
            add(wheatSq);
            wheatLab = new Label("Wheat:");
            add(wheatLab);

            woodSq = new ColorSquare(ColorSquare.WOOD, 0);
            add(woodSq);
            woodLab = new Label("Wood:");
            add(woodLab);

            cardLab = new Label("Cards:");
            add(cardLab);

            cardList = new List(0, false);
            add(cardList);

            playCardBut = new Button(CARD);
            add(playCardBut);

            if (interactive)
            {
                playCardBut.addActionListener(this);
            }

            giveLab = new Label("I Give: ");
            add(giveLab);
            getLab = new Label("I Get: ");
            add(getLab);
            sqPanel = new SquaresPanel(interactive);
            add(sqPanel);
            sendBut = new Button(SEND);
            add(sendBut);

            if (interactive)
            {
                sendBut.addActionListener(this);
            }

            clearBut = new Button(CLEAR);
            add(clearBut);

            if (interactive)
            {
                clearBut.addActionListener(this);
            }

            bankBut = new Button(BANK);
            add(bankBut);

            if (interactive)
            {
                bankBut.addActionListener(this);
            }

            playerSend = new ColorSquare[3];
            playerSendMap = new int[3];

            int cnt = 0;

            for (int pn = 0; pn < SOCGame.MAXPLAYERS; pn++)
            {
                SOCPlayer pl = game.getPlayer(pn);

                if ((pl.getName() == null) || (!pl.getName().equals(client.getNickname())))
                {
                    playerSend[cnt] = new ColorSquare(ColorSquare.CHECKBOX, true, playerInterface.getPlayerColor(pn));
                    playerSendMap[cnt] = pn;
                    add(playerSend[cnt]);
                    playerSend[cnt].setBoolValue(true);
                    cnt++;
                }
            }

            rollBut = new Button(ROLL);
            add(rollBut);

            if (interactive)
            {
                rollBut.addActionListener(this);
            }

            doneBut = new Button(DONE);
            add(doneBut);

            if (interactive)
            {
                doneBut.addActionListener(this);
            }

            quitBut = new Button(QUIT);
            add(quitBut);

            if (interactive)
            {
                quitBut.addActionListener(this);
            }

            // Remove all of the sit and take over buttons. 
            for (int i = 0; i < SOCGame.MAXPLAYERS; i++)
            {
                playerInterface.getPlayerHandPanel(i).removeSitBut();
                playerInterface.getPlayerHandPanel(i).removeTakeOverBut();
            }

            // If we haven't started yet, add the 'Start' button.
            if (game.getGameState() == SOCGame.NEW)
            {
                startBut = new Button(START);
                add(startBut);

                if (interactive)
                {
                    startBut.addActionListener(this);
                }
            }
        }
        else
        {
            /* This is another player's hand */
            D.ebugPrintln("**** SOCHandPanel.addPlayer(name) ****");
            D.ebugPrintln("player.getPlayerNumber() = " + player.getPlayerNumber());
            D.ebugPrintln("player.isRobot() = " + player.isRobot());
            D.ebugPrintln("game.isSeatLocked(" + player.getPlayerNumber() + ") = " + game.isSeatLocked(player.getPlayerNumber()));
            D.ebugPrintln("game.getPlayer(client.getNickname()) = " + game.getPlayer(client.getNickname()));

            if (player.isRobot() && (game.getPlayer(client.getNickname()) == null) && (!game.isSeatLocked(player.getPlayerNumber())))
            {
                addTakeOverBut();
            }

            if (player.isRobot() && (game.getPlayer(client.getNickname()) != null))
            {
                addSeatLockBut();
            }
            else
            {
                removeSeatLockBut();
            }

            vpLab = new Label("Points: ");
            add(vpLab);
            vpSq = new ColorSquare(ColorSquare.GREY, 0);
            add(vpSq);

            resourceLab = new Label("Resources: ");
            add(resourceLab);
            resourceSq = new ColorSquare(ColorSquare.GREY, 0);
            add(resourceSq);

            developmentLab = new Label("Dev. Cards: ");
            add(developmentLab);
            developmentSq = new ColorSquare(ColorSquare.GREY, 0);
            add(developmentSq);

            removeSitBut();
            removeRobotBut();
        }

        inPlay = true;

        doLayout();
    }

    /**
     * DOCUMENT ME!
     */
    public void updateDevCards()
    {
        if (cardList != null)
        {
            int i;
            SOCDevCardSet cards = player.getDevCards();

            cardList.removeAll();

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.OLD, SOCDevCardConstants.DISC);
                    i++)
            {
                cardList.add("Discovery");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.NEW, SOCDevCardConstants.DISC);
                    i++)
            {
                cardList.add("*NEW* Discovery");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.OLD, SOCDevCardConstants.KNIGHT);
                    i++)
            {
                cardList.add("Knight");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.NEW, SOCDevCardConstants.KNIGHT);
                    i++)
            {
                cardList.add("*NEW* Knight");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.OLD, SOCDevCardConstants.MONO);
                    i++)
            {
                cardList.add("Monopoly");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.NEW, SOCDevCardConstants.MONO);
                    i++)
            {
                cardList.add("*NEW* Monopoly");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.OLD, SOCDevCardConstants.ROADS);
                    i++)
            {
                cardList.add("Road Building");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.NEW, SOCDevCardConstants.ROADS);
                    i++)
            {
                cardList.add("*NEW* Road Building");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.OLD, SOCDevCardConstants.CAP);
                    i++)
            {
                cardList.add("Capitol (1VP)");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.NEW, SOCDevCardConstants.CAP);
                    i++)
            {
                cardList.add("Capitol (1VP)");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.OLD, SOCDevCardConstants.LIB);
                    i++)
            {
                cardList.add("Library (1VP)");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.NEW, SOCDevCardConstants.LIB);
                    i++)
            {
                cardList.add("Library (1VP)");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.OLD, SOCDevCardConstants.TEMP);
                    i++)
            {
                cardList.add("Temple (1VP)");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.NEW, SOCDevCardConstants.TEMP);
                    i++)
            {
                cardList.add("Temple (1VP)");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.OLD, SOCDevCardConstants.TOW);
                    i++)
            {
                cardList.add("Tower (1VP)");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.NEW, SOCDevCardConstants.TOW);
                    i++)
            {
                cardList.add("Tower (1VP)");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.OLD, SOCDevCardConstants.UNIV);
                    i++)
            {
                cardList.add("University (1VP)");
            }

            for (i = 0;
                    i < cards.getAmount(SOCDevCardSet.NEW, SOCDevCardConstants.UNIV);
                    i++)
            {
                cardList.add("University (1VP)");
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void removeSeatLockBut()
    {
        if (seatLockBut != null)
        {
            seatLockBut.setVisible(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void removeTakeOverBut()
    {
        if (takeOverBut != null)
        {
            remove(takeOverBut);
            takeOverBut.removeActionListener(this);
            takeOverBut = null;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void removeSitBut()
    {
        if (sitBut != null)
        {
            remove(sitBut);
            sitBut.removeActionListener(this);
            sitBut = null;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void removeRobotBut()
    {
        if (robotBut != null)
        {
            remove(robotBut);
            robotBut.removeActionListener(this);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void removeStartBut()
    {
        vpLab = new Label("Points: ");
        add(vpLab);
        vpSq = new ColorSquare(ColorSquare.GREY, 0);
        add(vpSq);

        remove(startBut);
        startBut.removeActionListener(this);
        doLayout();
    }

    /**
     * DOCUMENT ME!
     */
    public void updateCurrentOffer()
    {
        if (inPlay)
        {
            if (offer != null)
            {
                remove(offer);
                offer = null;
                doLayout();
            }

            SOCTradeOffer currentOffer = player.getCurrentOffer();

            if (currentOffer != null)
            {
                offer = new TradeOfferPanel(this, player.getPlayerNumber(), currentOffer.getGiveSet(), currentOffer.getGetSet(), currentOffer.getTo());
                add(offer);
                doLayout();
            }
            else
            {
                clearOffer();
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void rejectOffer()
    {
        if (offer != null)
        {
            remove(offer);
            offer = null;
            doLayout();
        }

        offer = new TradeOfferPanel(this, player.getPlayerNumber(), "No thanks.");
        add(offer);
        doLayout();
    }

    /**
     * DOCUMENT ME!
     */
    public void clearTradeMsg()
    {
        if ((offer != null) && (offer.msg != null))
        {
            remove(offer);
            offer = null;
            doLayout();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void clearOffer()
    {
        if (offer != null)
        {
            remove(offer);
            offer = null;
            doLayout();
        }

        if (player.getName().equals(client.getNickname()))
        {
            // clear the squares panel
            sqPanel.setValues(zero, zero);

            // reset the send squares
            for (int i = 0; i < 3; i++)
            {
                playerSend[i].setBoolValue(true);
            }
        }
    }

    /**
     * update the takeover button so that it only
     * allows takover when it's not the robot's turn
     */
    public void updateTakeOverButton()
    {
        if (takeOverBut != null)
        {
            if ((!game.isSeatLocked(player.getPlayerNumber())) && (game.getCurrentPlayerNumber() != player.getPlayerNumber()))
            {
                takeOverBut.setLabel(TAKEOVER);
            }
            else
            {
                takeOverBut.setLabel("* Seat Locked *");
            }
        }
    }

    /**
     * update the seat lock button so that it
     * allows a player to lock an unlocked seat
     * and vice versa
     */
    public void updateSeatLockButton()
    {
        if (seatLockBut != null)
        {
            if (game.isSeatLocked(player.getPlayerNumber()))
            {
                seatLockBut.setLabel(UNLOCKSEAT);
            }
            else
            {
                seatLockBut.setLabel(LOCKSEAT);
            }
        }
    }

    /**
     * turn the "largest army" label on or off
     *
     * @param haveIt  true if this player has the largest army
     */
    protected void setLArmy(boolean haveIt)
    {
        if (haveIt != larmy)
        {
            if (haveIt)
            {
                add(larmyLab);
            }
            else
            {
                remove(larmyLab);
            }

            larmy = haveIt;
        }

        doLayout();
    }

    /**
     * turn the "longest road" label on or off
     *
     * @param haveIt  true if this player has the longest road
     */
    protected void setLRoad(boolean haveIt)
    {
        if (haveIt != lroad)
        {
            if (haveIt)
            {
                add(lroadLab);
            }
            else
            {
                remove(lroadLab);
            }

            lroad = haveIt;
        }

        doLayout();
    }

    /**
     * update the value of a player element
     *
     * @param vt  the type of value
     */
    public void updateValue(int vt)
    {
        /**
         * We say that we're getting the total vp, but
         * for other players this will automatically get
         * the public vp because we will assume their
         * dev card vp total is zero.
         */
        switch (vt)
        {
        case VICTORYPOINTS:

            if (vpSq != null)
            {
                vpSq.setIntValue(player.getTotalVP());
            }

            break;

        case LONGESTROAD:

            if (lroadLab != null)
            {
                setLRoad(player.hasLongestRoad());
                doLayout();
            }

            break;

        case LARGESTARMY:

            if (larmyLab != null)
            {
                setLArmy(player.hasLargestArmy());
                doLayout();
            }

            break;

        case CLAY:

            if (claySq != null)
            {
                claySq.setIntValue(player.getResources().getAmount(SOCResourceConstants.CLAY));
            }

            break;

        case ORE:

            if (oreSq != null)
            {
                oreSq.setIntValue(player.getResources().getAmount(SOCResourceConstants.ORE));
            }

            break;

        case SHEEP:

            if (sheepSq != null)
            {
                sheepSq.setIntValue(player.getResources().getAmount(SOCResourceConstants.SHEEP));
            }

            break;

        case WHEAT:

            if (wheatSq != null)
            {
                wheatSq.setIntValue(player.getResources().getAmount(SOCResourceConstants.WHEAT));
            }

            break;

        case WOOD:

            if (woodSq != null)
            {
                woodSq.setIntValue(player.getResources().getAmount(SOCResourceConstants.WOOD));
            }

            break;

        case NUMRESOURCES:

            if (resourceSq != null)
            {
                resourceSq.setIntValue(player.getResources().getTotal());
            }

            break;

        case ROADS:

            if (roadSq != null)
            {
                roadSq.setIntValue(player.getNumPieces(SOCPlayingPiece.ROAD));
            }

            break;

        case SETTLEMENTS:

            if (settlementSq != null)
            {
                settlementSq.setIntValue(player.getNumPieces(SOCPlayingPiece.SETTLEMENT));
            }

            break;

        case CITIES:

            if (citySq != null)
            {
                citySq.setIntValue(player.getNumPieces(SOCPlayingPiece.CITY));
            }

            break;

        case NUMDEVCARDS:

            if (developmentSq != null)
            {
                developmentSq.setIntValue(player.getDevCards().getTotal());
            }

            break;

        case NUMKNIGHTS:

            if (knightsSq != null)
            {
                knightsSq.setIntValue(player.getNumKnights());
            }

            break;
        }

        // doLayout();
    }

    /**
     * DOCUMENT ME!
     */
    public void doLayout()
    {
        Dimension dim = getSize();
        int inset = 8;
        int space = 2;

        if (!inPlay)
        {
            /* just show the 'sit' button */
            /* and the 'robot' button     */
            if (sitBut != null)
            {
                sitBut.setBounds((dim.width - 60) / 2, (dim.height - 82) / 2, 60, 40);
            }
        }
        else
        {
            FontMetrics fm = this.getFontMetrics(this.getFont());
            int lineH = ColorSquare.HEIGHT;
            int stlmtsW = fm.stringWidth(new String("Stlmts: "));
            int knightsW = fm.stringWidth(new String("Knights: "));
            int faceW = 40;
            int pnameW = dim.width - (inset + faceW + inset + inset);

            faceImg.setBounds(inset, inset, faceW, faceW);
            faceImg.draw();
            pname.setBounds(inset + faceW + inset, inset, pnameW, lineH);

            //if (true) {
            if (player.getName().equals(client.getNickname()))
            {
                /* This is our hand */
                sqPanel.doLayout();

                Dimension sqpDim = sqPanel.getSize();
                int sheepW = fm.stringWidth(new String("Sheep: "));
                int pcW = fm.stringWidth(new String(CARD));
                int giveW = fm.stringWidth(new String("I Give: "));
                int clearW = fm.stringWidth(new String(CLEAR));
                int bankW = fm.stringWidth(new String(BANK));
                int cardsH = 5 * (lineH + space);
                int tradeH = sqpDim.height + space + (2 * (lineH + space));
                int sectionSpace = (dim.height - (inset + faceW + cardsH + tradeH + lineH + inset)) / 3;
                int tradeY = inset + faceW + sectionSpace;
                int cardsY = tradeY + tradeH + sectionSpace;

                // If we haven't started yet, show the 'Start' button.
                if (game.getGameState() == SOCGame.NEW)
                {
                    startBut.setBounds(inset + faceW + inset, inset + lineH + space, dim.width - (inset + faceW + inset + inset), lineH);
                }
                else
                {
                    int vpW = fm.stringWidth(vpLab.getText());
                    vpLab.setBounds(inset + faceW + inset, (inset + faceW) - lineH, vpW, lineH);
                    vpSq.setBounds(inset + faceW + inset + vpW + space, (inset + faceW) - lineH, ColorSquare.WIDTH, ColorSquare.WIDTH);
                    vpSq.draw();

                    int topStuffW = inset + faceW + inset + vpW + space + ColorSquare.WIDTH + space;

                    if (player == game.getPlayerWithLargestArmy())
                    {
                        larmyLab.setBounds(topStuffW, (inset + faceW) - lineH, (dim.width - (topStuffW + inset + space)) / 2, lineH);
                    }

                    if (player == game.getPlayerWithLongestRoad())
                    {
                        lroadLab.setBounds(topStuffW + ((dim.width - (topStuffW + inset + space)) / 2) + space, (inset + faceW) - lineH, (dim.width - (topStuffW + inset + space)) / 2, lineH);
                    }
                }

                giveLab.setBounds(inset, tradeY, giveW, lineH);
                getLab.setBounds(inset, tradeY + lineH, giveW, lineH);
                sqPanel.setLocation(inset + giveW + space, tradeY);

                int tbW = ((giveW + sqpDim.width) / 2);
                int tbX = inset;
                int tbY = tradeY + sqpDim.height + space;
                clearBut.setBounds(tbX, tbY + lineH + space, tbW, lineH);
                sendBut.setBounds(tbX, tbY, tbW, lineH);
                bankBut.setBounds(tbX + tbW + space, tbY + lineH + space, tbW, lineH);

                if (playerSend[0] != null)
                {
                    playerSend[0].setBounds(tbX + tbW + space, tbY, ColorSquare.WIDTH, ColorSquare.HEIGHT);
                    playerSend[0].draw();
                }

                if (playerSend[1] != null)
                {
                    playerSend[1].setBounds(tbX + tbW + space + ((tbW - ColorSquare.WIDTH) / 2), tbY, ColorSquare.WIDTH, ColorSquare.HEIGHT);
                    playerSend[1].draw();
                }

                if (playerSend[2] != null)
                {
                    playerSend[2].setBounds((tbX + tbW + space + tbW) - ColorSquare.WIDTH, tbY, ColorSquare.WIDTH, ColorSquare.HEIGHT);
                    playerSend[2].draw();
                }

                knightsLab.setBounds(dim.width - inset - knightsW - ColorSquare.WIDTH - space, tradeY, knightsW, lineH);
                knightsSq.setBounds(dim.width - inset - ColorSquare.WIDTH, tradeY, ColorSquare.WIDTH, ColorSquare.HEIGHT);
                roadLab.setBounds(dim.width - inset - knightsW - ColorSquare.WIDTH - space, tradeY + lineH + space, knightsW, lineH);
                roadSq.setBounds(dim.width - inset - ColorSquare.WIDTH, tradeY + lineH + space, ColorSquare.WIDTH, ColorSquare.HEIGHT);
                settlementLab.setBounds(dim.width - inset - knightsW - ColorSquare.WIDTH - space, tradeY + (2 * (lineH + space)), knightsW, lineH);
                settlementSq.setBounds(dim.width - inset - ColorSquare.WIDTH, tradeY + (2 * (lineH + space)), ColorSquare.WIDTH, ColorSquare.HEIGHT);
                cityLab.setBounds(dim.width - inset - knightsW - ColorSquare.WIDTH - space, tradeY + (3 * (lineH + space)), knightsW, lineH);
                citySq.setBounds(dim.width - inset - ColorSquare.WIDTH, tradeY + (3 * (lineH + space)), ColorSquare.WIDTH, ColorSquare.HEIGHT);

                clayLab.setBounds(inset, cardsY, sheepW, lineH);
                claySq.setBounds(inset + sheepW + space, cardsY, ColorSquare.WIDTH, ColorSquare.HEIGHT);
                claySq.draw();
                oreLab.setBounds(inset, cardsY + (lineH + space), sheepW, lineH);
                oreSq.setBounds(inset + sheepW + space, cardsY + (lineH + space), ColorSquare.WIDTH, ColorSquare.HEIGHT);
                oreSq.draw();
                sheepLab.setBounds(inset, cardsY + (2 * (lineH + space)), sheepW, lineH);
                sheepSq.setBounds(inset + sheepW + space, cardsY + (2 * (lineH + space)), ColorSquare.WIDTH, ColorSquare.HEIGHT);
                sheepSq.draw();
                wheatLab.setBounds(inset, cardsY + (3 * (lineH + space)), sheepW, lineH);
                wheatSq.setBounds(inset + sheepW + space, cardsY + (3 * (lineH + space)), ColorSquare.WIDTH, ColorSquare.HEIGHT);
                wheatSq.draw();
                woodLab.setBounds(inset, cardsY + (4 * (lineH + space)), sheepW, lineH);
                woodSq.setBounds(inset + sheepW + space, cardsY + (4 * (lineH + space)), ColorSquare.WIDTH, ColorSquare.HEIGHT);
                woodSq.draw();

                int clW = dim.width - (inset + sheepW + space + ColorSquare.WIDTH + (4 * space) + inset);
                int clX = inset + sheepW + space + ColorSquare.WIDTH + (4 * space);
                cardList.setBounds(clX, cardsY, clW, (4 * (lineH + space)) - 2);
                playCardBut.setBounds(((clW - pcW) / 2) + clX, cardsY + (4 * (lineH + space)), pcW, lineH);

                int bbW = 50;
                quitBut.setBounds(inset, dim.height - lineH - inset, bbW, lineH);
                rollBut.setBounds(dim.width - (bbW + space + bbW + inset), dim.height - lineH - inset, bbW, lineH);
                doneBut.setBounds(dim.width - inset - bbW, dim.height - lineH - inset, bbW, lineH);
            }
            else
            {
                /* This is another player's hand */
                int balloonH = dim.height - (inset + (4 * (lineH + space)) + inset);
                int dcardsW = fm.stringWidth(new String("Dev. Cards: "));
                int vpW = fm.stringWidth(vpLab.getText());

                if (player.isRobot())
                {
                    if (game.getPlayer(client.getNickname()) == null)
                    {
                        if (takeOverBut != null)
                        {
                            takeOverBut.setBounds(10, (inset + balloonH) - 10, dim.width - 20, 20);
                        }
                    }
                    else if (seatLockBut != null)
                    {
                        //seatLockBut.setBounds(10, inset+balloonH-10, dim.width-20, 20);
                        seatLockBut.setBounds(inset + dcardsW + space + ColorSquare.WIDTH + space, inset + balloonH + (lineH + space) + (lineH / 2), (dim.width - (2 * (inset + ColorSquare.WIDTH + (2 * space))) - stlmtsW - dcardsW), 2 * (lineH + space));
                    }
                }

                if (offer != null)
                {
                    offer.setBounds(inset, inset + faceW + space, dim.width - (2 * inset), balloonH);
                    offer.doLayout();
                }

                vpLab.setBounds(inset + faceW + inset, (inset + faceW) - lineH, vpW, lineH);
                vpSq.setBounds(inset + faceW + inset + vpW + space, (inset + faceW) - lineH, ColorSquare.WIDTH, ColorSquare.HEIGHT);
                vpSq.draw();

                int topStuffW = inset + faceW + inset + vpW + space + ColorSquare.WIDTH + space;

                if (player == game.getPlayerWithLargestArmy())
                {
                    larmyLab.setBounds(topStuffW, (inset + faceW) - lineH, (dim.width - (topStuffW + inset + space)) / 2, lineH);
                }

                if (player == game.getPlayerWithLongestRoad())
                {
                    lroadLab.setBounds(topStuffW + ((dim.width - (topStuffW + inset + space)) / 2) + space, (inset + faceW) - lineH, (dim.width - (topStuffW + inset + space)) / 2, lineH);
                }

                resourceLab.setBounds(inset, inset + balloonH + (2 * (lineH + space)), dcardsW, lineH);
                resourceSq.setBounds(inset + dcardsW + space, inset + balloonH + (2 * (lineH + space)), ColorSquare.WIDTH, ColorSquare.HEIGHT);
                resourceSq.draw();
                developmentLab.setBounds(inset, inset + balloonH + (3 * (lineH + space)), dcardsW, lineH);
                developmentSq.setBounds(inset + dcardsW + space, inset + balloonH + (3 * (lineH + space)), ColorSquare.WIDTH, ColorSquare.HEIGHT);
                developmentSq.draw();
                knightsLab.setBounds(inset, inset + balloonH + (lineH + space), dcardsW, lineH);
                knightsSq.setBounds(inset + dcardsW + space, inset + balloonH + (lineH + space), ColorSquare.WIDTH, ColorSquare.HEIGHT);
                knightsSq.draw();

                roadLab.setBounds(dim.width - inset - stlmtsW - ColorSquare.WIDTH - space, inset + balloonH + (lineH + space), stlmtsW, lineH);
                roadSq.setBounds(dim.width - inset - ColorSquare.WIDTH, inset + balloonH + (lineH + space), ColorSquare.WIDTH, ColorSquare.HEIGHT);
                settlementLab.setBounds(dim.width - inset - stlmtsW - ColorSquare.WIDTH - space, inset + balloonH + (2 * (lineH + space)), stlmtsW, lineH);
                settlementSq.setBounds(dim.width - inset - ColorSquare.WIDTH, inset + balloonH + (2 * (lineH + space)), ColorSquare.WIDTH, ColorSquare.HEIGHT);
                cityLab.setBounds(dim.width - inset - stlmtsW - ColorSquare.WIDTH - space, inset + balloonH + (3 * (lineH + space)), stlmtsW, lineH);
                citySq.setBounds(dim.width - inset - ColorSquare.WIDTH, inset + balloonH + (3 * (lineH + space)), ColorSquare.WIDTH, ColorSquare.HEIGHT);
            }
        }
    }
}
