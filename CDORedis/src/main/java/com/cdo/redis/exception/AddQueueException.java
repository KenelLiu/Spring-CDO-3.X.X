package com.cdo.redis.exception;

public class AddQueueException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8940737926646889479L;

	public AddQueueException(String err) {
        super(err);
    }

    public AddQueueException (Exception e) {    	
        super(e);
    }
}
