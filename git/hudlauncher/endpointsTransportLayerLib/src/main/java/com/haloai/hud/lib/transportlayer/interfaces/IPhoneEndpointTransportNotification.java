package com.haloai.hud.lib.transportlayer.interfaces;



/**
 * @author Created by Mo Bing(mobing@haloai.com) on 21/11/14.
 */
public interface IPhoneEndpointTransportNotification {
    public abstract void newDeviceAvailable(EndpointDeviceInfo hudDeviceInfo);

    public abstract void updateDeviceInfo(EndpointDeviceInfo hudDeviceInfo);

    public abstract void hardwareStatus(HardwareStatus hardwareStatus, HardwareStatusReason hardwareStatusReason);
    
    public abstract void dataArrival(byte[] newData);

    public static enum HardwareStatusReason
    {
        UNKNOWN,

        NOT_AVAILABLE,

        NOT_ENABLED,

        LOST_CONNECTION,

        USER_REQUEST,

        RETRY_LOGIC,

        CLIENT_DEVICE_MISSING,

        CLIENT_DEVICE_NOT_AVAILABLE,

        CLIENT_DEVICE_STORED,

        STARTED,

        ENDED,

        FAILED,

        PROGRESS,

        IS_TURNING_OFF,

        WAS_TURNED_OFF,

        IS_TURNING_ON,

        WAS_TURNED_ON;
        
    }

    public static enum HardwareStatus
    {
        DEVICE_CONNECTED,

        DEVICE_RECONNECTED,

        DEVICE_DISCONNECTED,

        DEVICE_SEARCH_STATUS_CHANGED,

        DEVICE_STATUS_CHANGED,

        FIRMWARE_UPDATE_STATUS_CHANGED;
    }
}
