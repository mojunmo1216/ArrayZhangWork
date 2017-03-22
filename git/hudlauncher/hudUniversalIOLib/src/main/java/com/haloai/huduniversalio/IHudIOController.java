package com.haloai.huduniversalio;

import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudInputerType;
import com.haloai.huduniversalio.HudIOConstants.HudOutputerType;
import com.haloai.huduniversalio.inputer.IInputerStatusListener;
import com.haloai.huduniversalio.outputer.IHudOutputer;

public interface IHudIOController {
	
	/****** Management methods*/
	public abstract void init();
	public abstract void setHudIOControllerListener(IHudIOControllerListener ioControllerListener);
//	public abstract void setLogger(IHaloLogger logger);
	
	/****** IO Session related methods */
	public abstract HudIOSessionType getCurrentSessionType();
	/*
	 * 取消当前Session，传递sessionType以保证取消的是正确的Session
	 * 一般只会在外部对第三方平台调用失败后，才会主动取消当前Session
	 */
	public abstract void endCurrentSession(HudIOSessionType sessionType);
	/*
	 * 继续当前Session，有些Session会暂停，以等待外部对第三方平台调用的返回才继续，此时一般只接受取消等操作
	 * 比如导航Session，会等待POI搜索完成后，才继续接受"第几个"的选择控制
	 */
	public abstract void continueCurrentSession(HudIOSessionType sessionType);

	/****** Inputer&Outputer related methods. */
	/*
	 * 判断Inputer是否可用
	 */
	public abstract boolean isInputerReady(HudInputerType inputerType);
	/*
	 * 设置Inputer状态监听器，不同的Inputer有不同的监听器
	 */
	public abstract void setInputerStatusListener(HudInputerType inputerType, IInputerStatusListener inputerStatusListener);
	/*
	 * 开始Inputer连接
	 */
	public abstract void resumeHudInputer(HudInputerType inputerType);
	/*
	 * 断开Inputer连接
	 */
	public abstract void pauseHudInputer(HudInputerType inputerType);
	/*
	 * 判断Outputer是否可用
	 */
	public abstract boolean isOutputerReady(HudOutputerType outputerType);
	/*
	 * 获取Outputer的实例
	 */
	public abstract IHudOutputer getHudOutputer(HudOutputerType outputerType);


	
}
