package com.haloai.hud.hudendpoint.phoneconnection;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Build;


import com.google.protobuf.InvalidProtocolBufferException;
import com.haloai.hud.lib.transportlayer.bluetooth.HudEndPointTransportBTSpp;
import com.haloai.hud.lib.transportlayer.bluetooth_ble.HudEndPointTransportBTBle;
import com.haloai.hud.lib.transportlayer.interfaces.EndpointDeviceInfo;
import com.haloai.hud.lib.transportlayer.interfaces.IHudEndpointTransportInterface;
import com.haloai.hud.lib.transportlayer.interfaces.IHudEndpointTransportNotification;
import com.haloai.hud.lib.transportlayer.wifi.HudEndPointTransportWifi;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.Hud2PhoneMessages;
import com.haloai.hud.model.v2.HudResponseId;
import com.haloai.hud.model.v2.Phone2HudMessages;
import com.haloai.hud.proto.v2.Phone2HudMessagesProtoDef;
import com.haloai.hud.utils.HaloLogger;

public class PhoneConnectionManager implements IHudEndpointTransportNotification, IPhoneDataSender {

	private static final String TAG ="PhoneConnectionManager";
	private EndpointDeviceInfo phoneDeviceInfo;
	private Map<PhoneConnectionType, IHudEndpointTransportInterface> mTransportMap;
	private List<IDataDispatcher> dataDispatcherList = new ArrayList<IDataDispatcher>();
	//****Singleton variables and methods
	private static PhoneConnectionManager theInstance;
	private Context mContext;
	//Not allow to construct this class explicitly.
	private PhoneConnectionManager(){};
	//A single point to create the instance
	public static synchronized PhoneConnectionManager getInstance() {
		if (theInstance == null) {
			theInstance = new PhoneConnectionManager();
		}
		return theInstance;
	}

	//Return the data sender instance.
	public static IPhoneDataSender getDataSender() {
		return theInstance;
	}

	public void start(Context appContext) {
		this.mContext=appContext;
		mTransportMap=new HashMap<PhoneConnectionType, IHudEndpointTransportInterface>();
		mTransportMap.put(PhoneConnectionType.TRANSPORT_BTSPP, new HudEndPointTransportBTSpp());
		mTransportMap.put(PhoneConnectionType.TRANSPORT_WIFI, new HudEndPointTransportWifi(appContext));
		if (Build.VERSION.SDK_INT >= 21)mTransportMap.put(PhoneConnectionType.TRANSPORT_BTBLE, new HudEndPointTransportBTBle(appContext));

		for(IHudEndpointTransportInterface transport:mTransportMap.values()){
			transport.start(this);
			transport.setLogger(HaloLogger.logger);
		}
	}

	public void stop() {
		for(IHudEndpointTransportInterface transport:mTransportMap.values()){
			transport.stop();
		}
		mTransportMap.clear();
	}


	public boolean isConnected() {
		boolean isConnected=false;
		for(IHudEndpointTransportInterface transport:mTransportMap.values()){
			if(transport.isConnected()){
				isConnected=true;
			}
		}
		return isConnected;
	}

	public boolean isConnected(PhoneConnectionType type){
		boolean isConnected=false;
		switch (type) {
			case TRANSPORT_BTSPP:
				isConnected=mTransportMap.get(PhoneConnectionType.TRANSPORT_BTSPP).isConnected();
				break;
			case TRANSPORT_WIFI:
				isConnected=mTransportMap.get(PhoneConnectionType.TRANSPORT_WIFI).isConnected();
				break;
			case TRANSPORT_BTBLE:
				isConnected=mTransportMap.get(PhoneConnectionType.TRANSPORT_BTBLE).isConnected();
				break;
			default:
				break;
		}
		return isConnected;
	}

	public void addDataDispatcher(IDataDispatcher dataDispatcher) {
		//不允许添加同类型的Dispatcher
		for (IDataDispatcher dispatcher : dataDispatcherList) {
			if (dataDispatcher.getClass().getName().equalsIgnoreCase(dispatcher.getClass().getName())) {
				throw new IllegalArgumentException("Duplicated Dispatcher instance: " + dispatcher.getClass().getName());
			}
		}
		dataDispatcherList.add(dataDispatcher);

	}

	public EndpointDeviceInfo getPhoneDeviceInfo() {
		return phoneDeviceInfo;
	}

	@Override
	public void phoneConnected(EndpointDeviceInfo deviceInfo) {
		phoneDeviceInfo = deviceInfo;

	}

	@Override
	public void phoneDisconnected() {
		phoneDeviceInfo = null;
	}

	@Override
	public void dataArrival(byte[] newData) {
		try {
			Phone2HudMessagesProtoDef.Phone2HudMessagesProto phone2HudMessagesProto =Phone2HudMessagesProtoDef.Phone2HudMessagesProto.parseFrom(newData);
			Phone2HudMessages phone2HudMessages =new Phone2HudMessages(phone2HudMessagesProto);
			String hudResponSerialNumber=phone2HudMessages.getPhoneRequestId().getPhoneRequestSerialNumber();
			Hud2PhoneMessages hud2PhoneMessages=new Hud2PhoneMessages();
			HudResponseId hudResponseId=new HudResponseId();
			hudResponseId.setHudResponseSerialNumber(hudResponSerialNumber);
			hud2PhoneMessages.setHudResponseId(hudResponseId);

			for (IDataDispatcher dispatcher : this.dataDispatcherList) {
				AHudResponse hudResponse = dispatcher.dispatchP2HData(phone2HudMessages);
				if(hudResponse!=null){
					hud2PhoneMessages.setAHudResponse(hudResponse);
				}
			}
			PhoneConnectionManager.getDataSender().sendData(hud2PhoneMessages.encapsulateHudH2PData());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendData(byte[] data) {
		if (isConnected(PhoneConnectionType.TRANSPORT_WIFI)){
			mTransportMap.get(PhoneConnectionType.TRANSPORT_WIFI).send(ByteBuffer.wrap(data));
			return;
		}else if(isConnected(PhoneConnectionType.TRANSPORT_BTBLE)){
			mTransportMap.get(PhoneConnectionType.TRANSPORT_BTBLE).send(ByteBuffer.wrap(data));
			return;
		}else if(isConnected(PhoneConnectionType.TRANSPORT_BTSPP)){
			mTransportMap.get(PhoneConnectionType.TRANSPORT_BTSPP).send(ByteBuffer.wrap(data));
			return;
		}
	}

	public enum PhoneConnectionType{
		TRANSPORT_WIFI,
		TRANSPORT_BTSPP,
		TRANSPORT_BTBLE
	}
}
