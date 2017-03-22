package com.unisound.framework.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimeoutTask {
	private static final String TAG = TimeoutTask.class.getSimpleName();
	
	private String taskName;
	private Runnable task;
	private long timeout;
	private Timer timer;
	private boolean isRunning = false;
	
	public TimeoutTask(String taskName, Runnable task, long timeout){
		this.taskName = taskName;
		this.task = task;
		this.timeout = timeout;
	}
	
	public void execute(String msg){
		LogUtils.d(TAG, taskName + " execute ||Because " + msg + " || " + (isRunning == false));
		if(isRunning == false){
			timer = new Timer();
			isRunning = true;
			
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					LogUtils.d(TAG, taskName + " time out");
					task.run();
				}
			}, timeout);
		}
	}
	
	public void stop(String msg){
		LogUtils.d(TAG, taskName + " cancel ||Because " + msg + " || " + (isRunning == true));
		if(timer != null && isRunning == true){
			isRunning = false;
			timer.cancel();
		}
	}
	
	public boolean isRunning(){
		return isRunning;
	}
}
