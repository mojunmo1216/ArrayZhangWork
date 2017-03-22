package com.haloai.hud.lib.transportlayer.interfaces;


import java.nio.ByteBuffer;
import java.util.List;

import com.haloai.hud.lib.transportlayer.exceptions.HudTransportException;
import com.haloai.hud.utils.IHaloLogger;

/**
 * 手机端硬件接口，用来隔离底层硬件具体的通讯协议，底层可以实现多种协议，比如蓝牙GATT、蓝牙BLE、USB Data等。
 *
 * @author Created by Harry Moo on 21/4/14.
 */
public interface IPhoneEndpointTransportInterface {

    public abstract EndpointDeviceInfo getRecentDevice();

    public abstract void clearRecentDevice();

    public abstract void connect(IPhoneEndpointTransportNotification hudHardwareNotification, EndpointDeviceInfo deviceInfo)
            throws HudTransportException;

    public abstract void searchForHudDevices(IPhoneEndpointTransportNotification hudHardwareNotification)
            throws HudTransportException;

    public abstract List<EndpointDeviceInfo> retrievePairedDevices();

    public abstract void stopDeviceSearch();

    public abstract boolean isConnected();

    public abstract boolean isSearching();

    public abstract void disconnect();

    public abstract void send(ByteBuffer byteBuffer);

//    public abstract List<IHudProtocolPacket> takeReceivedHudProtocolPacket();

    public abstract void setLogger(IHaloLogger engineLogger);
}
