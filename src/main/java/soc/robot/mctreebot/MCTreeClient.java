package soc.robot.mctreebot;

import soc.robot.SOCRobotBrain;
import soc.robot.SOCRobotClient;

public class MCTreeClient extends SOCRobotClient {
	
	/**
	 * @param h server hostname
	 * @param p server port
	 * @param nn nickname for robot
	 * @param pw password for robot
	 * @param co required cookie for robot connections to server
	 */
	public MCTreeClient(final String h, final int p, final String nn, final String pw, final String co) {
		super(h, p, nn, pw, co);
	}

}
