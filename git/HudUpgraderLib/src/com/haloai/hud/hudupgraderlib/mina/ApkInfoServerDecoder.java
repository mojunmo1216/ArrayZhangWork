package com.haloai.hud.hudupgraderlib.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.haloai.hud.hudupgraderlib.HudUpgradeConstants;
import com.haloai.hud.hudupgraderlib.model.HudUpgradeApkFileInfo;

public class ApkInfoServerDecoder implements MessageDecoder{

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		if(in.remaining()>HudUpgradeConstants.HEADER_LENGTH){
			int toal=in.getInt();
			if(in.remaining()<toal){
				return MessageDecoderResult.NEED_DATA;
			}else{
				int type=in.getInt();
				if(type==HudUpgradeConstants.APP_INFO_CODE){
					return MessageDecoderResult.OK;
				}else{
					return MessageDecoderResult.NOT_OK;
				}
			}
		}else{
			return MessageDecoderResult.NEED_DATA;
		}
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if(in.prefixedDataAvailable(4))
		{	
			int toal=in.getInt();
			int type=in.getInt();
			session.setAttribute(HudUpgradeConstants.DECODER_TYPE_KEY,type);
			HudUpgradeApkFileInfo apkInfo=new HudUpgradeApkFileInfo(toal,type,in);
		    out.write(apkInfo);
		    in.free();
			return MessageDecoderResult.OK;
		}else{		
			return MessageDecoderResult.NEED_DATA; 
		}
	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
			throws Exception {		
	}

}
