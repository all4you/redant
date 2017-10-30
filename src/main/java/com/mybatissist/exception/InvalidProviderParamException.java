package com.mybatissist.exception;

/**
 * InvalidProviderParamException
 * @author gris.wang
 * @create 2017-10-20
 */
public class InvalidProviderParamException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public InvalidProviderParamException(String s) {
        super(s);
    }

}
