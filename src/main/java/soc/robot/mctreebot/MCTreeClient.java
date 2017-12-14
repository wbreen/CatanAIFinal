package soc.robot.mctreebot;

import soc.game.SOCGame;
import soc.message.SOCMessage;
import soc.robot.SOCRobotBrain;
import soc.robot.SOCRobotClient;
import soc.util.CappedQueue;
import soc.util.SOCRobotParameters;

public class MCTreeClient extends SOCRobotClient {
	
	private static final String RBCLASSNAME_SAMPLE = MCTreeClient.class.getName();
	
	/**
	 * @param h server hostname
	 * @param p server port
	 * @param nn nickname for robot
	 * @param pw password for robot
	 * @param co required cookie for robot connections to server
	 */
	public MCTreeClient(final String h, final int p, final String nn, final String pw, final String co) {
		super(h, p, nn, pw, co);
		
		rbclass = RBCLASSNAME_SAMPLE;
	}
	
	@Override
	public SOCRobotBrain createBrain (final SOCRobotParameters params, final SOCGame ga, final CappedQueue<SOCMessage> mq) {
		return new MCTreeBrain(this, params, ga, mq);
	}
	
	public static void main(String[] args) {
		if (args.length < 5) {
			System.err.println("Java Settlers Monte Carlo Tree robot example");
            System.err.println("usage: java " + RBCLASSNAME_SAMPLE + " hostname port_number userid password cookie");

            return;
		}
		
		MCTreeClient cli = new MCTreeClient(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);
		cli.init();
	}

}
