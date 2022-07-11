package com.lingfei.android.uilib.language;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
	* 线程池管理工具类
	*/
public class ThreadPoolManager{

				private ExecutorService service;

				@SuppressWarnings("unused")
				private ThreadPoolManager(){
								int num = Runtime.getRuntime().availableProcessors();
								//service = Executors.newFixedThreadPool(num * 3);
								service = Executors.newCachedThreadPool();
				}

				private static final ThreadPoolManager manager = new ThreadPoolManager();

				public static ThreadPoolManager getInstance(){
								return manager;
				}

				public void executeTask(Runnable runnable){
								service.execute(runnable);
				}


}
