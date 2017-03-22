package com.haloai.huduniversalio.inputer;

import com.haloai.hud.model.v2.NaviRouteInfo;

/**
 * Created by ylq on 16/8/8.
 */
public interface IPhoneInputerNotifer {

  public abstract void startNavigation(NaviRouteInfo routeInfo);

  public abstract void stopNavigation();

  public abstract void phoneOnClickCommand(int command);

  public abstract void phonePlayRecorderVideo(String path,int index);

  public abstract void phoneControllerRecorderVideo(int command);

}


