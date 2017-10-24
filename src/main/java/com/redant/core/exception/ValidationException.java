package com.redant.core.exception;

/**
 * ValidationException
 * @author gris.wang
 * @create 2017-10-20
 */
public class ValidationException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ValidationException(String s) {
        super(s);
    }

	public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }


}
