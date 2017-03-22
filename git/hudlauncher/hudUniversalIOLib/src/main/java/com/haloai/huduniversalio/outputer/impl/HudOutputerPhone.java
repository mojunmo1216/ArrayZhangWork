package com.haloai.huduniversalio.outputer.impl;

import com.haloai.hud.hudendpoint.phoneconnection.Hud2PhoneCommandHelper;
import com.haloai.huduniversalio.outputer.IHudOutputer;

public class HudOutputerPhone implements IHudOutputer {

	public static enum PhoneCallCommand {
		ANSWER_PHONECALL,
		HANDUP_PHONECALL
	}

	@Override
	public void output(Object object, HudOutputContentType contentType,
			boolean keepRecongize) {
		if (object instanceof PhoneCallCommand) {
			PhoneCallCommand cmd = (PhoneCallCommand)object;
			if (cmd == PhoneCallCommand.ANSWER_PHONECALL) {
				answerPhoneCall();
			} else if (cmd == PhoneCallCommand.HANDUP_PHONECALL) {
				handupPhoneCall();
			}
		}
	}

	private void answerPhoneCall() {
		Hud2PhoneCommandHelper.sendCmdOfAnswerPhoneCall();
	}

	private void handupPhoneCall() {
		Hud2PhoneCommandHelper.sendCmdOfKillPhoneCall();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOutputContentType2IDLE() {
		
	}



}
