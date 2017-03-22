package com.haloai.hud.lib.transportlayer.interfaces;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * HUD设备和Phone设备描述类
 * @author Harry Moo
 */
public class EndpointDeviceInfo implements Parcelable {

    private String name;
    private String uniqueAddress;
    private String firmwareVersion;
    private String serialNumber;

    public EndpointDeviceInfo() {}

    /**
     * 返回硬件唯一地址，比如蓝牙MAC地址，USB设备地址等。
     * @add WIFI的ip地址
     * @return 返回设备地址
     */
    public String getUniqueAddress()
    {
        return this.uniqueAddress;
    }

    /**
     * 设置硬件唯一地址，比如蓝牙MAC地址，USB设备地址等。
     * @add WIFI的ip地址
     * @param macAddress 设备地址
     */
    public void setUniqueAddress(String macAddress)
    {
        this.uniqueAddress = macAddress;
    }

    /**
     * 返回设备名。
     * @return 返回设备名。
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * 设置设备名。
     * @param deviceName 设备名称
     */
    public void setName(String deviceName)
    {
        if (deviceName == null)
            this.name = "";
        else
            this.name = deviceName;
    }

    /**
     * 获取HUD固件版本号
     * @return 返回固件版本号字符串
     */
	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	/**
	 * 设置HUD固件版本号
	 * @param firmwareVersion
	 */
	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	/**
	 * 获取HUD产品序列号
	 * @return 返回序列号字符串
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * 设置HuD产品序列号
	 * @param serialNumber
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

    public String toString() {
        String deviceInfoStr = "Name: " + this.name + "\nAddress: " + this.uniqueAddress;
        if (this.firmwareVersion != null)
        	deviceInfoStr += "\nVersion: " + this.firmwareVersion;
        if (this.serialNumber != null)
        	deviceInfoStr += "\nSerialNumber: " + this.serialNumber;
        return deviceInfoStr;
    }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(name);
		out.writeString(uniqueAddress);
		out.writeString(firmwareVersion);
		out.writeString(serialNumber);
	}
	
	public static final Parcelable.Creator<EndpointDeviceInfo> CREATOR = new Parcelable.Creator<EndpointDeviceInfo>() {
		public EndpointDeviceInfo createFromParcel(Parcel in) {
		    return new EndpointDeviceInfo(in);
		}
		
		public EndpointDeviceInfo[] newArray(int size) {
		    return new EndpointDeviceInfo[size];
		}
	};
	
	private EndpointDeviceInfo(Parcel in) {
		this.name = in.readString();
		this.uniqueAddress = in.readString();
		this.firmwareVersion = in.readString();
		this.serialNumber = in.readString();
	}

}