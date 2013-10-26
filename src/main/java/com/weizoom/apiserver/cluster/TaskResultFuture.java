/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-15
 */

package com.weizoom.apiserver.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.weizoom.apiserver.cluster.constant.ResultCode;
import com.weizoom.apiserver.cluster.event.IResultListener;

/**
 * <code>TaskResultFuture</code>是<code>TaskResult</code>的<code>Future</code>描述<br />
 * 支持为其添加监听器<br />
 * @author wuyadong
 *
 */
public class TaskResultFuture extends TaskResult {
	final static private Logger LOG = Logger.getLogger(TaskResultFuture.class);
	
	private boolean isDone;
	final private List<IResultListener> listeners;
	private Channel channel;
	
	final private ReentrantLock lock;
	final private Condition doneCondition;
	
	public TaskResultFuture(String taskId) {
		this(taskId, ResultCode.SUCCESS);
	}
	
	TaskResultFuture(String taskId, ResultCode code) {
		this(taskId, code, new JSONObject());
	}
	
	private TaskResultFuture(String taskId, ResultCode code, JSONObject result) {
		this(taskId, code, result, code.toString());
	}

	private TaskResultFuture(String taskId, ResultCode code, JSONObject result, String message) {
		super(taskId, code, result, message);
		listeners = new ArrayList<IResultListener>();
		lock = new ReentrantLock();
		doneCondition = lock.newCondition();
		isDone = false;
	}

	public void setDone() {
		lock.lock();
		try {
			if (isDone) {
				doneCondition.signalAll();
				return;
			}
			isDone = true;
			notifyListeners();
			doneCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public boolean isDone() {
		lock.lock();
		try {
			return isDone;
		} finally {
			lock.unlock();
		}
	}
	
	public void awaitUninterruptibly() {
		boolean interrupted = false;
		lock.lock(); 
		try {
			while (! isDone) {
				try {
					doneCondition.await();
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
		} finally {
			lock.unlock();
	        if (interrupted) {
	            Thread.currentThread().interrupt();
	        }
		}
	}
	
	public boolean awaitUninterruptibly(long time, TimeUnit unit) {
		boolean interrupted = false;
		lock.lock();
		try {
			long nanoTime = unit.toNanos(time);
			while (! isDone) {
				if (nanoTime > 0) {
					try {
						nanoTime = doneCondition.awaitNanos(nanoTime);
					} catch (InterruptedException e) {
						interrupted = true;
					}
				} else {
					return false;
				}
			}
			return true;
		} finally {
			lock.unlock();
	        if (interrupted) {
	            Thread.currentThread().interrupt();
	        }
		}
	}
	
	//TODO 中断处理
	@Override
	public ResultCode getCode() {
		lock.lock();
		try {
			try {
				while (! isDone) {
					doneCondition.await();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return this.code;
		} finally {
			lock.unlock();
		}
	}
	
	//TODO 中断处理
	@Override
	public String getMessage() {
		lock.lock();
		try {
			try {
				while (! isDone) {
					doneCondition.await();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return this.message;
		} finally {
			lock.unlock();
		}
	}

	//TODO 中断处理
	@Override
	public JSONObject getResult() {
		lock.lock();
		try {
			try {
				while (! isDone) {
					doneCondition.await();
				}
			} catch (InterruptedException e) {
				// TODO: handle exception
			}
			return this.result;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void setCode(ResultCode code) {
		if (code == null) {
			throw new NullPointerException("codes");
		}
		lock.lock();
		try {
			this.code = code;
		} finally {
			lock.unlock();
		}
	}
	
	public void setMessage(String message) {
		if (message == null) {
			throw new NullPointerException("message");
		}
		lock.lock();
		try {
			this.message = message;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void setResult(JSONObject result) {
		if (result == null) {
			throw new NullPointerException("result");
		}
		lock.lock();
		try {
			this.result = result;
		} finally {
			lock.unlock();
		}
	}

	//TODO 中断处理(很少发生)
	@Override
	public HttpResponse toHttpResponse() {
		lock.lock();
		try {
			try {
				while (! isDone) {
					doneCondition.await();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return super.toHttpResponse();
		} finally {
			lock.unlock();
		}
	}
	
	//TODO 中断处理
	@Override
	public JSONObject toJson() {
		lock.lock();
		try {
			try {
				while (! isDone) {
					doneCondition.await();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return super.toJson();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 增加事件监听器
	 * @param listener
	 */
	public void addListener(IResultListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		listeners.add(listener);
	}
	
    private void notifyListeners() {
    	for (IResultListener listener : listeners) {
    		try {
				listener.operationComplete(this);
			} catch (Exception e) {
				LOG.error("task result future notify get a exception: ", e);
				e.printStackTrace();
			}
    	}
    }
    
    public void setChannel(Channel channel) {
    	if (channel == null) {
    		throw new NullPointerException("channel");
    	}
    	this.channel = channel;
    }
    
    public Channel getChannel() {
    	return this.channel;
    }
}
