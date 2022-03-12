package com.cdo.redis.exception;

public class OutOfBoundRequestsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4611787367014523645L;

	public OutOfBoundRequestsException(String err) {
        super(err);
    }

    public OutOfBoundRequestsException (Exception e) {    	
        super(e);
    }
}
