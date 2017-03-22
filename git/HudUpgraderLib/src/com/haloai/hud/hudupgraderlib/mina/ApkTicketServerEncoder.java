package com.haloai.hud.hudupgraderlib.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkTicket;


public class ApkTicketServerEncoder<T extends HudUpgradeApkTicket> implements MessageEncoder<T> {

	@Override
	public void encode(IoSession arg0, T ticket, ProtocolEncoderOutput out)
			throws Exception {
		int cat=ticket.getDataLength();
		IoBuffer iobuffer=IoBuffer.allocate(cat);
		ticket.encode(iobuffer);
		iobuffer.flip();
		out.write(iobuffer);
		out.flush();
		iobuffer.free();		
	}

	
}
