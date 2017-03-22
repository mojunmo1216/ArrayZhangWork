package com.unisound.data.source.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiTaskTest {
	private static final String TAG = MultiTaskTest.class.getSimpleName();
	
	private Runnable runnable;
	private ExecutorService pool;
	private static final int TIME = 10;
	private int successTime = 0;
	
	public MultiTaskTest(){
		pool = Executors.newCachedThreadPool();
	}
	
	public void setTask(Runnable runnable){
		this.runnable = runnable;
	}
	
	public void startTask(){
		LogUtils.d(TAG, "task start");
		
		for(int i = 0; i < TIME; i++){
			if(runnable != null){
				pool.execute(runnable);
			}else{
				LogUtils.e(TAG, "startTask report : runable is null");
				return;
			}
		}
	}
	
	public void stopTask(){
		pool.shutdownNow();
	}

	public synchronized void done4Once() {
		successTime++;
		if(successTime == TIME){
			LogUtils.d(TAG, "task success");
		}
	}
	
	public int getCurrentTime(){
		return successTime;
	}
}
