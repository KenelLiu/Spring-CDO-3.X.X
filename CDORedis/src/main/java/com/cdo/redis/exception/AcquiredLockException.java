package com.cdo.redis.exception;

public class AcquiredLockException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8940737926646889479L;

	public AcquiredLockException(String err) {
        super(err);
    }

    public AcquiredLockException (Exception e) {    	
        super(e);
    }
}
