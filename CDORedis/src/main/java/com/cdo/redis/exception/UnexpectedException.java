package com.cdo.redis.exception;

public class UnexpectedException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8940737926646889479L;

	public UnexpectedException(String err) {
        super(err);
    }

    public UnexpectedException (Exception e) {    	
        super(e);
    }
}
