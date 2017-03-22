package com.haloai.huduniversalio.inputer.dispatchImpl;

import android.content.Context;
import android.content.Intent;

import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
//import com.haloai.hud.hudendpoint.protocol.IHudNaviController;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.HudNobodyResponse;
import com.haloai.hud.model.v2.HudRemoterCommand;
import com.haloai.hud.model.v2.Phone2HudMessages;
import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.inputer.IPhoneInputerNotifer;

/**
 * Created by zhangrui on 16/6/21.
 */
public class PhoneCommandDispatcherImp implements IDataDispatcher {
    private Context mContext;
    private IPhoneInputerNotifer mPhoneInputerNotifer;
    public PhoneCommandDispatcherImp(Context context, IPhoneInputerNotifer phoneInputerNotifer){
        this.mContext=context;
        this.mPhoneInputerNotifer=phoneInputerNotifer;
    }

    @Override
    public AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages) {
        HudRemoterCommand hudRemoterCommand = phone2HudMessages.getHudRemoterCommand();
        if (hudRemoterCommand != null) {
            int commandId = hudRemoterCommand.getHudRemoterCommandId();
            if (commandId == EndpointsConstants.REMOTER_COMMAND_PLAY_VIDEO) {
                int command = hudRemoterCommand.getHudCommandInt();
                mPhoneInputerNotifer.phoneControllerRecorderVideo(command);
                if(command == EndpointsConstants.COMMAND_VALUVE_START){
                    mPhoneInputerNotifer.phonePlayRecorderVideo(hudRemoterCommand.getHudCommandString(),hudRemoterCommand.getHudCommandInt1());
                }
                return new HudNobodyResponse();
            }else if(commandId == EndpointsConstants.REMOTER_COMMAND_EXIT_NAVI){
                mPhoneInputerNotifer.stopNavigation();
                return new HudNobodyResponse();
            }else if(commandId == EndpointsConstants.REMOTER_COMMAND_ONCLICK){
                int command = hudRemoterCommand.getHudCommandInt();
                mPhoneInputerNotifer.phoneOnClickCommand(command);
                return new HudNobodyResponse();
            }
        }
        return null;
    }
}
