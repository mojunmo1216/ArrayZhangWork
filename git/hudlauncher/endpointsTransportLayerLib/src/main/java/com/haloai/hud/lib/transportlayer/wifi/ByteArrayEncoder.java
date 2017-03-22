package com.haloai.hud.lib.transportlayer.wifi;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * @author ��
 */
/**
 *  ������������ֱ�ӷ���ȥ(��������)
 */
public class ByteArrayEncoder extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession session, Object message,
            ProtocolEncoderOutput out) throws Exception {
        out.write(message);
        out.flush();
        
    }
}

