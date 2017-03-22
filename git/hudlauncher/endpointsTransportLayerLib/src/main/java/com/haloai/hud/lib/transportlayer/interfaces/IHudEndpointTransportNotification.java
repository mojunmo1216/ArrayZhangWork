package com.haloai.hud.lib.transportlayer.interfaces;

public interface IHudEndpointTransportNotification {

	public abstract void phoneConnected(EndpointDeviceInfo deviceInfo);
	public abstract void phoneDisconnected();
	public abstract void dataArrival(byte[] newData);
}
