package soc.robot.mctreebot;

import soc.game.SOCGame;
import soc.game.SOCResourceConstants;
import soc.game.SOCResourceSet;
import soc.game.SOCTradeOffer;
import soc.message.SOCMessage;
import soc.robot.SOCRobotBrain;
import soc.robot.SOCRobotClient;
import soc.robot.SOCRobotNegotiator;
import soc.util.CappedQueue;
import soc.util.SOCRobotParameters; 

public class MCTreeBrain extends SOCRobotBrain{
	
	//Constructor
	public MCTreeBrain(SOCRobotClient rc, SOCRobotParameters params, SOCGame ga, CappedQueue<SOCMessage> mq) {
		super(rc, params, ga, mq);
	}
	
	
	
	@Override
	/**
	 * If the offer is not given to us, igonre it
	 * if the offer is towards us, accept it
	 */
	protected int considerOffer(SOCTradeOffer offer) {
		if(!offer.getTo()[getOurPlayerNumber()]) {
			return SOCRobotNegotiator.IGNORE_OFFER;
		}
		return SOCRobotNegotiator.ACCEPT_OFFER;
	}
	
	//TODO
	//want to try to place something we want to build
		//robotBrain.whatWeWantToBuild
	//set our building plan (a stack of SOCPossiblePieces), set in planBuilding()
	protected int idiotWilliam;
	
	
}
