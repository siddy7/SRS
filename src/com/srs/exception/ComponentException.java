package com.srs.exception;

import com.srs.exception.ErrorCode.ComponentErrorCode;

public class ComponentException extends Exception {

	ComponentErrorCode errorCode;

	public ComponentException(ComponentErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public ComponentException(ComponentErrorCode errorCode, Throwable t) {
		super(t);
		this.errorCode = errorCode;
	}

	private static final long serialVersionUID = 1L;

	public ComponentErrorCode getErrorCode() {
		return errorCode;
	}

}
