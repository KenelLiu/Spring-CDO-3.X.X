package com.cdo.redis.exception;

public class RedisException extends RuntimeException {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 2216496941288991948L;

	public RedisException(String msg) {
	    super(msg);
	  }

	  public RedisException(String msg, Throwable th) {
	    super(msg, th);
	  }

	  public RedisException(Throwable th) {
	    super(th);
	  }
}
