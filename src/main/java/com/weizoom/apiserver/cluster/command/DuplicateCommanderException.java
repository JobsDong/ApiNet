/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2013-5-28
 */
package com.weizoom.apiserver.cluster.command;

/**
 * 
 * @author wuyadong
 *
 */
class DuplicateCommanderException extends RuntimeException {
	private static final long serialVersionUID = -1389121609055607601L;
	
	public DuplicateCommanderException(IClusterCommander commander) {
		super("Duplicate commander : " + commander.getCommandName());
	}
}
