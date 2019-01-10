package com.redant.core.common.exception;

/**
 * 非法session
 * @author gris.wang
 * @date 2017-10-20
 */
public class InvalidSessionException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public InvalidSessionException(String s) {
        super(s);
    }

}
