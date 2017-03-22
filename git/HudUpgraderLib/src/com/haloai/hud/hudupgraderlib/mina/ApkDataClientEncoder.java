package com.haloai.hud.hudupgraderlib.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkFileData;

public class ApkDataClientEncoder<T extends HudUpgradeApkFileData> implements MessageEncoder<T>{

	@Override
	public void encode(IoSession session, T data, ProtocolEncoderOutput out)
			throws Exception {		
		int cat=data.getDataLength();
		IoBuffer iobuffer=IoBuffer.allocate(cat);
		data.encode(iobuffer);
		iobuffer.flip();
		out.write(iobuffer);
		out.flush();
		iobuffer.free();
	}

}
