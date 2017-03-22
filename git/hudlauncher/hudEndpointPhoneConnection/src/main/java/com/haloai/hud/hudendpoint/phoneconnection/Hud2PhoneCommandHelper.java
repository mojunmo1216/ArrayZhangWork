package com.haloai.hud.hudendpoint.phoneconnection;



public class Hud2PhoneCommandHelper {

	/*
	 * @param contactInfo the Phone Number or Contact Name
	 */

	public static void sendCmdOfMakePhoneCall(String contactInfo) {
	//	sendCmd(Hud2PhoneCommand.H2PCMD_MAKE_A_PHONECALL, contactInfo, null, null, null);
	}

	public static void sendCmdOfKillPhoneCall() {
		//sendCmd(Hud2PhoneCommand.H2PCMD_KILL_A_PHONECALL, null, null, null, null);
	}

	public static void sendCmdOfAnswerPhoneCall() {
		//sendCmd(Hud2PhoneCommand.H2PCMD_ANSWER_PHONECALL, null, null, null, null);
	}

	public static void sendCmdOfMusicPlay(String musicAction) {
		//sendCmd(musicAction, null, null, null, null);
	}

	public static void sendCmdOfMakeNavigation(double lat, double lng, String name) {
		//sendCmd(Hud2PhoneCommand.H2PCMD_MAKE_A_NAVIGATION, String.valueOf(lat), String.valueOf(lng), name, null);
	}

	public static void sendCmdOfKillNavigation() {
		//sendCmd(Hud2PhoneCommand.H2PCMD_KILL_A_NAVIGATION, null, null, null, null);
	}

	private static void sendCmd(String cmdId, String param1, String param2, String param3, byte[] paramBundle) {
		/*Log.v("hanyu", "sendCmd  cmdId "+cmdId+" param1:"+param1);
		Hud2PhoneCommand h2pCmd = Hud2PhoneCommand.newHud2PhoneCommand(cmdId);
		h2pCmd.setParam1(param1);
		h2pCmd.setParam2(param2);
		h2pCmd.setParam3(param3);
		h2pCmd.setParamBundle(paramBundle);
		HudH2PData hudH2PData = new HudH2PData();
		hudH2PData.setHud2PhoneCommand(h2pCmd);
		byte[] cmdBytes = hudH2PData.encapsulateToRawData();

		PhoneConnectionManager.getDataSender().sendData(cmdBytes);*/
	}
}
