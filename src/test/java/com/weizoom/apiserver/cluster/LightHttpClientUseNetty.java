/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-6-8
 */
package com.weizoom.apiserver.cluster;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.queue.BlockingReadHandler;

/**
 * <code>LightHttpClient</code>是使用Netty实现的简易的Http请求客户端<br />
 * 
 * @author wuyadong
 *
 */
public class LightHttpClientUseNetty {
	
	private static ClientBootstrap initClientBoostrap(BlockingReadHandler<HttpResponse> reader) {
		ClientBootstrap clientBootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()
				));
		clientBootstrap.setPipelineFactory(createPipelineFactory(reader));
		return clientBootstrap;
	}
	/**
	 * Http请求的Get方式
	 * @param uri
	 * @return
	 * @throws exception
	 */
	public static HttpResponse send(String ip, int port, String uri) throws Exception {
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
		
		BlockingReadHandler<HttpResponse> reader = new BlockingReadHandler<HttpResponse>();
		ClientBootstrap clientBootstrap = initClientBoostrap(reader);
		ChannelFuture channelFuture = clientBootstrap.connect(new InetSocketAddress(ip, port));
		HttpResponse httpResponse = null;
		channelFuture.awaitUninterruptibly();
		if (channelFuture.isSuccess()) {
			channelFuture.getChannel().write(httpRequest);
			httpResponse = reader.read();
			channelFuture.getChannel().close().awaitUninterruptibly();
		}
		
		clientBootstrap.releaseExternalResources();
		return httpResponse;
	}
	
	private static ChannelPipelineFactory createPipelineFactory(final BlockingReadHandler<HttpResponse> reader) {
		ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
			
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("request-encoder", new HttpRequestEncoder());
				pipeline.addLast("response-decoder", new HttpResponseDecoder());
				pipeline.addLast("block-handler", reader);
				return pipeline;
			}
		};
		return pipelineFactory;
	}
}
