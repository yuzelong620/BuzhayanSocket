package com.crxl.xllxj.module.core.thread;

import org.xlcr.thread.Work;

public class ChannelThreads {
	
    WorkChannelThread main;
    Work save;
    
	public ChannelThreads(WorkChannelThread main, Work save) { 
		this.main = main;
		this.save = save;
	}
	
	public void putMainTask(Runnable task){
		main.put(task);
	}
	public void putSaveTask(Runnable task){
		save.put(task);
	}

}
