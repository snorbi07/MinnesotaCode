package com.norbertsram.minnesota;

public class MinnesotaException extends RuntimeException {

	private static final long serialVersionUID = -67562070893270748L;
	
	public MinnesotaException(String message) {
		super(message);
	}
	
	public MinnesotaException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
