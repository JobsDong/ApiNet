/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-10
 */
package com.weizoom.apiserver.cluster.handler.codec;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;

/**
 * <code>Compressdecoder</code>是将已压缩的<code>ChannelBuffer</code>解压
 * 现在使用的是Zlib的GZIP压缩算法
 * @author wuyadong
 *
 */
public class CompressDecoder extends ZlibDecoder {
	final static private Logger LOG = Logger.getLogger(CompressDecoder.class);
	
	public CompressDecoder() {
		super(ZlibWrapper.ZLIB);
	}
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		LOG.info("Compress decoder receive : " + msg);
		return super.decode(ctx, channel, msg);
	}
}
