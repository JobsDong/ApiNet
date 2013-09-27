/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-4-11
 */

package com.weizoom.apiserver.cluster.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultExceptionEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.weizoom.apiserver.cluster.ClusterSettings;
import com.weizoom.apiserver.cluster.ClusterState;
import com.weizoom.apiserver.cluster.Task;
import com.weizoom.apiserver.cluster.TaskResult;
import com.weizoom.apiserver.cluster.constant.ResultCode;
import com.weizoom.apiserver.cluster.exception.DuplicateOperatorException;
import com.weizoom.apiserver.util.ObjectsFactory;
import com.wintim.common.util.LogFactory;

/**
 * {@link com.weizoom.apiserver.cluster.Cluster}运维操作的<i>Handler</i><br>
 * <br>
 * 
 * 可通过{@link #registerOperator(IClusterOperator)}
 * 和{@link #registerOperator(IClusterOperator, boolean)}向该<i>Handler</i>
 * 注册{@link IClusterOperator}<br>
 * <br>
 * 
 * 该<i>Handler</i>处理运维操作时，首先根据运维操作名称获取对应的{@link IClusterOperator}完成
 * 相应的运维操作
 * 注：默认全部加入进去了
 * 
 * @author chuter
 *
 */
public class ClusterOperationHandler extends SimpleChannelUpstreamHandler {
	final static private Logger LOG = LogFactory.getLogger(ClusterOperationHandler.class);
	
	final static private Map<String, IClusterOperator> name2operator = new ConcurrentHashMap<String, IClusterOperator>();
	
	final private ClusterState clusterState;
	public ClusterOperationHandler(ClusterState clusterState) {
		if (clusterState == null) {
			throw new NullPointerException("clusterState");
		}
		this.clusterState = clusterState;
		registerBuildinOperators();
		registerBuildinOperators();
	}
	
	private void fireExceptionEvent(ClusterOperationTask operationTask, ChannelHandlerContext ctx, ExceptionEvent e) {
		assert (operationTask != null);
		
		TaskResult executeResult = new TaskResult(operationTask.getId(), ResultCode.SYSTEM_ERROR_NOT_NEED_RETRY, e.getCause().getMessage());
		Channel channel = ctx.getChannel();
    	if (channel != null && channel.isWritable()) {
    		channel.write(executeResult.toHttpResponse()).addListener(ChannelFutureListener.CLOSE);
    	}
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (! (e.getMessage() instanceof Task)) {
        	ctx.sendUpstream(e);
        	return;
        }
		
		Task task = (Task) e.getMessage();
        if (! isOperationTask(task)) {
        	ctx.sendUpstream(e);
        	return;
        }
    	ClusterOperationTask operationTask = buildOperationTask(task);

        try {
        	TaskResult taskExecuteResult =  exuteOperationTask(operationTask);
        	
        	Channel channel = ctx.getChannel();
        	if (channel != null && channel.isWritable()) {
        		channel.write(taskExecuteResult.toHttpResponse()).addListener(ChannelFutureListener.CLOSE);
        	}
		} catch (Exception cause) {
			ExceptionEvent event = new DefaultExceptionEvent(ctx.getChannel(), cause);
			fireExceptionEvent(operationTask, ctx, event);
		}
    }
	
	TaskResult exuteOperationTask(ClusterOperationTask operationTask) {
		IClusterOperator operator = name2operator.get(operationTask.getOperationName());
		if (null == operator) {
			throw new NullPointerException("No cluster operator for operation " + operationTask.getOperationName());
		}
		
		TaskResult operateResult = operator.operate(operationTask, clusterState);
		return operateResult;
	}
	
	private ClusterOperationTask buildOperationTask(Task task) {
		assert (task != null);
	
		JSONObject operationTaskParamJson = task.getParamJson();
		operationTaskParamJson.remove(ClusterOperationTask.OPERATION_TASK_FLAG_ATTR);
		
		OperateType operateType = OperateType.GET;
		if (operationTaskParamJson.containsKey(ClusterOperationTask.OPERATE_TYPE)) {
			operateType = OperateType.parse(operationTaskParamJson.remove(ClusterOperationTask.OPERATE_TYPE).toString());
		}
		
		String operationName = (String) operationTaskParamJson.remove(ClusterOperationTask.OPERATION_NAME_ATTR);
		return new ClusterOperationTask(
				operateType,
				task.getId(),
				operationName,
				operationTaskParamJson
			);
	}
	
	private boolean isOperationTask(Task task) {
		assert (task != null);
		
		boolean isOperationTask = true;
		Object isOperationTaskValueObj = task.getParamJson().get(ClusterOperationTask.OPERATION_TASK_FLAG_ATTR);
		if (null == isOperationTaskValueObj) {
			return false;
		} else {
			if (isOperationTaskValueObj instanceof Boolean) {
				isOperationTask &= (Boolean) isOperationTaskValueObj;
			} else if (isOperationTaskValueObj instanceof String) {
				isOperationTask &= "yes".equals(isOperationTaskValueObj) || "true".equals(isOperationTaskValueObj);
			} else if (isOperationTaskValueObj instanceof Number) {
				isOperationTask &= ((Number) isOperationTaskValueObj).intValue() > 0;
			} else {
				isOperationTask &= false;
			}
		}
		
		Object operationName = task.getParamJson().get(ClusterOperationTask.OPERATION_NAME_ATTR);
		if (operationName == null) {
			return false;
		}
		
		return isOperationTask;
	}
	
	public void registerOperator(IClusterOperator operator) {
		this.registerOperator(operator, false);
	}
	
	public void registerOperator(IClusterOperator operator, boolean overWrite) {
		if (null == operator) {
			throw new NullPointerException("operator");
		}
		
		LOG.info("registering operator " + operator.getClass().getName());

		if (overWrite) {
			name2operator.put(operator.getOperationName(), operator);
		} else {
			if (name2operator.containsKey(operator.getOperationName())) {
				throw new DuplicateOperatorException(operator);
			} else {
				name2operator.put(operator.getOperationName(), operator);
			}
		}
	}
	
	private void registerBuildinOperators() {
		registerOperator(new ClusterSimpleStatusOperator(), true);
		registerOperator(new ClusterWeightOperator(), true);
		registerOperator(new ClusterNodesStatusOperator(), true);
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	private void registerCustomerOperators() throws Exception {
		String[] customerOperatorClassStrs = ClusterSettings.getCustomerizedOperators();
		Collection<IClusterOperator> customerOperators = (Collection<IClusterOperator>) ObjectsFactory.createObjects(Arrays.asList(customerOperatorClassStrs), IClusterOperator.class);
		for (IClusterOperator operator : customerOperators) {
			registerOperator(operator, true);
		}
	}
}
