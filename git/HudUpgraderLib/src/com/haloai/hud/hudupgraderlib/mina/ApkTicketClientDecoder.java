package com.haloai.hud.hudupgraderlib.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.haloai.hud.hudupgraderlib.HudUpgradeConstants;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkTicket;


public class ApkTicketClientDecoder  implements MessageDecoder{

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		if(in.remaining()<4){
			return MessageDecoderResult.NEED_DATA;
		}
		in.getInt();
		int type=in.getInt();
		if(type!=HudUpgradeConstants.APP_DATA_CODE&&type!=HudUpgradeConstants.APP_INFO_CODE){
			return MessageDecoderResult.NOT_OK;
		}
		return MessageDecoderResult.OK;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if(in.remaining()<4){
			return MessageDecoderResult.NEED_DATA;
		}
		int toal=in.getInt();
		int type=in.getInt();
		session.setAttribute(HudUpgradeConstants.DECODER_TYPE_KEY, type);
		HudUpgradeApkTicket ticket=new HudUpgradeApkTicket(toal,type, in);
		out.write(ticket);
		in.free();
		return MessageDecoderResult.OK;
	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
			throws Exception {
		
		
	}

	
}
