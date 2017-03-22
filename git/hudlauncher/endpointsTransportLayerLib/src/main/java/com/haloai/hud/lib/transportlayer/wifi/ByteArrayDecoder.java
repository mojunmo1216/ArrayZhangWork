package com.haloai.hud.lib.transportlayer.wifi;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * @author ��
 */
public class ByteArrayDecoder extends CumulativeProtocolDecoder  {

    @Override
    public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
            throws Exception {
        
        if(in.remaining() > 4){//ǰ4�ֽ��ǰ�ͷ
            //��ǵ�ǰposition�Ŀ��ձ��mark���Ա��̵�reset�����ָܻ�positionλ��
            in.mark(); 
            byte[] l = new byte[4];
            in.get(l);

            //�������ݳ���
            int len = byteArrayToInt(l);//��byteת��int
           

            //ע�������get�����ᵼ�������remaining()ֵ�����仯
            if(in.remaining() < len){
                //�����Ϣ���ݲ����������ûָ�positionλ�õ�����ǰ,������һ��, ���������ݣ���ƴ�ճ���������
                in.reset();   
                return false;
            }else{
                //��Ϣ�����㹻
                in.reset();//���ûָ�positionλ�õ�����ǰ
                int sumlen = 4+len;//�ܳ� = ��ͷ+����
                byte[] packArr = new byte[sumlen];
                in.get(packArr, 0 , sumlen);
                
                IoBuffer buffer = IoBuffer.allocate(sumlen);
                buffer.put(packArr);
                buffer.flip();
                out.write(buffer);
                buffer.free();
                
                if(in.remaining() > 0){//�����ȡһ�����������ݺ�ճ�˰������ø����ٵ���һ�Σ�������һ�ν���
                    return true;
                }
            }
        }
        return false;//����ɹ����ø�����н����¸���
    }
    
    public static int byteArrayToInt(byte[] b){ 
    	byte[] a = new byte[4]; 
    	int i = a.length - 1,j = b.length - 1; 
    	for (; i >= 0 ; i--,j--) {//��b��β��(��intֵ�ĵ�λ)��ʼcopy���� 
    	if(j >= 0) 
    	a[i] = b[j]; 
    	else 
    	a[i] = 0;//���b.length����4,�򽫸�λ��0 
    	} 
    	int v0 = (a[0] & 0xff) << 24;//&0xff��byteֵ�޲���ת��int,����Java�Զ�����������,�ᱣ����λ�ķ���λ 
    	int v1 = (a[1] & 0xff) << 16; 
    	int v2 = (a[2] & 0xff) << 8; 
    	int v3 = (a[3] & 0xff) ; 
    	return v0 + v1 + v2 + v3; 
    	}
    
}