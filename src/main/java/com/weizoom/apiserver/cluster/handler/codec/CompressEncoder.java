/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-10
 */
package com.weizoom.apiserver.cluster.handler.codec;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;


public class CompressEncoder extends ZlibEncoder {
	final static private Logger LOG = Logger.getLogger(CompressEncoder.class);
	
	public CompressEncoder() {
		super(ZlibWrapper.ZLIB);
	}
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		LOG.info("compress encoder get an object: " + msg);
		return super.encode(ctx, channel, msg);
	}
}
