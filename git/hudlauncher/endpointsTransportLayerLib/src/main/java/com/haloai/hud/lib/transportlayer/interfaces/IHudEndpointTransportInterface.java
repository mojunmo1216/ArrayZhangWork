/**
 * 
 */
package com.haloai.hud.lib.transportlayer.interfaces;

import java.nio.ByteBuffer;

import com.haloai.hud.lib.transportlayer.exceptions.HudTransportException;
import com.haloai.hud.utils.IHaloLogger;

/**
 * @author harrymoo
 *
 */
public interface IHudEndpointTransportInterface {
	
	public abstract void start(IHudEndpointTransportNotification noticiationHandler) throws HudTransportException;
	public abstract void stop() throws HudTransportException;
	public abstract EndpointDeviceInfo currentConnectedPhone();
	public abstract boolean isConnected();
	public abstract void send(ByteBuffer byteBuffer);
	public abstract void setLogger(IHaloLogger logger);
	
}
