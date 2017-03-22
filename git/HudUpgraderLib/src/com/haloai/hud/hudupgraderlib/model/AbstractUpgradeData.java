package com.haloai.hud.hudupgraderlib.model;

import org.apache.mina.core.buffer.IoBuffer;

public abstract class AbstractUpgradeData{
    private int toal; //数据总长
	private int type; //数据类型
      
    public AbstractUpgradeData(int toal,int type)
    {
    	  this.toal=toal;
    	  this.type=type;  	  
    }      
    
    public int getDataLength()
    {
  		return 4 + 4+getAllFieldsDataLength();
  	}
    
    public void encode(IoBuffer ioBuffer) {
    	ioBuffer.putInt(toal);
    	ioBuffer.putInt(type);
  	}
      
    protected abstract int getAllFieldsDataLength();
     
}
