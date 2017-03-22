package com.haloai.hud.hudupgraderlib.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkFileInfo;

public class ApkInfoClientEncoder<T extends HudUpgradeApkFileInfo> implements MessageEncoder<T> {

	@Override
	public void encode(IoSession arg0, T info, ProtocolEncoderOutput out)
			throws Exception {
		int cat=info.getDataLength();
		IoBuffer iobuffer=IoBuffer.allocate(cat);
		info.encode(iobuffer);
		iobuffer.flip();
		out.write(iobuffer);
		out.flush();
		iobuffer.free();
	}

}
