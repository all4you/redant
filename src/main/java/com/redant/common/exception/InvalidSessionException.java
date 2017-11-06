package com.redant.common.exception;

/**
 * 非法session
 * @author gris.wang
 * @create 2017-10-20
 */
public class InvalidSessionException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public InvalidSessionException(String s) {
        super(s);
    }

}
