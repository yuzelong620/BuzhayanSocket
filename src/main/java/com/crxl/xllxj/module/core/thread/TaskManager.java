package com.crxl.xllxj.module.core.thread;

  

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.xlcr.thread.ThreadGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlcr.thread.Work;
import org.xlcr.thread.WorkThread;

import com.crxl.xllxj.module.core.config.ServerConfigLoad;
import com.crxl.xllxj.module.core.service.BaseServiceAdapter;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class TaskManager extends BaseServiceAdapter{
	final Logger messageLogger=LoggerFactory.getLogger("system");
	Logger logger = LoggerFactory.getLogger(TaskManager.class);
	public static final TaskManager instance = new TaskManager();

	private TaskManager() {

	}

	public final ThreadGroup<WorkChannelThread> mainGroup  = new ThreadGroup<WorkChannelThread>(generateWorkChannelThread(50, "main"));// 主要逻辑处理线程
	
	public final ThreadGroup<Work> saveGroup  = new ThreadGroup<Work>(generateWorkThread(10, "save"));// 保存数据线程
	public final ThreadGroup<Work> loginGroup = new ThreadGroup<Work>(generateWorkThread(50, "login"));// 登录
	public final ThreadGroup<Work> logGroup   = new ThreadGroup<Work>(generateWorkThread(5, "log"));// 日志
	public final ThreadGroup<Work> otherGroup = new ThreadGroup<Work>(generateWorkThread(15, "other"));//其他
	
	HashMap<Work ,Work > mainToSave=new HashMap<>();//main 线程映射save线程
    
	public void init() { 
		mainGroup.start();
		saveGroup.start();
		loginGroup.start();
		logGroup.start();
		otherGroup.start();
		//映射一下 main线程和save线程的关系
		mappingMainThreadAndSaveThread();
		testMappingSaveThreadInfo();
	}
	private WorkChannelThread[] generateWorkChannelThread(int size,String name) {
		WorkChannelThread[] threads=new WorkChannelThread[size];
		for(int  i=0;i<size;i++){
			WorkChannelThread t=new WorkChannelThread(name+"_"+i);
			threads[i]=t;
		}
		return threads;
	}
	private WorkThread[] generateWorkThread(int size,String name) {
		WorkThread[] threads=new WorkThread[size];
		for(int  i=0;i<size;i++){
			WorkThread t=new WorkThread(name+"_"+i);
			threads[i]=t;
		}
		return threads;
	}
	private void testMappingSaveThreadInfo() {
		 HashMap<Work ,List<Work >> map=new HashMap<>();
		 for(Entry<Work , Work > entry:mainToSave.entrySet()) {
			 logger.info(entry.getKey().getName()+"---->"+entry.getValue().getName());
			 Work  main=entry.getKey();
			 Work  save=entry.getValue();
			 List<Work > list=map.get(save);
			 if(list==null) {
				 list=new ArrayList<>();
				 map.put(save, list);
			 }
			 list.add(main);
		 }
		 
		 for(Entry<Work , List<Work >> entry:map.entrySet()) {
			 StringBuffer sb=new StringBuffer();
			 for(Work  t:entry.getValue()) {
				 sb.append("  ");
				 sb.append(t.getName());
				 sb.append("  ");
			 }
			 logger.info(entry.getKey().getName()+"映射："+sb.toString());
		 }
	}

	private void mappingMainThreadAndSaveThread() {
		HashMap<Work ,Work > mainToSaveTemp=new HashMap<>();
		Work [] mainThreads=mainGroup.threads;
		Work [] saveThreads=saveGroup.threads;
		int saveLenth=saveThreads.length;
		int mailLenth=mainThreads.length;
		for(int i=0;i<mailLenth;i++) {
			Work main=mainThreads[i];
			int index=(i+saveLenth)%saveLenth;
			Work  save=saveThreads[index];
			mainToSaveTemp.put(main, save);
		}
		this.mainToSave=mainToSaveTemp;
	}

	public void close() {
		loginGroup.close();
		otherGroup.close();
		mainGroup.close();
		saveGroup.close();
		logGroup.close();
	}

  final AttributeKey<ChannelThreads> channelThreads = AttributeKey.valueOf("channelThreads");
    /**
     * 注册 关联 线程
     * @param channel
     */
	public ChannelThreads registerChannelThread(Channel channel) {
		ChannelThreads ct = channel.attr(channelThreads).get();
		if (ct != null) {
			logger.error("ChannelThreads 已经存在，重复注册");
			return ct;
		}
		WorkChannelThread  main = mainGroup.getLeastTaskThread();
		Work save = mainToSave.get(main);//映射关系
		ChannelThreads temp = new ChannelThreads(main, save);
		channel.attr(channelThreads).set(temp);
		return temp;
	}
	/**
	 * 主逻辑任务 
	 * @param channel
	 * @param task
	 * @return
	 */
	public boolean putMainTask(Channel channel,Runnable task){
		Work mainThread=channel.attr(channelThreads).get().main;
		if(mainThread.getTaskSize()>ServerConfigLoad.getServerConfig().getThread_max_task_size()) {
			logger.error("线程任务处理已经到上线，丢弃任务");
			return false;//
		}
		mainThread.put(task);
		return true;
	}
	 
	public void putSaveTask(Channel channel,Runnable task){
		 channel.attr(channelThreads).get().save.put(task);
	}
	 
	public void putSaveTaskByHash(Runnable task){
		saveGroup.hashPut(task);
	}
	/**
	 * 其他任務根據 hash 放入對應的綫程
	 * @param task
	 */
	public void hashPutOtherTask(Runnable task){
		 this.otherGroup.hashPut(task);
	}
	/**
	 * 登陸 任務
	 * @param task
	 */
	public boolean putLoginTask(Runnable task){
		Work thread=this.loginGroup.getLeastTaskThread();
		if(thread.getTaskSize()>ServerConfigLoad.getServerConfig().getThread_max_task_size()) {
			logger.info("服务器繁忙，丢弃loginGroup 的任务");
			return false;
		}
		this.loginGroup.getLeastTaskThread().put(task);
		return true;
	}
	
	/**
	 * 记录日志消息(客户端和服务器来往的消息)
	 * @param info 
	 * @param data
	 */
	public void putMessageLogger(String loggerInfo){
		 Runnable task=new Runnable(){
			public void run(){
				messageLogger.info(loggerInfo);
			}
		};
		this.logGroup.getLeastTaskThread().put(task);
	}
}
