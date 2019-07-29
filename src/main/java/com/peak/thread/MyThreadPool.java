package com.peak.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 
 * @author Pan
 *
 */
public class MyThreadPool {
	/**
	 * 设置核心池大小 
	 */
    int corePoolSize = 5;

    /** 设置线程池最大能接受多少线程 */

    /** 当前线程数大于corePoolSize、小于maximumPoolSize时，超出corePoolSize的线程数的生命周期 */
    long keepActiveTime = 200;

    /** 设置时间单位，秒 */
    TimeUnit timeUnit = TimeUnit.SECONDS;

    /** 设置线程池缓存队列的排队策略为FIFO，并且指定缓存队列大小为5 */
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(5);

	private int maximumPoolSize = 200;

    //创建ThreadPoolExecutor线程池对象，并初始化该对象的各种参数
    //ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize , keepActiveTime, timeUnit,workQueue);
    
    static volatile MyThreadPool myThreadPool = null;
    
    private MyThreadPool() {
    }
    
    public static MyThreadPool getInstance() {
    	if(myThreadPool == null) {
    		synchronized (MyThreadPool.class) {
				if(myThreadPool == null) {
					myThreadPool = new MyThreadPool();
				}
			}
    	}
    	return myThreadPool;
    }
    
    public ThreadPoolExecutor getThreadPool() {
    	ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
    	        .setNameFormat("demo-pool-%d").build();
    	return new ThreadPoolExecutor(corePoolSize, maximumPoolSize , keepActiveTime, timeUnit, workQueue, namedThreadFactory);
	}
    
    		
}
