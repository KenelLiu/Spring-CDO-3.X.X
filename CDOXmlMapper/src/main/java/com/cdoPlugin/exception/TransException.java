package com.cdoPlugin.exception;

public class TransException extends Exception {

	private static final long serialVersionUID = -4827973186515205031L;
	public TransException(String message) {
        super(message);
    }
 
    public TransException(Throwable cause) {
        super(cause);
    }
    public TransException(String message,Throwable cause) {
        super(message,cause);
    }

    
}
